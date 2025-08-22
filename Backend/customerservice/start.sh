#!/bin/bash

# Customer Service Startup Script
# This script makes it easy to run the customer service

set -e

echo "🚀 Starting Customer Service for Food Delivery System"
echo "=============================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose > /dev/null 2>&1; then
    echo "❌ Error: docker-compose is not installed."
    exit 1
fi

# Function to show usage
show_usage() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start     Start the customer service (default)"
    echo "  stop      Stop the customer service"
    echo "  restart   Restart the customer service"
    echo "  build     Build and start the service"
    echo "  logs      Show service logs"
    echo "  status    Show service status"
    echo "  test      Run API tests"
    echo "  clean     Stop and remove all containers/volumes"
    echo "  help      Show this help message"
}

# Function to start the service
start_service() {
    echo "📦 Starting customer service..."
    docker-compose up -d
    
    echo "⏳ Waiting for service to be ready..."
    sleep 30
    
    echo "🔍 Checking service health..."
    if curl -f http://localhost:9092/customers > /dev/null 2>&1; then
        echo "✅ Customer service is running successfully!"
        echo "🌐 Service URL: http://localhost:9092"
        echo "📚 API Documentation: See README.md"
        show_test_commands
    else
        echo "❌ Service health check failed. Checking logs..."
        docker-compose logs customerservice
    fi
}

# Function to build and start
build_service() {
    echo "🔨 Building and starting customer service..."
    docker-compose up -d --build
    
    echo "⏳ Waiting for service to be ready..."
    sleep 60
    
    echo "🔍 Checking service health..."
    if curl -f http://localhost:9092/customers > /dev/null 2>&1; then
        echo "✅ Customer service is running successfully!"
        echo "🌐 Service URL: http://localhost:9092"
        show_test_commands
    else
        echo "❌ Service health check failed. Checking logs..."
        docker-compose logs customerservice
    fi
}

# Function to stop the service
stop_service() {
    echo "🛑 Stopping customer service..."
    docker-compose down
    echo "✅ Service stopped successfully!"
}

# Function to restart the service
restart_service() {
    echo "🔄 Restarting customer service..."
    docker-compose restart
    
    echo "⏳ Waiting for service to be ready..."
    sleep 30
    
    echo "🔍 Checking service health..."
    if curl -f http://localhost:9092/customers > /dev/null 2>&1; then
        echo "✅ Customer service restarted successfully!"
    else
        echo "❌ Service health check failed after restart."
        docker-compose logs customerservice
    fi
}

# Function to show logs
show_logs() {
    echo "📋 Showing customer service logs..."
    docker-compose logs -f customerservice
}

# Function to show status
show_status() {
    echo "📊 Customer Service Status"
    echo "========================="
    
    echo "🐳 Container Status:"
    docker-compose ps
    
    echo ""
    echo "🔍 Service Health:"
    if curl -f http://localhost:9092/customers > /dev/null 2>&1; then
        echo "✅ Customer Service: HEALTHY"
    else
        echo "❌ Customer Service: UNHEALTHY"
    fi
    
    echo ""
    echo "🌐 Service URLs:"
    echo "   - API Base: http://localhost:9092"
    echo "   - Database: localhost:5432"
}

# Function to run API tests
run_tests() {
    echo "🧪 Running API Tests..."
    echo "======================"
    
    # Check if service is running
    if ! curl -f http://localhost:9092/customers > /dev/null 2>&1; then
        echo "❌ Service is not running. Please start it first."
        exit 1
    fi
    
    echo "1. Testing GET /customers..."
    curl -s http://localhost:9092/customers | head -c 100
    echo ""
    
    echo "2. Testing POST /customers..."
    CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:9092/customers \
        -H "Content-Type: application/json" \
        -d '{"name":"Test User","email":"test@test.com","password":"test123"}')
    echo $CUSTOMER_RESPONSE | head -c 100
    echo ""
    
    # Extract customer ID for further tests
    CUSTOMER_ID=$(echo $CUSTOMER_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    
    if [ ! -z "$CUSTOMER_ID" ]; then
        echo "3. Testing GET /customers/$CUSTOMER_ID..."
        curl -s http://localhost:9092/customers/$CUSTOMER_ID | head -c 100
        echo ""
        
        echo "4. Testing authentication..."
        curl -s -X POST http://localhost:9092/auth/login \
            -H "Content-Type: application/json" \
            -d '{"email":"test@test.com","password":"test123"}' | head -c 100
        echo ""
        
        echo "5. Cleaning up test data..."
        curl -s -X DELETE http://localhost:9092/customers/$CUSTOMER_ID
        echo "✅ Tests completed!"
    else
        echo "❌ Failed to create test customer"
    fi
}

# Function to clean everything
clean_service() {
    echo "🧹 Cleaning up customer service..."
    echo "⚠️  This will remove all containers, networks, and volumes!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker-compose down -v
        docker rmi customerservice-customerservice postgres:15 2>/dev/null || true
        echo "✅ Cleanup completed!"
    else
        echo "❌ Cleanup cancelled."
    fi
}

# Function to show test commands
show_test_commands() {
    echo ""
    echo "🧪 Quick Test Commands:"
    echo "======================"
    echo "# Get all customers:"
    echo "curl http://localhost:9092/customers"
    echo ""
    echo "# Create a customer:"
    echo "curl -X POST http://localhost:9092/customers \\"
    echo "  -H 'Content-Type: application/json' \\"
    echo "  -d '{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"password123\"}'"
    echo ""
    echo "# Run full test suite:"
    echo "./start.sh test"
}

# Main script logic
case "${1:-start}" in
    start)
        start_service
        ;;
    stop)
        stop_service
        ;;
    restart)
        restart_service
        ;;
    build)
        build_service
        ;;
    logs)
        show_logs
        ;;
    status)
        show_status
        ;;
    test)
        run_tests
        ;;
    clean)
        clean_service
        ;;
    help|--help|-h)
        show_usage
        ;;
    *)
        echo "❌ Unknown command: $1"
        show_usage
        exit 1
        ;;
esac
