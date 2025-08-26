# Food Delivery System - Kubernetes Deployment

This directory contains all the necessary Kubernetes manifests to deploy the Food Delivery System on a Kubernetes cluster.

## 📋 Prerequisites

- Kubernetes cluster (local or cloud)
- kubectl configured to connect to your cluster
- Docker Hub account (images are pulled from `lasithaharshana/food-delivery-*`)

### For Local Development:
- Docker Desktop with Kubernetes enabled, OR
- Minikube, OR
- Kind (Kubernetes in Docker)

## 🏗️ Architecture

The system consists of:
- **3 MySQL databases** (auth, restaurant, order)
- **Auth Service** (port 8081)
- **Restaurant Service** (port 9093) 
- **Order Service** (port 9094)
- **API Gateway** (port 8080)
- **Frontend** (port 80)

## 🔐 Security

All sensitive data is stored in Kubernetes Secrets with base64 encoding:
- JWT secrets for authentication
- Database passwords
- Service credentials

## 📁 Files Structure

```
k8s/
├── namespace.yaml              # Creates food-delivery namespace
├── secrets.yaml               # All secrets with base64 encoding
├── configmaps.yaml            # Configuration data
├── persistent-volumes.yaml    # Storage for MySQL databases
├── mysql-deployments.yaml     # MySQL database deployments
├── authservice.yaml           # Auth service deployment
├── restaurantservice.yaml     # Restaurant service deployment
├── orderservice.yaml          # Order service deployment
├── apigateway.yaml            # API Gateway deployment
├── frontend.yaml              # Frontend deployment
├── deploy.bat                 # Windows deployment script
├── cleanup.bat                # Windows cleanup script
├── deploy.sh                  # Linux/Mac deployment script
└── cleanup.sh                 # Linux/Mac cleanup script
```

## 🚀 Quick Start

### Option 1: Using Deployment Scripts (Recommended)

#### Windows:
```cmd
cd k8s
deploy.bat
```

#### Linux/Mac:
```bash
cd k8s
chmod +x deploy.sh
./deploy.sh
```

### Option 2: Manual Deployment

1. **Create namespace and apply configurations:**
```bash
kubectl apply -f namespace.yaml
kubectl apply -f secrets.yaml
kubectl apply -f configmaps.yaml
kubectl apply -f persistent-volumes.yaml
```

2. **Deploy databases:**
```bash
kubectl apply -f mysql-deployments.yaml
```

3. **Wait for databases to be ready:**
```bash
kubectl wait --for=condition=ready pod -l app=mysql-auth -n food-delivery --timeout=300s
kubectl wait --for=condition=ready pod -l app=mysql-restaurant -n food-delivery --timeout=300s
kubectl wait --for=condition=ready pod -l app=mysql-order -n food-delivery --timeout=300s
```

4. **Deploy services in order:**
```bash
kubectl apply -f authservice.yaml
kubectl wait --for=condition=ready pod -l app=authservice -n food-delivery --timeout=300s

kubectl apply -f restaurantservice.yaml
kubectl wait --for=condition=ready pod -l app=restaurantservice -n food-delivery --timeout=300s

kubectl apply -f orderservice.yaml
kubectl wait --for=condition=ready pod -l app=orderservice -n food-delivery --timeout=300s

kubectl apply -f apigateway.yaml
kubectl wait --for=condition=ready pod -l app=apigateway -n food-delivery --timeout=300s

kubectl apply -f frontend.yaml
```

## 🌐 Accessing the Application

### Check Service Status:
```bash
kubectl get services -n food-delivery
kubectl get pods -n food-delivery
```

### Access URLs:

#### If using LoadBalancer (cloud or Docker Desktop):
```bash
# Get external IPs
kubectl get services -n food-delivery

# Frontend will be available at: http://<EXTERNAL-IP>:80
# API Gateway will be available at: http://<EXTERNAL-IP>:8080
```

#### If using port forwarding (Minikube, Kind, etc.):
```bash
# Frontend
kubectl port-forward -n food-delivery service/frontend 3000:80

# API Gateway  
kubectl port-forward -n food-delivery service/apigateway 8080:8080

# Access at:
# Frontend: http://localhost:3000
# API Gateway: http://localhost:8080
```

## 🔍 Monitoring and Troubleshooting

