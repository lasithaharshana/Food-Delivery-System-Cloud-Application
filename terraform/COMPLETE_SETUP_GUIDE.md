# Complete Terraform + Kubernetes Setup Guide

This guide provides everything you need to deploy a Kubernetes cluster on AWS and set up your Food Delivery System.

## 📋 Prerequisites

- AWS Account with temp user credentials
- Windows machine with PowerShell
- SSH key pair for EC2 access

## 🚀 Quick Start

1. **Install Tools** (run as Administrator):
   ```cmd
   # Install Chocolatey
   @powershell -NoProfile -InputFormat None -ExecutionPolicy Bypass -Command "iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))"
   
   # Install required tools
   choco install terraform awscli kubernetes-cli -y
   ```

2. **Configure AWS**:
   ```cmd
   aws configure
   # AWS Access Key ID: [your-temp-user-access-key]
   # AWS Secret Access Key: [your-temp-user-secret-key]
   # Default region: us-east-1
   # Default output format: json
   ```

3. **Generate SSH Keys**:
   ```cmd
   ssh-keygen -t rsa -b 4096 -f "%USERPROFILE%\.ssh\food-delivery-k8s-key" -N ""
   ```

4. **Deploy Infrastructure**:
   ```cmd
   cd terraform
   terraform init
   terraform plan
   terraform apply
   ```

5. **Get Master IP**:
   ```cmd
   terraform output master_public_ip
   ```

## 🔧 Manual Kubernetes Setup

### Step 1: Connect to Master Node
```cmd
ssh -i "%USERPROFILE%\.ssh\food-delivery-k8s-key" ubuntu@[MASTER_IP]
```

### Step 2: Install Kubernetes (Master Node Script)
Copy and paste this entire script on the master node:

```bash
#!/bin/bash
set -e

echo "Installing Kubernetes Master Node..."

# Update system
sudo apt-get update -y && sudo apt-get upgrade -y

# Install Docker
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Configure Docker
sudo mkdir -p /etc/docker
cat <<EOF | sudo tee /etc/docker/daemon.json
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {"max-size": "100m"},
  "storage-driver": "overlay2"
}
EOF

sudo systemctl enable docker
sudo systemctl daemon-reload
sudo systemctl restart docker
sudo usermod -aG docker ubuntu

# Install Kubernetes
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.28/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.28/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt-get update -y
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl

# Disable swap
sudo swapoff -a
sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab
sudo systemctl enable kubelet

echo "✅ Kubernetes components installed successfully!"
```

### Step 3: Initialize Kubernetes Cluster
```bash
# Get private IP
PRIVATE_IP=$(hostname -I | awk '{print $1}')
echo "Using IP: $PRIVATE_IP"

# Initialize cluster
sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address=$PRIVATE_IP

# Setup kubeconfig
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# Install Flannel CNI
kubectl apply -f https://raw.githubusercontent.com/flannel-io/flannel/master/Documentation/kube-flannel.yml

# Allow pods on master node
kubectl taint nodes --all node-role.kubernetes.io/control-plane- --ignore-not-found=true

# Create namespace
kubectl create namespace food-delivery

# Check status
kubectl get nodes
kubectl get pods --all-namespaces

echo "✅ Kubernetes cluster ready!"
```

### Step 4: Copy Kubeconfig to Windows
Exit SSH and run from Windows:
```cmd
mkdir "%USERPROFILE%\.kube"
scp -i "%USERPROFILE%\.ssh\food-delivery-k8s-key" ubuntu@[MASTER_IP]:~/.kube/config "%USERPROFILE%\.kube\config"

# Test kubectl from Windows
kubectl get nodes
```

## 📦 Deploy Applications

### Step 1: Update Docker Images
```cmd
# Update your k8s manifests with your Docker Hub username
# In each k8s/*.yaml file, change:
# image: lasitha6646/food-delivery-frontend:latest
# to:
# image: your-dockerhub-username/food-delivery-frontend:latest
```

