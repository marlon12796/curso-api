output "ec2_public_ip" {
  description = "IP pública de la instancia EC2"
  value       = module.ec2.public_ip
}

output "api_url" {
  description = "URL temporal de la API"
  value       = "http://${module.ec2.public_ip}:${var.app_port}"
}

output "ecr_repository_url" {
  description = "URL del repositorio ECR de la API"
  value       = module.ecr.repository_url
}
