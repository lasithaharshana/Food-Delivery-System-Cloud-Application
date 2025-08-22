@echo off
setlocal enabledelayedexpansion

REM Customer Service Startup Script for Windows
REM This script makes it easy to run the customer service

echo 🚀 Starting Customer Service for Food Delivery System
echo ==============================================

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Docker is not running. Please start Docker first.
    exit /b 1
)

REM Check if Docker Compose is available
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: docker-compose is not installed.
    exit /b 1
)

REM Get command argument
set COMMAND=%1
if "%COMMAND%"=="" set COMMAND=start

goto %COMMAND% 2>nul || goto unknown_command

:start
echo 📦 Starting customer service...
docker-compose up -d

echo ⏳ Waiting for service to be ready...
timeout /t 30 /nobreak >nul

echo 🔍 Checking service health...
curl -f http://localhost:9092/customers >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Customer service is running successfully!
    echo 🌐 Service URL: http://localhost:9092
    echo 📚 API Documentation: See README.md
    call :show_test_commands
) else (
    echo ❌ Service health check failed. Checking logs...
    docker-compose logs customerservice
)
goto end

:stop
echo 🛑 Stopping customer service...
docker-compose down
echo ✅ Service stopped successfully!
goto end

:restart
echo 🔄 Restarting customer service...
docker-compose restart

echo ⏳ Waiting for service to be ready...
timeout /t 30 /nobreak >nul

echo 🔍 Checking service health...
curl -f http://localhost:9092/customers >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Customer service restarted successfully!
) else (
    echo ❌ Service health check failed after restart.
    docker-compose logs customerservice
)
goto end

:build
echo 🔨 Building and starting customer service...
docker-compose up -d --build

echo ⏳ Waiting for service to be ready...
timeout /t 60 /nobreak >nul

echo 🔍 Checking service health...
curl -f http://localhost:9092/customers >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Customer service is running successfully!
    echo 🌐 Service URL: http://localhost:9092
    call :show_test_commands
) else (
    echo ❌ Service health check failed. Checking logs...
    docker-compose logs customerservice
)
goto end

:logs
echo 📋 Showing customer service logs...
docker-compose logs -f customerservice
goto end

:status
echo 📊 Customer Service Status
echo =========================

echo 🐳 Container Status:
docker-compose ps

echo.
echo 🔍 Service Health:
curl -f http://localhost:9092/customers >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Customer Service: HEALTHY
) else (
    echo ❌ Customer Service: UNHEALTHY
)

echo.
echo 🌐 Service URLs:
echo    - API Base: http://localhost:9092
echo    - Database: localhost:5432
goto end

:test
echo 🧪 Running API Tests...
echo ======================

REM Check if service is running
curl -f http://localhost:9092/customers >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Service is not running. Please start it first.
    exit /b 1
)

echo 1. Testing GET /customers...
curl -s http://localhost:9092/customers
echo.

echo 2. Testing POST /customers...
curl -s -X POST http://localhost:9092/customers -H "Content-Type: application/json" -d "{\"name\":\"Test User\",\"email\":\"test@test.com\",\"password\":\"test123\"}"
echo.

echo 3. Testing authentication...
curl -s -X POST http://localhost:9092/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test@test.com\",\"password\":\"test123\"}"
echo.

echo ✅ Basic tests completed!
goto end

:clean
echo 🧹 Cleaning up customer service...
echo ⚠️  This will remove all containers, networks, and volumes!
set /p REPLY="Are you sure? (y/N): "
if /i "!REPLY!"=="y" (
    docker-compose down -v
    docker rmi customerservice-customerservice postgres:15 2>nul
    echo ✅ Cleanup completed!
) else (
    echo ❌ Cleanup cancelled.
)
goto end

:help
call :show_usage
goto end

:show_usage
echo Usage: %0 [COMMAND]
echo.
echo Commands:
echo   start     Start the customer service (default)
echo   stop      Stop the customer service
echo   restart   Restart the customer service
echo   build     Build and start the service
echo   logs      Show service logs
echo   status    Show service status
echo   test      Run API tests
echo   clean     Stop and remove all containers/volumes
echo   help      Show this help message
goto :eof

:show_test_commands
echo.
echo 🧪 Quick Test Commands:
echo ======================
echo # Get all customers:
echo curl http://localhost:9092/customers
echo.
echo # Create a customer:
echo curl -X POST http://localhost:9092/customers ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"password123\"}"
echo.
echo # Run full test suite:
echo start.bat test
goto :eof

:unknown_command
echo ❌ Unknown command: %COMMAND%
call :show_usage
exit /b 1

:end
endlocal
