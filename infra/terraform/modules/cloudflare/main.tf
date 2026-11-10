
resource "cloudflare_zero_trust_tunnel_cloudflared" "api" {
  account_id = var.account_id
  name       = var.name
  config_src = "cloudflare"
}
resource "cloudflare_dns_record" "api" {
  zone_id = var.zone_id
  name    = var.hostname
  type    = "CNAME"
  content = "${cloudflare_zero_trust_tunnel_cloudflared.api.id}.cfargotunnel.com"
  ttl     = 1
  proxied = true
}
resource "cloudflare_zero_trust_tunnel_cloudflared_config" "api" {
  account_id = var.account_id
  tunnel_id  = cloudflare_zero_trust_tunnel_cloudflared.api.id
  config = {
    ingress = [{
      hostname = var.hostname
      service  = var.service
      }, {
      service = "http_status:404"
    }]
  }
}
