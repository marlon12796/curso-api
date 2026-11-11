variable "name" {
  description = "Nombre base de los recursos IAM de GitHub Actions"
  type        = string
}

variable "github_repository" {
  description = "Repositorio GitHub en formato owner/repository"
  type        = string
}

variable "github_branch" {
  description = "Rama autorizada para asumir el role"
  type        = string
  default     = "master"
}

variable "ecr_repository_arn" {
  description = "ARN del repositorio ECR"
  type        = string
}

variable "instance_arn" {
  description = "ARN de la instancia EC2 de producción"
  type        = string
}

variable "tags" {
  description = "Tags comunes de los recursos"
  type        = map(string)
  default     = {}
}
