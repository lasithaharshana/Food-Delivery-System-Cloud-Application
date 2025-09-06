@echo off
REM Food Delivery System - Kubernetes Deployment Script for Windows
REM This script deploys the entire Food Delivery System to Kubernetes

echo  Starting Food Delivery System Kubernetes Deployment...

REM Apply namespace first
echo  Creating namespace...
kubectl apply -f namespace.yaml

REM Apply secrets and configmaps
echo  Applying secrets and configurations...
kubectl apply -f secrets.yaml
kubectl apply -f configmaps.yaml

REM Apply persistent volumes
echo  Creating persistent volumes...
kubectl apply -f persistent-volumes.yaml

REM Deploy MySQL databases
echo  Deploying MySQL databases...
kubectl apply -f mysql-deployments.yaml

REM Wait for databases to be ready
echo  Waiting for databases to be ready...
kubectl wait --for=condition=ready pod -l app=mysql-auth -n food-delivery --timeout=300s
kubectl wait --for=condition=ready pod -l app=mysql-food -n food-delivery --timeout=300s
kubectl wait --for=condition=ready pod -l app=mysql-order -n food-delivery --timeout=300s

REM Deploy services in order
echo  Deploying Auth Service...
kubectl apply -f authservice.yaml

echo  Waiting for Auth Service to be ready...
kubectl wait --for=condition=ready pod -l app=authservice -n food-delivery --timeout=300s

echo  Deploying food Service...
kubectl apply -f foodservice.yaml

echo  Waiting for food Service to be ready...
kubectl wait --for=condition=ready pod -l app=foodservice -n food-delivery --timeout=300s

echo  Deploying Order Service...
kubectl apply -f orderservice.yaml

echo  Waiting for Order Service to be ready...
kubectl wait --for=condition=ready pod -l app=orderservice -n food-delivery --timeout=300s

echo  Deploying API Gateway...
kubectl apply -f apigateway.yaml

echo  Waiting for API Gateway to be ready...
kubectl wait --for=condition=ready pod -l app=apigateway -n food-delivery --timeout=300s

echo  Deploying File Server...
kubectl apply -f file-server.yaml

echo  Waiting for File Server to be ready...
kubectl wait --for=condition=ready pod -l app=file-server -n food-delivery --timeout=300s

echo  Deploying Frontend...
kubectl apply -f frontend.yaml

echo  Waiting for Frontend to be ready...
kubectl wait --for=condition=ready pod -l app=frontend -n food-delivery --timeout=300s

REM Deploy monitoring stack
echo  Deploying Monitoring Configuration...
kubectl apply -f monitoring-config.yaml

echo  Deploying Monitoring Stack...
kubectl apply -f monitoring.yaml

echo  Waiting for Monitoring components to be ready...
kubectl wait --for=condition=ready pod -l app=prometheus -n food-delivery --timeout=300s
kubectl wait --for=condition=ready pod -l app=grafana -n food-delivery --timeout=300s

echo  Deploying Ingress...
kubectl apply -f ingress.yaml

echo  Deployment completed!
echo.
echo  Getting service information...
kubectl get services -n food-delivery

echo.
echo  Getting pod status...
kubectl get pods -n food-delivery

echo.
echo  Food Delivery System is now running on Kubernetes!
echo.
echo  Access URLs:
echo  - Frontend: http://food-delivery.local or http://localhost
echo  - API Gateway: http://food-delivery.local/api or http://localhost/api
echo  - Grafana Monitoring: http://food-delivery.local/grafana or http://localhost/grafana (admin/admin123)
echo  - Prometheus: http://food-delivery.local/prometheus or http://localhost/prometheus
echo  - File Server: http://food-delivery.local/uploads or http://localhost/uploads
echo.
echo  To get service information:
echo kubectl get services -n food-delivery
echo.
echo  For port forwarding (if LoadBalancer is pending):
echo kubectl port-forward -n food-delivery service/frontend 3000:80
echo kubectl port-forward -n food-delivery service/apigateway 8080:8080
echo kubectl port-forward -n food-delivery service/grafana 3001:3000
echo kubectl port-forward -n food-delivery service/prometheus 9090:9090

pause
