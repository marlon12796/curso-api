data "aws_iam_policy_document" "ec2_assume_role" {
  statement {
    effect = "Allow"
    principals {
      type = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
   actions = ["sts:AssumeRole"]
  }
}
resource "aws_iam_role" "this" {
   name               = "${var.name}-role"
   assume_role_policy = data.aws_iam_policy_document.ec2_assume_role.json
   tags = merge(
    var.tags,{
        Name = "${var.name}-role"
    }
   )
}
resource "aws_iam_instance_profile" "this" {
  name = "${var.name}-profile"
  role = aws_iam_role.this.name
  tags = merge(
    var.tags,
    {
      Name = "${var.name}-profile"
    }
  )
}
