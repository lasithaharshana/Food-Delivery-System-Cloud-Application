@echo off
REM Food Delivery System - Kubernetes Cleanup Script for Windows
REM This script removes all resources of the Food Delivery System from Kubernetes

echo Starting Food Delivery System Kubernetes Cleanup...

REM Delete services first
echo 🔧 Deleting services...
kubectl delete -f frontend.yaml --ignore-not-found=true
kubectl delete -f apigateway.yaml --ignore-not-found=true
kubectl delete -f orderservice.yaml --ignore-not-found=true
kubectl delete -f restaurantservice.yaml --ignore-not-found=true
kubectl delete -f authservice.yaml --ignore-not-found=true

REM Delete databases
echo  Deleting MySQL databases...
kubectl delete -f mysql-deployments.yaml --ignore-not-found=true

REM Delete persistent volumes (this will also delete data)
echo  Deleting persistent volumes...
kubectl delete -f persistent-volumes.yaml --ignore-not-found=true

REM Delete configs and secrets
echo  Deleting configurations and secrets...
kubectl delete -f configmaps.yaml --ignore-not-found=true
kubectl delete -f secrets.yaml --ignore-not-found=true

REM Delete namespace (this will delete any remaining resources)
echo  Deleting namespace...
kubectl delete -f namespace.yaml --ignore-not-found=true

echo  Cleanup completed!
echo  All Food Delivery System resources have been removed from Kubernetes.

pause
