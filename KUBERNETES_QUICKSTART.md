# 🚀 Quick Start - Food Delivery System on Kubernetes

Follow these simple steps to get your Food Delivery System running on Kubernetes locally.

## 📋 Prerequisites Check

✅ Docker installed  
✅ kubectl installed  
⚠️ Kubernetes cluster needed

## 🎯 One-Command Setup

Run this single command to set up everything:

```cmd
setup-k8s.bat
```

This script will:
1. ✅ Check all prerequisites
2. 🛠️ Guide you through Kubernetes setup if needed
3. 🐳 Build and push Docker images (optional)
4. 🚀 Deploy to Kubernetes
5. 🌐 Provide access instructions

## 🛠️ Manual Setup (if needed)

### 1. Set up Kubernetes (choose one):

#### Option A: Docker Desktop (Recommended)
1. Open Docker Desktop
2. Settings → Kubernetes → Enable Kubernetes
3. Apply & Restart

#### Option B: Minikube
```cmd
choco install minikube
minikube start --driver=docker
```

### 2. Build and Push Images
```cmd
docker login
build-push-images.bat
```

### 3. Deploy to Kubernetes
```cmd
cd k8s
deploy.bat
```

## 🌐 Access the Application

### Port Forward Method (Always works):
```cmd
# Frontend
kubectl port-forward -n food-delivery service/frontend 3000:80

# API Gateway
kubectl port-forward -n food-delivery service/apigateway 8080:8080
```

Then open:
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080

### Check Status:
```cmd
kubectl get pods -n food-delivery
kubectl get services -n food-delivery
```

## 🔍 Troubleshooting

### Common Issues:

**🐳 "Docker not running"**
- Start Docker Desktop

**⚠️ "No Kubernetes cluster"**  
- Enable Kubernetes in Docker Desktop, or
- Use Minikube: `minikube start --driver=docker`

**📦 "Image pull errors"**
- Run `build-push-images.bat` to build images
- Make sure you're logged into Docker Hub: `docker login`

**🔄 "Pods not ready"**
- Wait a few minutes for services to start
- Check logs: `kubectl logs -n food-delivery deployment/authservice`

### Debug Commands:
```cmd
# Check everything
kubectl get all -n food-delivery

# Check specific pod
kubectl describe pod <pod-name> -n food-delivery

# View logs
kubectl logs -n food-delivery deployment/authservice
kubectl logs -n food-delivery deployment/mysql-auth
```

## 🧹 Cleanup

Remove everything:
```cmd
cd k8s
cleanup.bat
```

## 📚 Detailed Documentation

- **📖 Full Setup Guide**: [k8s/SETUP.md](k8s/SETUP.md)
- **📋 Kubernetes README**: [k8s/README.md](k8s/README.md)

## 🎯 Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Frontend     │────▶│   API Gateway   │────▶│  Auth Service   │
│   (React App)   │    │   (Port 8080)   │    │   (Port 8081)   │
│   (Port 3000)   │    └─────────────────┘    └─────────────────┘
└─────────────────┘             │                       │
                                 │              ┌─────────────────┐
                                 │              │   MySQL Auth    │
                                 │              │   (Port 3306)   │
                                 │              └─────────────────┘
                                 │
                    ┌─────────────────┐    ┌─────────────────┐
                    │Restaurant Service│    │  Order Service  │
                    │   (Port 9093)   │    │   (Port 9094)   │
                    └─────────────────┘    └─────────────────┘
                             │                       │
                    ┌─────────────────┐    ┌─────────────────┐
                    │MySQL Restaurant │    │   MySQL Order   │
                    │   (Port 3306)   │    │   (Port 3306)   │
                    └─────────────────┘    └─────────────────┘
```

## ✨ Features

- 🔐 **Secure**: JWT authentication with base64 encoded secrets
- 📊 **Scalable**: Multiple replicas for each service
- 💾 **Persistent**: MySQL data persists across restarts
- 🌐 **Accessible**: LoadBalancer services for external access
- 📈 **Observable**: Health checks and actuator endpoints
- 🛠️ **Maintainable**: Easy deployment and cleanup scripts

Happy coding! 🎉
