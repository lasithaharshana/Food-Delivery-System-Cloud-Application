# Food Delivery System - Kubernetes Deployment Guide

## Overview
This guide provides instructions for deploying the Food Delivery System on Kubernetes, including all core services and monitoring components.

## Architecture
The system consists of the following components:

### Core Services
- **Frontend** - React application (Port: 80)
- **API Gateway** - Spring Boot Gateway (Port: 8080)
- **Auth Service** - Authentication service (Port: 8081)
- **Food Service** - Restaurant and menu management (Port: 9093)
- **Order Service** - Order processing (Port: 9094)
- **File Server** - Nginx for serving uploaded images (Port: 80)

### Databases
- **MySQL Auth DB** - Authentication data (Port: 3306)
- **MySQL Food DB** - Restaurant and menu data (Port: 3306)
- **MySQL Order DB** - Order data (Port: 3306)

### Monitoring Stack
- **Prometheus** - Metrics collection (Port: 9090)
- **Grafana** - Monitoring dashboards (Port: 3000)
- **cAdvisor** - Container metrics (Port: 8080)
- **Node Exporter** - Host metrics (Port: 9100)
- **Blackbox Exporter** - Endpoint monitoring (Port: 9115)
- **MySQL Exporters** - Database metrics (Port: 9104)

## Prerequisites
- Kubernetes cluster (minikube, kind, or cloud provider)
- kubectl configured
- Ingress controller (nginx-ingress recommended)

## Quick Deployment

### Windows
```cmd
cd k8s
deploy.bat
```

### Linux/Mac
```bash
cd k8s
chmod +x deploy.sh
./deploy.sh
```

## Manual Deployment Steps

1. **Create Namespace and Secrets**
   ```bash
   kubectl apply -f namespace.yaml
   kubectl apply -f secrets.yaml
   kubectl apply -f configmaps.yaml
   ```

2. **Setup Storage**
   ```bash
   kubectl apply -f persistent-volumes.yaml
   ```

3. **Deploy Databases**
   ```bash
   kubectl apply -f mysql-deployments.yaml
   ```

4. **Deploy Application Services**
   ```bash
   kubectl apply -f authservice.yaml
   kubectl apply -f foodservice.yaml
   kubectl apply -f orderservice.yaml
   kubectl apply -f apigateway.yaml
   kubectl apply -f file-server.yaml
   kubectl apply -f frontend.yaml
   ```

5. **Deploy Monitoring**
   ```bash
   kubectl apply -f monitoring-config.yaml
   kubectl apply -f monitoring.yaml
   ```

6. **Setup Ingress**
   ```bash
   kubectl apply -f ingress.yaml
   ```

## Access URLs

### With Ingress (Recommended)
- **Frontend**: http://food-delivery.local or http://localhost
- **API**: http://food-delivery.local/api or http://localhost/api
- **Grafana**: http://food-delivery.local/grafana or http://localhost/grafana (admin/admin123)
- **Prometheus**: http://food-delivery.local/prometheus or http://localhost/prometheus
- **File Server**: http://food-delivery.local/uploads or http://localhost/uploads

### Port Forwarding (if LoadBalancer is pending)
```bash
kubectl port-forward -n food-delivery service/frontend 3000:80
kubectl port-forward -n food-delivery service/apigateway 8080:8080
kubectl port-forward -n food-delivery service/grafana 3001:3000
kubectl port-forward -n food-delivery service/prometheus 9090:9090
```

## Monitoring

### Grafana Dashboards
- Default admin credentials: `admin/admin123`
- Prometheus datasource is pre-configured
- Import additional dashboards as needed

### Prometheus Targets
- Application services with `/actuator/prometheus` endpoints
- MySQL databases via exporters
- Container metrics via cAdvisor
- Host metrics via Node Exporter
- Health checks via Blackbox Exporter

## Storage
- **Database Storage**: 5Gi per MySQL instance
- **File Uploads**: 2Gi shared storage
- **Prometheus**: 10Gi for metrics retention (15 days)
- **Grafana**: 5Gi for dashboard storage

## Scaling
Services are configured with replicas:
- Frontend: 2 replicas
- API Gateway: 2 replicas
- Auth Service: 2 replicas
- Food Service: 2 replicas
- Order Service: 2 replicas

To scale a service:
```bash
kubectl scale deployment -n food-delivery authservice --replicas=3
```

## Troubleshooting

### Check Pod Status
```bash
kubectl get pods -n food-delivery
```

### View Pod Logs
```bash
kubectl logs -n food-delivery <pod-name>
```

### Check Services
```bash
kubectl get services -n food-delivery
```

### Check Ingress
```bash
kubectl get ingress -n food-delivery
```

## Cleanup
To remove all resources:

### Windows
```cmd
cleanup.bat
```

### Linux/Mac
```bash
./cleanup.sh
```

## File Structure
- `namespace.yaml` - Kubernetes namespace
- `secrets.yaml` - Database credentials and JWT secrets
- `configmaps.yaml` - Application configuration and MySQL init scripts
- `persistent-volumes.yaml` - Storage claims
- `mysql-deployments.yaml` - Database deployments
- `authservice.yaml` - Authentication service
- `foodservice.yaml` - Food/restaurant service
- `orderservice.yaml` - Order processing service
- `apigateway.yaml` - API Gateway
- `file-server.yaml` - File serving with Nginx
- `frontend.yaml` - React frontend
- `monitoring.yaml` - Complete monitoring stack
- `monitoring-config.yaml` - Prometheus and Grafana configuration
- `ingress.yaml` - Ingress routing rules
- `deploy.bat/deploy.sh` - Deployment scripts
- `cleanup.bat/cleanup.sh` - Cleanup scripts

## Environment Variables
All sensitive data is stored in Kubernetes secrets, and configuration is managed through ConfigMaps. The system automatically configures:
- Database connections
- Service discovery
- CORS settings
- File upload limits
- JWT authentication
- Monitoring targets

## Updates Made from Docker Compose
- Added complete monitoring stack (Prometheus, Grafana, exporters)
- Fixed database naming consistency (authdb, fooddb, orderdb)
- Added MySQL exporter users via init scripts
- Configured proper health checks and resource limits
- Added monitoring endpoints to ingress
- Removed unnecessary ConfigMaps
- Updated deployment scripts with monitoring components
- Added comprehensive documentation
