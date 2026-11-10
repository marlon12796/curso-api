data "aws_caller_identity" "current" {}

data "aws_partition" "current" {}

data "aws_region" "current" {}

locals {
  parameter_names = {
    DB_URL                  = "/${var.project_name}/prod/DB_URL"
    DB_USERNAME             = "/${var.project_name}/prod/DB_USERNAME"
    DB_PASSWORD             = "/${var.project_name}/prod/DB_PASSWORD"
    JWT_SECRET              = "/${var.project_name}/prod/JWT_SECRET"
    JWT_EXPIRATION_MS       = "/${var.project_name}/prod/JWT_EXPIRATION_MS"
    CLOUDFLARE_TUNNEL_TOKEN = "/${var.project_name}/prod/CLOUDFLARE_TUNNEL_TOKEN"
  }

  parameter_arns = {
    for key, name in local.parameter_names : key => format(
      "arn:%s:ssm:%s:%s:parameter%s",
      data.aws_partition.current.partition,
      data.aws_region.current.region,
      data.aws_caller_identity.current.account_id,
      name
    )
  }
}
