#!/bin/bash

# Food Delivery System - Kubernetes Deployment Script
# This script deploys the entire Food Delivery System to Kubernetes

echo " Starting Food Delivery System Kubernetes Deployment..."

# Apply namespace first
echo " Creating namespace..."
kubectl apply -f namespace.yaml

# Apply secrets and configmaps
echo " Applying secrets and configurations..."
kubectl apply -f secrets.yaml
kubectl apply -f configmaps.yaml

# Apply persistent volumes
echo " Creating persistent volumes..."
kubectl apply -f persistent-volumes.yaml

# Deploy MySQL databases
echo " Deploying MySQL databases..."
kubectl apply -f mysql-deployments.yaml

# Wait for databases to be ready
echo " Waiting for databases to be ready..."
kubectl wait --for=condition=ready pod -l app=mysql-auth -n food-delivery --timeout=300s
kubectl wait --for=condition=ready pod -l app=mysql-restaurant -n food-delivery --timeout=300s
kubectl wait --for=condition=ready pod -l app=mysql-order -n food-delivery --timeout=300s

# Deploy services in order
echo " Deploying Auth Service..."
kubectl apply -f authservice.yaml

echo " Waiting for Auth Service to be ready..."
kubectl wait --for=condition=ready pod -l app=authservice -n food-delivery --timeout=300s

echo " Deploying Restaurant Service..."
kubectl apply -f restaurantservice.yaml

echo " Waiting for Restaurant Service to be ready..."
kubectl wait --for=condition=ready pod -l app=restaurantservice -n food-delivery --timeout=300s

echo " Deploying Order Service..."
kubectl apply -f orderservice.yaml

echo " Waiting for Order Service to be ready..."
kubectl wait --for=condition=ready pod -l app=orderservice -n food-delivery --timeout=300s

echo " Deploying API Gateway..."
kubectl apply -f apigateway.yaml

echo " Waiting for API Gateway to be ready..."
kubectl wait --for=condition=ready pod -l app=apigateway -n food-delivery --timeout=300s

echo " Deploying Frontend..."
kubectl apply -f frontend.yaml

echo " Waiting for Frontend to be ready..."
kubectl wait --for=condition=ready pod -l app=frontend -n food-delivery --timeout=300s

echo " Deployment completed!"
echo ""
echo " Getting service information..."
kubectl get services -n food-delivery

echo ""
echo " Getting pod status..."
kubectl get pods -n food-delivery

echo ""
echo " Access URLs:"
echo "Frontend: http://$(kubectl get service frontend -n food-delivery -o jsonpath='{.status.loadBalancer.ingress[0].ip}'):80"
echo "API Gateway: http://$(kubectl get service apigateway -n food-delivery -o jsonpath='{.status.loadBalancer.ingress[0].ip}'):8080"

echo ""
echo " Food Delivery System is now running on Kubernetes!"
