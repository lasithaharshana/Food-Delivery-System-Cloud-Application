@echo off
REM Food Delivery System - Complete Setup and Deployment Script
REM This script will guide you through the entire process

echo ==========================================
echo 🚀 Food Delivery System K8s Setup
echo ==========================================
echo.

REM Check prerequisites
echo 🔍 Checking prerequisites...

REM Check Docker
docker --version > nul 2>&1
if errorlevel 1 (
    echo ❌ Docker not found. Please install Docker Desktop.
    echo Download from: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
) else (
    echo ✅ Docker found
)

REM Check kubectl
kubectl version --client > nul 2>&1
if errorlevel 1 (
    echo ❌ kubectl not found. Please install kubectl.
    echo Download from: https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/
    pause
    exit /b 1
) else (
    echo ✅ kubectl found
)

REM Check Kubernetes cluster
echo.
echo 🔍 Checking Kubernetes cluster...
kubectl cluster-info > nul 2>&1
if errorlevel 1 (
    echo ❌ No Kubernetes cluster found.
    echo.
    echo 📋 Please set up a Kubernetes cluster first:
    echo.
    echo Option 1: Docker Desktop Kubernetes ^(Recommended^)
    echo   1. Open Docker Desktop
    echo   2. Go to Settings ^> Kubernetes
    echo   3. Enable Kubernetes
    echo   4. Apply ^& Restart
    echo.
    echo Option 2: Minikube
    echo   1. Install: choco install minikube
    echo   2. Start: minikube start --driver=docker
    echo.
    echo Option 3: Kind
    echo   1. Install: choco install kind
    echo   2. Create: kind create cluster --name food-delivery
    echo.
    echo After setting up Kubernetes, run this script again.
    pause
    exit /b 1
) else (
    echo ✅ Kubernetes cluster found
    kubectl get nodes
)

echo.
echo ==========================================
echo 🐳 Docker Images Setup
echo ==========================================

REM Check if user wants to build images
echo.
echo Do you want to build and push Docker images to Docker Hub?
echo ^(Required for first-time deployment^)
echo.
set /p BUILD_IMAGES=Build images? (y/n): 

if /i "%BUILD_IMAGES%"=="y" (
    echo.
    echo 🔐 Make sure you're logged into Docker Hub...
    set /p CONTINUE=Press Enter after logging in (docker login), or 'n' to skip: 
    
    if /i not "%CONTINUE%"=="n" (
        echo.
        echo 🏗️ Building and pushing images...
        call build-push-images.bat
        if errorlevel 1 (
            echo ❌ Failed to build/push images
            pause
            exit /b 1
        )
    )
) else (
    echo ⏭️ Skipping image build. Using existing images from Docker Hub.
)

echo.
echo ==========================================
echo ⚙️ Kubernetes Deployment
echo ==========================================

echo.
echo 🚀 Deploying Food Delivery System to Kubernetes...
cd k8s
call deploy.bat

if errorlevel 1 (
    echo ❌ Deployment failed
    pause
    exit /b 1
)

echo.
echo ==========================================
echo 🌐 Access Information
echo ==========================================

echo.
echo 📊 Getting service information...
kubectl get services -n food-delivery

echo.
echo 🔍 Getting pod status...
kubectl get pods -n food-delivery

echo.
echo 🌐 To access the application:
echo.
echo Frontend (React App):
echo   kubectl port-forward -n food-delivery service/frontend 3000:80
echo   Then open: http://localhost:3000
echo.
echo API Gateway (Backend APIs):
echo   kubectl port-forward -n food-delivery service/apigateway 8080:8080  
echo   Then open: http://localhost:8080
echo.

REM Check if LoadBalancer services have external IPs
for /f "tokens=*" %%i in ('kubectl get service frontend -n food-delivery -o jsonpath^="{.status.loadBalancer.ingress[0].ip}" 2^>nul') do set FRONTEND_IP=%%i
for /f "tokens=*" %%i in ('kubectl get service apigateway -n food-delivery -o jsonpath^="{.status.loadBalancer.ingress[0].ip}" 2^>nul') do set API_IP=%%i

if not "%FRONTEND_IP%"=="" (
    echo External Access ^(if available^):
    echo   Frontend: http://%FRONTEND_IP%:80
    echo   API Gateway: http://%API_IP%:8080
    echo.
)

echo ==========================================
echo 🎉 Deployment Complete!
echo ==========================================
echo.
echo 💡 Useful commands:
echo   kubectl get pods -n food-delivery          ^(Check pod status^)
echo   kubectl logs -n food-delivery deployment/authservice  ^(View logs^)
echo   kubectl port-forward -n food-delivery service/frontend 3000:80  ^(Access frontend^)
echo.
echo 🧹 To cleanup everything:
echo   cd k8s ^&^& cleanup.bat
echo.

pause
