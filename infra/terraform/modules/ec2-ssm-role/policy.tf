data "aws_iam_policy_document" "ssm_read" {
  statement {
    effect = "Allow"
    actions = [
      "ssm:GetParameter"
    ]
    resources = values(var.parameter_arns)
  }
}
resource "aws_iam_role_policy" "ssm_read" {
  name   = "${var.name}-ssm-read"
  role = aws_iam_role.this.id
  policy = data.aws_iam_policy_document.ssm_read.json
}
