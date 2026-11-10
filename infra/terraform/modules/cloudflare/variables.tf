variable "account_id" {
  description = "ID de la cuenta de Cloudflare"
  type        = string
}

variable "zone_id" {
  description = "ID de la zona DNS de Cloudflare"
  type        = string
}

variable "hostname" {
  description = "Hostname público del servicio"
  type        = string
}

variable "service" {
  description = "Servicio interno accesible desde cloudflared"
  type        = string
  default     = "http://api:3050"
}

variable "name" {
  description = "Nombre del túnel"
  type        = string
}
