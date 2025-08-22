# Customer Service - Food Delivery System

A Spring Boot microservice for managing customers in the Food Delivery System. This service provides REST APIs for customer registration, authentication, and management with PostgreSQL database integration.

## 🚀 Quick Start

### Prerequisites

- **Docker** and **Docker Compose** installed on your system

### 1. Clone and Run

```bash

# Navigate to customer service directory
cd Food-Delivery-System-Cloud-Application/Backend/customerservice

# Start the service (this will build and run everything)
docker-compose up -d --build
```

### 2. Verify Service is Running

```bash
# Check running containers
docker ps

# Check service health
curl http://localhost:9092/customers
```

That's it! The service is now running on `http://localhost:9092` 🎉

## 📋 API Documentation

### Base URL
```
http://localhost:9092
```

### Endpoints

#### Customer Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `GET` | `/customers` | Get all customers | - |
| `POST` | `/customers` | Create new customer | [CustomerRequest](#customerrequest) |
| `GET` | `/customers/{id}` | Get customer by ID | - |
| `PUT` | `/customers/{id}` | Update customer | [CustomerRequest](#customerrequest) |
| `DELETE` | `/customers/{id}` | Delete customer | - |

#### Authentication

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/auth/login` | Authenticate customer | [AuthRequest](#authrequest) |
| `POST` | `/customers/auth/login` | Alternative auth endpoint | [AuthRequest](#authrequest) |

### Request/Response Models

#### CustomerRequest
```json
{
  "name": "John Doe",           // Required: 2-100 characters
  "email": "john@example.com",  // Required: Valid email format
  "password": "password123"     // Required: Minimum 6 characters
}
```

#### CustomerResponse
```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2025-08-22T10:30:00Z"
}
```

#### AuthRequest
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

#### AuthResponse
```json
{
  "token": "token:uuid"
}
```

## 🧪 Testing the API

### 1. Create a Customer
```bash
curl -X POST http://localhost:9092/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "password": "alice123"
  }'
```

### 2. Get All Customers
```bash
curl http://localhost:9092/customers
```

### 3. Authenticate
```bash
curl -X POST http://localhost:9092/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "alice123"
  }'
```

### 4. Update Customer
```bash
curl -X PUT http://localhost:9092/customers/{customer-id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Smith",
    "email": "alice.smith@example.com",
    "password": "newalice123"
  }'
```

### 5. Delete Customer
```bash
curl -X DELETE http://localhost:9092/customers/{customer-id}
```

## 🔧 Configuration

### Environment Variables

You can customize the service by creating a `.env` file:

```bash
# Copy the example environment file
cp .env.example .env

# Edit the file to customize settings
# Example configurations:
POSTGRES_DB=your_db_name
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_secure_password
SERVER_PORT=9092
```

### Custom Database

To use an external database, update the environment variables in `docker-compose.yml`:

```yaml
environment:
  DB_HOST: your-db-host
  DB_PORT: 5432
  DB_NAME: your-db-name
  DB_USER: your-db-user
  DB_PASSWORD: your-db-password
```

## 📊 Monitoring & Health Checks

### Health Check Endpoint
```bash
# Check if service is healthy
curl http://localhost:9092/customers
```

### Container Health Status
```bash
# Check container health
docker ps
docker-compose ps
```

### Logs
```bash
# View service logs
docker-compose logs customerservice

# Follow logs in real-time
docker-compose logs -f customerservice
```

## 🚨 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using port 9092
   netstat -an | grep 9092
   
   # Stop existing containers
   docker-compose down
   ```

2. **Database Connection Issues**
   ```bash
   # Check database container
   docker-compose logs db
   
   # Restart services
   docker-compose restart
   ```

3. **Build Failures**
   ```bash
   # Clean rebuild
   docker-compose down
   docker-compose up --build --force-recreate
   ```

### Reset Everything
```bash
# Stop and remove containers, networks, and volumes
docker-compose down -v

# Remove images
docker rmi customerservice-customerservice postgres:15

# Start fresh
docker-compose up -d --build
```

## 🔒 Security Features

- **Password Encryption**: Passwords are hashed using BCrypt
- **Input Validation**: All inputs are validated before processing
- **SQL Injection Protection**: JPA/Hibernate provides protection
- **Container Security**: Non-root user in Docker container

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client API    │    │  Customer       │    │   PostgreSQL    │
│   Requests      │◄──►│  Service        │◄──►│   Database      │
│                 │    │  (Spring Boot)  │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
``
