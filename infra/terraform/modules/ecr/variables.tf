variable "name" {
  description = "Nombre del repositorio ECR"
  type        = string
}

variable "image_tag_mutability" {
  description = "Indica si una etiqueta puede sobrescribirse"
  type        = string
  default     = "IMMUTABLE"
}

variable "force_delete" {
  description = "Permite eliminar el repositorio aunque tenga imágenes"
  type        = bool
  default     = false
}

variable "scan_on_push" {
  description = "Escanea las imágenes al subirlas"
  type        = bool
  default     = true
}

variable "tags" {
  description = "Tags del repositorio"
  type        = map(string)
  default     = {}
}
