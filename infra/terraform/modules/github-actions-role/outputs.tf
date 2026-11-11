output "role_arn" {
  description = "ARN del IAM Role para GitHub Actions"
  value       = aws_iam_role.github_actions.arn
}
