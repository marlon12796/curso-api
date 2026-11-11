data "aws_iam_policy_document" "permissions" {
  statement {
    sid    = "EcrPush"
    effect = "Allow"
    actions = [
      "ecr:BatchCheckLayerAvailability",
      "ecr:CompleteLayerUpload",
      "ecr:InitiateLayerUpload",
      "ecr:PutImage",
      "ecr:UploadLayerPart",
      "ecr:DescribeImages"
    ]
    resources = [var.ecr_repository_arn]
  }

  statement {
    sid       = "EcrAuthentication"
    effect    = "Allow"
    actions   = ["ecr:GetAuthorizationToken"]
    resources = ["*"]
  }

  statement {
    sid    = "SendCommandsToProductionEc2"
    effect = "Allow"
    actions = [
      "ssm:SendCommand"
    ]
    resources = [
      var.instance_arn,
      "arn:${data.aws_partition.current.partition}:ssm:*:${data.aws_caller_identity.current.account_id}:document/AWS-RunShellScript"
    ]
  }

  statement {
    sid    = "ReadCommandResult"
    effect = "Allow"
    actions = [
      "ssm:GetCommandInvocation",
      "ssm:ListCommandInvocations",
      "ssm:ListCommands"
    ]
    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "permissions" {
  name   = "${var.name}-github-actions-permissions"
  role   = aws_iam_role.github_actions.id
  policy = data.aws_iam_policy_document.permissions.json
}
