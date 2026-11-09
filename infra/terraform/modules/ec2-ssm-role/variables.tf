variable "name" {
  description = "Nombre base del IAM Role y del Instance Profile"
  type        = string
}

variable "parameter_arns" {
  description = "ARN de los parametros SSM que EC2 puede leer"
  type        = map(string)
}

variable "ecr_repository_arn" {
  description = "ARN del repositorio ECR que EC2 puede descargar"
  type        = string
}

variable "tags" {
  description = "Tags adicionales de los recursos IAM"
  type        = map(string)
  default     = {}
}
