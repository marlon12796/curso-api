output "tunnel_id" {
  description = "ID del Cloudflare Tunnel"
  value       = cloudflare_zero_trust_tunnel_cloudflared.api.id
}

output "hostname" {
  description = "Hostname público de la API"
  value       = var.hostname
}
