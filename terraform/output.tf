output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_elasticache_cluster.super_redis_cluster.id
}

output "instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_elasticache_cluster.super_redis_cluster.cache_nodes.0.address
}