data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_caller_identity" "current" {}

data "aws_partition" "current" {}

module "cloudflare" {
  source = "./modules/cloudflare"

  account_id = var.cloudflare_account_id
  zone_id    = var.cloudflare_zone_id
  hostname   = var.cloudflare_hostname
  service    = "http://api:3050"

  name = "${var.project_name}-production"
}

resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name    = "${var.project_name}-vpc"
    Project = var.project_name
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = data.aws_availability_zones.available.names[0]
  map_public_ip_on_launch = true

  tags = {
    Name    = "${var.project_name}-public-subnet"
    Project = var.project_name
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name    = "${var.project_name}-igw"
    Project = var.project_name
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name    = "${var.project_name}-public-routes"
    Project = var.project_name
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "app" {
  name        = "${var.project_name}-sg"
  description = "Acceso controlado para la API Curso API"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH solo desde la IP administrativa"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.admin_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name    = "${var.project_name}-sg"
    Project = var.project_name
  }
}

module "ec2" {
  source = "./modules/ec2"

  name                   = "${var.project_name}-ec2"
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.app.id]
  key_name               = var.key_name
  iam_instance_profile   = module.ec2_ssm_role.instance_profile_name
  user_data = templatefile("${path.module}/modules/ec2/user-data.sh", {
    aws_region                  = var.aws_region
    db_url_parameter            = module.parameter_store.parameter_names["DB_URL"]
    db_username_parameter       = module.parameter_store.parameter_names["DB_USERNAME"]
    db_password_parameter       = module.parameter_store.parameter_names["DB_PASSWORD"]
    jwt_secret_parameter        = module.parameter_store.parameter_names["JWT_SECRET"]
    jwt_expiration_parameter    = module.parameter_store.parameter_names["JWT_EXPIRATION_MS"]
    cloudflare_tunnel_parameter = module.parameter_store.parameter_names["CLOUDFLARE_TUNNEL_TOKEN"]
  })

  tags = {
    Project = var.project_name
  }
}

module "ecr" {
  source = "./modules/ecr"

  name         = var.project_name
  force_delete = true

  tags = {
    Project = var.project_name
  }
}

module "parameter_store" {
  source = "./modules/parameter-store"

  project_name = var.project_name
}

module "ec2_ssm_role" {
  source = "./modules/ec2-ssm-role"

  name               = var.project_name
  parameter_arns     = module.parameter_store.parameter_arns
  ecr_repository_arn = module.ecr.repository_arn

  tags = {
    Project = var.project_name
  }
}

module "github_actions_role" {
  source = "./modules/github-actions-role"

  name               = var.project_name
  github_repository  = "marlon12796/curso-api"
  github_branch      = "master"
  ecr_repository_arn = module.ecr.repository_arn
  instance_arn       = "arn:${data.aws_partition.current.partition}:ec2:${var.aws_region}:${data.aws_caller_identity.current.account_id}:instance/${module.ec2.id}"

  tags = {
    Project = var.project_name
  }
}
