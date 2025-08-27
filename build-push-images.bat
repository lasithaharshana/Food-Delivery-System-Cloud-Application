@echo off
REM Build and Push Docker Images Script for Food Delivery System
REM This script matches the services in docker-compose.yml exactly
REM Make sure you're logged into Docker Hub: docker login

echo 🐳 Building and Pushing Food Delivery System Docker Images...
echo.

REM Check if logged into Docker Hub
docker info > nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo 🔐 Please make sure you're logged into Docker Hub (docker login)
echo.

set DOCKER_USERNAME=lasithaharshana
set PROJECT_ROOT=%cd%

echo 📦 Building Auth Service...
cd "%PROJECT_ROOT%\Backend\authservice"
docker build -t %DOCKER_USERNAME%/food-delivery-authservice:latest .
if errorlevel 1 (
    echo ❌ Failed to build authservice
    pause
    exit /b 1
)

echo 📤 Pushing Auth Service...
docker push %DOCKER_USERNAME%/food-delivery-authservice:latest
if errorlevel 1 (
    echo ❌ Failed to push authservice
    pause
    exit /b 1
)

echo 🍽️ Building Restaurant Service...
cd "%PROJECT_ROOT%\Backend\restaurantservice"
docker build -t %DOCKER_USERNAME%/food-delivery-restaurantservice:latest .
if errorlevel 1 (
    echo ❌ Failed to build restaurantservice
    pause
    exit /b 1
)

echo 📤 Pushing Restaurant Service...
docker push %DOCKER_USERNAME%/food-delivery-restaurantservice:latest
if errorlevel 1 (
    echo ❌ Failed to push restaurantservice
    pause
    exit /b 1
)

echo 📦 Building Order Service...
cd "%PROJECT_ROOT%\Backend\orderservice"
docker build -t %DOCKER_USERNAME%/food-delivery-orderservice:latest .
if errorlevel 1 (
    echo ❌ Failed to build orderservice
    pause
    exit /b 1
)

echo 📤 Pushing Order Service...
docker push %DOCKER_USERNAME%/food-delivery-orderservice:latest
if errorlevel 1 (
    echo ❌ Failed to push orderservice
    pause
    exit /b 1
)

echo 🌐 Building API Gateway...
cd "%PROJECT_ROOT%\Backend\apigateway"
docker build -t %DOCKER_USERNAME%/food-delivery-apigateway:latest .
if errorlevel 1 (
    echo ❌ Failed to build apigateway
    pause
    exit /b 1
)

echo 📤 Pushing API Gateway...
docker push %DOCKER_USERNAME%/food-delivery-apigateway:latest
if errorlevel 1 (
    echo ❌ Failed to push apigateway
    pause
    exit /b 1
)

echo 🎨 Building Frontend...
cd "%PROJECT_ROOT%\frontend"
docker build -t %DOCKER_USERNAME%/food-delivery-frontend:latest .
if errorlevel 1 (
    echo ❌ Failed to build frontend
    pause
    exit /b 1
)

echo 📤 Pushing Frontend...
docker push %DOCKER_USERNAME%/food-delivery-frontend:latest
if errorlevel 1 (
    echo ❌ Failed to push frontend
    pause
    exit /b 1
)

echo.
echo ✅ All images built and pushed successfully!
echo.
echo 📋 Images created:
echo - %DOCKER_USERNAME%/food-delivery-authservice:latest
echo - %DOCKER_USERNAME%/food-delivery-restaurantservice:latest
echo - %DOCKER_USERNAME%/food-delivery-orderservice:latest
echo - %DOCKER_USERNAME%/food-delivery-apigateway:latest
echo - %DOCKER_USERNAME%/food-delivery-frontend:latest
echo.
echo 🚀 Ready for Kubernetes deployment!
echo Run: cd k8s && deploy.bat

pause