### Step 2: Deploy to Kubernetes
```cmd
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmaps.yaml  
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/persistent-volumes.yaml
kubectl apply -f k8s/mysql-deployments.yaml

# Wait for MySQL to be ready
kubectl rollout status deployment/mysql-auth -n food-delivery
kubectl rollout status deployment/mysql-food -n food-delivery
kubectl rollout status deployment/mysql-order -n food-delivery

# Deploy services
kubectl apply -f k8s/authservice.yaml
kubectl apply -f k8s/foodservice.yaml
kubectl apply -f k8s/orderservice.yaml
kubectl apply -f k8s/apigateway.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

### Step 3: Check Deployment
```cmd
kubectl get pods -n food-delivery
kubectl get services -n food-delivery
kubectl get ingress -n food-delivery
```

### Step 4: Access Application
- Frontend: `http://[MASTER_IP]:30080`
- API Gateway: `http://[MASTER_IP]:30081`

## 🛠️ Terraform Configuration Files

### main.tf
The main Terraform configuration creates:
- VPC with public subnet
- Security group with required ports
- EC2 instances (1 master + 2 workers)
- SSH key pair

### variables.tf
Configurable parameters:
- `aws_region` (default: us-east-1)
- `cluster_name` (default: food-delivery-k8s)
- `master_instance_type` (default: t3.medium)
- `worker_instance_type` (default: t3.small)
- `worker_count` (default: 2)

### terraform.tfvars
Your actual configuration:
```hcl
aws_region = "us-east-1"
cluster_name = "food-delivery-k8s"
environment = "production"
master_instance_type = "t3.medium"
worker_instance_type = "t3.small"
worker_count = 2
public_key_path = "C:/Users/MSI/.ssh/food-delivery-k8s-key.pub"
```

## 💰 Cost Estimation
- Master (t3.medium): ~$30/month
- 2x Workers (t3.small): ~$30/month
- **Total: ~$60/month**

## 🔍 Troubleshooting

### SSH Connection Issues
```cmd
# Fix SSH key permissions
icacls "%USERPROFILE%\.ssh\food-delivery-k8s-key" /inheritance:r
icacls "%USERPROFILE%\.ssh\food-delivery-k8s-key" /grant:r "%USERNAME%:(R)"

# Test connection with verbose output
ssh -i "%USERPROFILE%\.ssh\food-delivery-k8s-key" -v ubuntu@[MASTER_IP]
```

### Kubernetes Issues
```bash
# Check cluster status
kubectl cluster-info
kubectl get nodes -o wide

# Check pod logs
kubectl logs -l app=frontend -n food-delivery
kubectl describe pod [POD_NAME] -n food-delivery

# Restart services
kubectl rollout restart deployment/frontend -n food-delivery
```

### Common Commands
```bash
# Scale deployment
kubectl scale deployment frontend --replicas=3 -n food-delivery

# Port forward for local access
kubectl port-forward svc/frontend 8080:80 -n food-delivery

# Get pod shell
kubectl exec -it [POD_NAME] -n food-delivery -- /bin/bash

# View logs
kubectl logs -f [POD_NAME] -n food-delivery
```

## 🗑️ Cleanup
```cmd
# Destroy all AWS resources
terraform destroy
```

## 📝 GitHub Actions Integration

To set up automatic deployment, add these secrets to your GitHub repository:

| Secret Name | Value |
|-------------|--------|
| `DOCKERHUB_USERNAME` | your-dockerhub-username |
| `DOCKERHUB_TOKEN` | your-dockerhub-token |
| `AWS_ACCESS_KEY_ID` | your-aws-access-key |
| `AWS_SECRET_ACCESS_KEY` | your-aws-secret-key |
| `AWS_REGION` | us-east-1 |
| `K8S_MASTER_IP` | [MASTER_PUBLIC_IP] |
| `SSH_PRIVATE_KEY` | [SSH_PRIVATE_KEY_CONTENT] |

## 🎯 Single Node Setup (Cost Saving)

For development, you can use just one node:
```hcl
# In terraform.tfvars
master_instance_type = "t3.large"
worker_count = 0
```

This reduces cost to ~$35/month but removes high availability.

---

**This guide contains everything needed for a complete deployment. Follow the steps in order for a successful setup!**
