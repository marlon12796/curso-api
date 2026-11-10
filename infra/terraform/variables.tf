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

variable "ami_id" {
  description = "AMI aprobada para la EC2"
  type        = string
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

variable "cloudflare_account_id" {
  description = "ID de la cuenta de Cloudflare"
  type        = string
}

variable "cloudflare_zone_id" {
  description = "ID de la zona DNS de Cloudflare"
  type        = string
}

variable "cloudflare_hostname" {
  description = "Hostname público de la API, por ejemplo api.example.com"
  type        = string
}


