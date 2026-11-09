variable "name" {
  description = "Nombre base del IAM Role y del Instance Profile"
  type        = string
}

variable "parameter_arns" {
  description = "ARN de los parametros SSM que EC2 puede leer"
  type        = map(string)
}

variable "tags" {
  description = "Tags adicionales de los recursos IAM"
  type        = map(string)
  default     = {}
}
