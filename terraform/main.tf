terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }

  required_version = ">= 0.14.9"
}

provider "aws" {
  profile = "default"
  region  = "us-west-2"
}

variable "aws_region" {
  type    = string
  default = "us-west-2"
}

# Redis
resource "aws_elasticache_cluster" "super_redis_cluster" {
  cluster_id           = "super-redis-cluster"
  engine               = "redis"
  node_type            = "cache.m4.large" // 2 vCPU, 6 GB RAM
  num_cache_nodes      = 1
  parameter_group_name = "default.redis6.x"
  engine_version       = "6.x"
  port                 = 6379
  az_mode              = "single-az"
}

# MySQL
resource "aws_db_instance" "super_mysql" {
  instance_class       = "db.m6g.large" // 2 vCPU, 8 GB RAM
  engine               = "mysql"
  engine_version       = "8.0.23"
  name                 = "super_mysql"
  username             = "admin"
  password             = "SXIToq70IBqv9Hmz"
  parameter_group_name = "default.mysql8.0"
  publicly_accessible  = true
  multi_az             = false
  allocated_storage    = 50
}

# IAM Policy for Task Execution Role
data "aws_iam_policy_document" "ecs_tasks_execution_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

# Task Execution Role
resource "aws_iam_role" "ecs_tasks_execution_role" {
  name               = "ecs-task-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_tasks_execution_role.json
}

# Task Execution Role Policy Attachment
resource "aws_iam_role_policy_attachment" "ecs_tasks_execution_role" {
  role       = aws_iam_role.ecs_tasks_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Cloudwatch for ECS Tasks
resource "aws_cloudwatch_log_group" "redis_demo_super_log_group" {
  name = "/ecs/redis-demo-super-cluster"
}

data "aws_ecr_repository" "redis_demo_super_ecr" {
  name = "redis-and-loom"
}

# ECS Cluster
resource "aws_ecs_cluster" "redis_demo_super_cluster" {
  name = "redis-demo-super-cluster"
}

# Task Definition
resource "aws_ecs_task_definition" "redis_demo_super_task" {
  family                = "redis-demo-super-task"
  # Naming our first task
  container_definitions = <<DEFINITION
  [
    {
      "name": "redis-demo-super-task",
      "image": "${data.aws_ecr_repository.redis_demo_super_ecr.repository_url}:latest",
      "essential": true,
      "memory": 8192,
      "cpu": 4096,
      "environment": [
                  {
                      "name": "REDIS_HOST",
                      "value": "${aws_elasticache_cluster.super_redis_cluster.cache_nodes.0.address}"
                  },
                  {
                      "name": "MYSQL_HOST",
                      "value": "${aws_db_instance.super_mysql.endpoint}"
                  },
                  {
                      "name": "MYSQL_USERNAME",
                      "value": "${aws_db_instance.super_mysql.username}"
                  },
                  {
                      "name": "MYSQL_PASSWORD",
                      "value": "${aws_db_instance.super_mysql.password}"
                  }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.redis_demo_super_log_group.name}",
          "awslogs-stream-prefix": "${aws_cloudwatch_log_group.redis_demo_super_log_group.name}",
          "awslogs-region": "${var.aws_region}"
        }
      }
    }
  ]
  DEFINITION

  # Stating that we are using ECS Fargate
  requires_compatibilities = ["FARGATE"]
  # Using awsvpc as our network mode as this is required for Fargate
  network_mode             = "awsvpc"
  memory                   = 8192
  cpu                      = 4096
  execution_role_arn       = aws_iam_role.ecs_tasks_execution_role.arn
}

