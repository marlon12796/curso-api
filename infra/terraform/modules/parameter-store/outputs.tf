output "parameter_names" {
  description = "Nombres de los parametros SSM creados"

  value = {
    for key, parameter in aws_ssm_parameter.app : key => parameter.name
  }
}

output "parameter_arns" {
  description = "ARN de los parametros SSM creados"

  value = {
    for key, parameter in aws_ssm_parameter.app : key => parameter.arn
  }
}
