variable "project_name" {
  description = "Nombre base del proyecto"
  type        = string
}

variable "db_url" {
  description = "URL JDBC de PostgreSQL"
  type        = string
}

variable "db_username" {
  description = "Usuario de PostgreSQL"
  type        = string
}

variable "db_password" {
  description = "Password de PostgreSQL"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Secreto utilizado para firmar los tokens JWT"
  type        = string
  sensitive   = true
}

variable "jwt_expiration_ms" {
  description = "Duracion del token JWT en milisegundos"
  type        = string
}
