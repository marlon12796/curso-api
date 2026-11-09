output "role_name" {
  description = "Nombre del IAM Role asociado a EC2"
  value       = aws_iam_role.this.name
}

output "role_arn" {
  description = "ARN del IAM Role asociado a EC2"
  value       = aws_iam_role.this.arn
}

output "instance_profile_name" {
  description = "Nombre del Instance Profile para EC2"
  value       = aws_iam_instance_profile.this.name
}
