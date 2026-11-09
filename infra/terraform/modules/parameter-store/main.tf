locals {
  db_parameters = {
    DB_URL = {
      description = "URL de PostgreSQL para ${var.project_name}"
      type        = "String"
      value       = var.db_url
    }

    DB_USERNAME = {
      description = "Usuario de PostgreSQL para ${var.project_name}"
      type        = "String"
      value       = var.db_username
    }

    DB_PASSWORD = {
      description = "Password de PostgreSQL para ${var.project_name}"
      type        = "SecureString"
      value       = var.db_password
    }

    JWT_SECRET = {
      description = "Secreto JWT para ${var.project_name}"
      type        = "SecureString"
      value       = var.jwt_secret
    }

    JWT_EXPIRATION_MS = {
      description = "Expiración del JWT para ${var.project_name}"
      type        = "String"
      value       = var.jwt_expiration_ms
    }
  }
}

resource "aws_ssm_parameter" "app" {
  for_each    = local.db_parameters
  name        = "/${var.project_name}/prod/${each.key}"
  type        = each.value.type
  description = each.value.description
  value       = each.value.value
  tags = {
    Project     = var.project_name
    Environment = "prod"
  }
}