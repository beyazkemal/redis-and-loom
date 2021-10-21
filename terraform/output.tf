output "redis_cluster_id" {
  description = "ID of the Redis cluster"
  value       = aws_elasticache_cluster.super_redis_cluster.id
}

output "redis_endpoint" {
  description = "Public endpoint address of the Redis instance"
  value       = aws_elasticache_cluster.super_redis_cluster.cache_nodes.0.address
}

output "mysql_cluster_id" {
  description = "ID of the MySQL instance"
  value       = aws_db_instance.super_mysql.id
}

output "mysql_endpoint" {
  description = "Public endpoint address of the MySQL instance"
  value       = aws_db_instance.super_mysql.endpoint
}