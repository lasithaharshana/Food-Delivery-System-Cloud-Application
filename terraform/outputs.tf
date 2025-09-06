output "master_public_ip" {
  description = "Public IP of the master node"
  value       = aws_instance.k8s_master.public_ip
}

output "master_private_ip" {
  description = "Private IP of the master node"
  value       = aws_instance.k8s_master.private_ip
}

output "selected_ami_id" {
  description = "AMI ID used for the instances"
  value       = local.selected_ami
}

output "aws_region" {
  description = "AWS region used for deployment"
  value       = var.aws_region
}

output "worker_public_ips" {
  description = "Public IPs of worker nodes"
  value       = aws_instance.k8s_worker[*].public_ip
}

output "worker_private_ips" {
  description = "Private IPs of worker nodes"
  value       = aws_instance.k8s_worker[*].private_ip
}

output "vpc_id" {
  description = "ID of the VPC"
  value       = aws_vpc.k8s_vpc.id
}

output "subnet_id" {
  description = "ID of the public subnet"
  value       = aws_subnet.k8s_public_subnet.id
}

output "security_group_id" {
  description = "ID of the security group"
  value       = aws_security_group.k8s_sg.id
}

output "ssh_command" {
  description = "SSH command to connect to master node"
  value       = "ssh -i ~/.ssh/${var.cluster_name}-key ubuntu@${aws_instance.k8s_master.public_ip}"
}

output "kubeconfig_command" {
  description = "Command to get kubeconfig from master node"
  value       = "scp -i ~/.ssh/${var.cluster_name}-key ubuntu@${aws_instance.k8s_master.public_ip}:~/.kube/config ~/.kube/config"
}