### Check pod logs:
```bash
kubectl logs -n food-delivery deployment/authservice
kubectl logs -n food-delivery deployment/restaurantservice
kubectl logs -n food-delivery deployment/orderservice
kubectl logs -n food-delivery deployment/apigateway
kubectl logs -n food-delivery deployment/frontend
```

### Check pod status:
```bash
kubectl get pods -n food-delivery -o wide
kubectl describe pod <pod-name> -n food-delivery
```

### Check service connectivity:
```bash
kubectl exec -n food-delivery deployment/apigateway -- curl http://authservice:8081/actuator/health
kubectl exec -n food-delivery deployment/apigateway -- curl http://restaurantservice:9093/actuator/health
kubectl exec -n food-delivery deployment/apigateway -- curl http://orderservice:9094/actuator/health
```

## 🗄️ Database Access

### Connect to MySQL databases:
```bash
# Auth Database
kubectl exec -it -n food-delivery deployment/mysql-auth -- mysql -u root -ppassword auth_db

# Restaurant Database
kubectl exec -it -n food-delivery deployment/mysql-restaurant -- mysql -u user -ppassword restaurantdb

# Order Database
kubectl exec -it -n food-delivery deployment/mysql-order -- mysql -u user -ppassword orderdb
```

## 📊 Scaling

### Scale services:
```bash
kubectl scale deployment authservice --replicas=3 -n food-delivery
kubectl scale deployment restaurantservice --replicas=3 -n food-delivery
kubectl scale deployment orderservice --replicas=3 -n food-delivery
kubectl scale deployment apigateway --replicas=3 -n food-delivery
kubectl scale deployment frontend --replicas=3 -n food-delivery
```

## 🧹 Cleanup

### Option 1: Using Cleanup Scripts

#### Windows:
```cmd
cleanup.bat
```

#### Linux/Mac:
```bash
chmod +x cleanup.sh
./cleanup.sh
```

### Option 2: Manual Cleanup
```bash
kubectl delete namespace food-delivery
```

This will remove all resources in the food-delivery namespace.

## 🔒 Secrets Management

The following secrets are base64 encoded in `secrets.yaml`:

### JWT Configuration:
- `JWT_SECRET`: `wi9hb9t4CkfiHh/vvbBQyVvAwkwPXBE9d/9UVmPCuWk=`
- `JWT_EXPIRATION`: `86400000` (24 hours)

### Database Credentials:
- **Auth DB**: root/password, authuser/authpass
- **Restaurant DB**: root/root, user/password  
- **Order DB**: root/root, user/password

⚠️ **Important**: Change these default passwords in production!

## 🐳 Docker Images

The deployment uses the following Docker Hub images:
- `lasithaharshana/food-delivery-authservice:latest`
- `lasithaharshana/food-delivery-restaurantservice:latest`
- `lasithaharshana/food-delivery-orderservice:latest`
- `lasithaharshana/food-delivery-apigateway:latest`
- `lasithaharshana/food-delivery-frontend:latest`

These images are built automatically by the GitHub Actions workflow in `.github/workflows/docker-build-push.yml`.

## 🔧 Configuration

### Resource Limits:
- **MySQL**: 512Mi-1Gi memory, 250m-500m CPU
- **Services**: 512Mi-1Gi memory, 250m-500m CPU  
- **Frontend**: 256Mi-512Mi memory, 100m-250m CPU

### Health Checks:
- **Liveness probes**: Check if containers are running
- **Readiness probes**: Check if services are ready to accept traffic
- **Startup delays**: Allow time for Spring Boot applications to start

## 📈 Performance Tuning

For production deployments, consider:

1. **Increase replicas** for high availability
2. **Configure resource requests/limits** based on load testing
3. **Use horizontal pod autoscaler** for automatic scaling
4. **Configure ingress** for proper load balancing
5. **Use persistent storage classes** appropriate for your environment
6. **Enable monitoring** with Prometheus/Grafana
7. **Configure log aggregation** with ELK stack or similar

## 🌟 Next Steps

1. **Set up Ingress** for external access with custom domains
2. **Configure TLS/SSL** certificates
3. **Add monitoring** with Prometheus and Grafana
4. **Set up CI/CD** pipeline for automated deployments
5. **Configure backup** strategies for MySQL databases
6. **Implement secrets management** with tools like Vault or External Secrets Operator
