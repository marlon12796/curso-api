data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_ssm_parameter" "amazon_linux_2023" {
  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"
}

resource "random_password" "db" {
  length  = 32
  special = false
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

  ingress {
    description = "Puerto temporal de la API"
    from_port   = var.app_port
    to_port     = var.app_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
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
  ami                    = data.aws_ssm_parameter.amazon_linux_2023.value
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.app.id]
  key_name               = var.key_name
  iam_instance_profile   = module.ec2_ssm_role.instance_profile_name
  user_data = templatefile("${path.module}/modules/ec2/user-data.sh", {
    aws_region               = var.aws_region
    db_url_parameter         = module.parameter_store.parameter_names["DB_URL"]
    db_username_parameter    = module.parameter_store.parameter_names["DB_USERNAME"]
    db_password_parameter    = module.parameter_store.parameter_names["DB_PASSWORD"]
    jwt_secret_parameter     = module.parameter_store.parameter_names["JWT_SECRET"]
    jwt_expiration_parameter = module.parameter_store.parameter_names["JWT_EXPIRATION_MS"]
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

  project_name      = var.project_name
  db_url            = var.db_url
  db_username       = var.db_username
  db_password       = random_password.db.result
  jwt_secret        = var.jwt_secret
  jwt_expiration_ms = var.jwt_expiration_ms
}

module "ec2_ssm_role" {
  source = "./modules/ec2-ssm-role"

  name           = var.project_name
  parameter_arns = module.parameter_store.parameter_arns

  tags = {
    Project = var.project_name
  }
}
