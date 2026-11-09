output "parameter_names" {
  description = "Nombres de los parametros SSM existentes"

  value = local.parameter_names
}

output "parameter_arns" {
  description = "ARN de los parametros SSM existentes"

  value = local.parameter_arns
}
