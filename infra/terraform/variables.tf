variable "aws_region" {
  description = "Región AWS donde se crearán los recursos"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Nombre base de los recursos"
  type        = string
  default     = "curso-api"
}

variable "instance_type" {
  description = "Tipo de instancia EC2"
  type        = string
  default     = "t3.micro"
}

variable "key_name" {
  description = "Nombre del Key Pair existente para conectarse por SSH"
  type        = string
}

variable "admin_cidr" {
  description = "IP o rango autorizado para SSH, por ejemplo 203.0.113.10/32"
  type        = string
}

variable "app_port" {
  description = "Puerto temporal de la API para la práctica inicial"
  type        = number
  default     = 3050
}

variable "db_url" {
  description = "URL JDBC de PostgreSQL"
  type        = string
}

variable "db_username" {
  description = "Usuario de PostgreSQL"
  type        = string
}

variable "jwt_secret" {
  description = "Secreto utilizado para firmar los tokens JWT"
  type        = string
  sensitive   = true
}

variable "jwt_expiration_ms" {
  description = "Duracion del token JWT en milisegundos"
  type        = string
  default     = "3600000"
}

