# API Usage Examples

## Register a Customer

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_customer",
    "email": "john@customer.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "role": "CUSTOMER"
  }'
```

## Register a Restaurant

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "pizza_place",
    "email": "owner@pizzaplace.com",
    "password": "password123",
    "firstName": "Mario",
    "lastName": "Rossi",
    "phoneNumber": "+1234567891",
    "role": "RESTAURANT",
    "restaurantName": "Mario Pizza Place",
    "restaurantAddress": "123 Pizza Street, Food City"
  }'
```

## Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_customer",
    "password": "password123"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "username": "john_customer",
    "email": "john@customer.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "role": "CUSTOMER",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

## Get User Profile (Authenticated)

```bash
curl -X GET http://localhost:8081/api/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Update User Profile

```bash
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "firstName": "John Updated",
    "lastName": "Doe Updated",
    "phoneNumber": "+9876543210"
  }'
```

## Get All Users (with pagination)

```bash
curl -X GET "http://localhost:8081/api/users?paginated=true&page=0&size=10&sortBy=createdAt&sortDir=desc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Get Users by Role

```bash
# Get all restaurants
curl -X GET http://localhost:8081/api/users/role/RESTAURANT \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get all customers
curl -X GET http://localhost:8081/api/users/role/CUSTOMER \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Deactivate User

```bash
curl -X PATCH http://localhost:8081/api/users/1/deactivate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Activate User

```bash
curl -X PATCH http://localhost:8081/api/users/1/activate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Health Check

```bash
curl -X GET http://localhost:8081/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## Error Responses

### Validation Error (400)
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "Invalid email format",
    "password": "Password must be at least 6 characters"
  }
}
```

### User Already Exists (409)
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 409,
  "error": "User Already Exists",
  "message": "Username already exists: john_customer",
  "path": "/api/auth/register"
}
```

### Unauthorized (401)
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Invalid username/email or password",
  "path": "/api/auth/login"
}
```

### User Not Found (404)
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "User Not Found",
  "message": "User not found with ID: 999",
  "path": "/api/users/999"
}
```
