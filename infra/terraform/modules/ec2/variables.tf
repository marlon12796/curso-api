variable "name" {
  description = "Nombre de la instancia EC2"
  type        = string
}

variable "ami" {
  description = "AMI que utilizará EC2"
  type        = string
}

variable "instance_type" {
  description = "Tipo de instancia EC2"
  type        = string
}

variable "subnet_id" {
  description = "Subnet donde se desplegará EC2"
  type        = string
}

variable "vpc_security_group_ids" {
  description = "Security Groups asociados a EC2"
  type        = list(string)
}

variable "key_name" {
  description = "Nombre del Key Pair de EC2"
  type        = string
}

variable "iam_instance_profile" {
  description = "Instance Profile asociado a EC2"
  type        = string
}

variable "user_data" {
  description = "Script de inicializacion de la instancia"
  type        = string
}

variable "root_volume_size" {
  description = "Tamaño del disco raíz en GB"
  type        = number
  default     = 20
}

variable "tags" {
  description = "Tags adicionales para la instancia"
  type        = map(string)
  default     = {}
}
