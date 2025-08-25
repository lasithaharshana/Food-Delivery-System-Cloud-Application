# Food Delivery System - API Testing Guide

This README provides all the curl commands to test the Food Delivery System APIs through the API Gateway. All commands are ready to copy and paste into Postman or terminal.

## Base URL
```
API Gateway: http://localhost:8080
```

## Authentication Endpoints

### 1. Health Check
```bash
curl -X GET http://localhost:8080/actuator/health
```

### 2. Register Customer
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testcustomer",
    "email": "testcustomer@example.com",
    "password": "password123",
    "role": "CUSTOMER",
    "firstName": "Test",
    "lastName": "Customer",
    "address": "123 Customer St"
  }'
```

### 3. Register Restaurant
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testrestaurant",
    "email": "testrestaurant@example.com",
    "password": "password123",
    "role": "RESTAURANT",
    "firstName": "Test",
    "lastName": "Restaurant",
    "address": "456 Restaurant Ave",
    "restaurantName": "Test Restaurant"
  }'
```

### 4. Login (Customer)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testcustomer",
    "password": "password123"
  }'
```

### 5. Login (Restaurant)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testrestaurant",
    "password": "password123"
  }'
```

### 6. Validate Token
```bash
curl -X POST http://localhost:8080/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_JWT_TOKEN_HERE"
  }'
```

## Protected Endpoints (Require Authentication)

### Authentication Endpoints

#### Validate Token
```bash
curl -X POST http://localhost:8080/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_JWT_TOKEN_HERE"
  }'
```

### User Management Endpoints (Require Authentication)

#### Get User by ID
```bash
curl -X GET http://localhost:8080/api/users/{userId} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get User by Username
```bash
curl -X GET http://localhost:8080/api/users/username/{username} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get User by Email
```bash
curl -X GET "http://localhost:8080/api/users/email/{email}" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get All Users
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get All Users with Pagination
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10&sortBy=id&sortDir=asc&paginated=true" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get Users by Role
```bash
curl -X GET http://localhost:8080/api/users/role/{role} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get Active Users
```bash
curl -X GET http://localhost:8080/api/users/active \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get Inactive Users
```bash
curl -X GET http://localhost:8080/api/users/inactive \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Update User
```bash
curl -X PUT http://localhost:8080/api/users/{userId} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Updated",
    "lastName": "Name",
    "email": "updated@example.com",
    "phoneNumber": "+1234567890",
    "address": "Updated Address"
  }'
```

#### Delete User (Restaurant Role Required)
```bash
curl -X DELETE http://localhost:8080/api/users/{userId} \
  -H "Authorization: Bearer YOUR_RESTAURANT_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Deactivate User
```bash
curl -X PATCH http://localhost:8080/api/users/{userId}/deactivate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Activate User
```bash
curl -X PATCH http://localhost:8080/api/users/{userId}/activate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Customer Endpoints (Require CUSTOMER role)

#### Get My Orders
```bash
curl -X GET http://localhost:8080/api/orders/my-orders \
  -H "Authorization: Bearer YOUR_CUSTOMER_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_CUSTOMER_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 1,
    "items": [
      {
        "inventoryItemId": 1,
        "quantity": 2
      }
    ],
    "deliveryAddress": "123 Customer St"
  }'
```

### Restaurant Endpoints (Require RESTAURANT role)

#### Get My Inventory
```bash
curl -X GET http://localhost:8080/api/inventory/my-inventory \
  -H "Authorization: Bearer YOUR_RESTAURANT_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Add Inventory Item
```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Authorization: Bearer YOUR_RESTAURANT_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pizza Margherita",
    "description": "Fresh tomatoes, mozzarella, basil",
    "price": 12.99,
    "quantity": 50,
    "category": "PIZZA"
  }'
```

#### Update Inventory Item
```bash
curl -X PUT http://localhost:8080/api/inventory/{itemId} \
  -H "Authorization: Bearer YOUR_RESTAURANT_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pizza Margherita",
    "description": "Fresh tomatoes, mozzarella, basil",
    "price": 14.99,
    "quantity": 45,
    "category": "PIZZA"
  }'
```

## Example Usage Flow

### 1. Register and Login as Customer
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "CUSTOMER",
    "firstName": "John",
    "lastName": "Doe",
    "address": "123 Main St"
  }'

# Login (save the accessToken from response)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_doe",
    "password": "password123"
  }'
```

### 2. Register and Login as Restaurant
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "pizza_palace",
    "email": "admin@pizzapalace.com",
    "password": "password123",
    "role": "RESTAURANT",
    "firstName": "Pizza",
    "lastName": "Manager",
    "address": "456 Pizza Ave",
    "restaurantName": "Pizza Palace"
  }'

# Login (save the accessToken from response)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "pizza_palace",
    "password": "password123"
  }'
```

## Testing with Postman

1. **Import Collection**: Create a new Postman collection and add all the above requests
2. **Environment Variables**: Set up environment variables:
   - `base_url`: `http://localhost:8080`
   - `customer_token`: (Set after customer login)
   - `restaurant_token`: (Set after restaurant login)
3. **Authorization**: Use Bearer Token type with `{{customer_token}}` or `{{restaurant_token}}`

## Service Status

### Direct Service Access (For Development)
- **Auth Service**: http://localhost:8081
- **Order Service**: http://localhost:9094
- **Inventory Service**: http://localhost:9093
- **API Gateway**: http://localhost:8080

### Health Checks
```bash
# API Gateway
curl -X GET http://localhost:8080/actuator/health

# Auth Service
curl -X GET http://localhost:8081/actuator/health

# Order Service
curl -X GET http://localhost:9094/actuator/health

# Inventory Service
curl -X GET http://localhost:9093/actuator/health
```

## Docker Commands

### Start All Services
```bash
cd Backend
docker-compose up -d
```

### Check Service Status
```bash
docker ps
```

### View Service Logs
```bash
docker-compose logs authservice
docker-compose logs apigateway
docker-compose logs orderservice
docker-compose logs inventoryservice
```

### Stop All Services
```bash
docker-compose down
```

## Response Examples

### Successful Registration Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "role": "CUSTOMER",
    "isActive": true,
    "createdAt": "2025-08-24T18:27:24.411372",
    "updatedAt": "2025-08-24T18:27:24.411438",
    "address": "123 Test St"
  }
}
```

### Successful Login Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "role": "CUSTOMER",
    "isActive": true
  }
}
```

### Token Validation Response
```json
{
  "valid": true,
  "message": "Token is valid"
}
```

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Invalid or expired token"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied"
}
```

### 409 Conflict (User exists)
```json
{
  "timestamp": "2025-08-24T18:27:24.123456",
  "status": 409,
  "error": "User Already Exists",
  "message": "Username already exists: testuser"
}
```

## Notes

1. **JWT Tokens**: Save the `accessToken` from login responses to use in protected endpoints
2. **Token Expiry**: Tokens expire after 24 hours. Re-login if you get 401 errors
3. **Role-Based Access**: Customer endpoints require CUSTOMER role, Restaurant endpoints require RESTAURANT role
4. **CORS**: API Gateway is configured to allow requests from localhost:3000 and localhost:5173
5. **Content-Type**: Always use `application/json` for request headers

---

All services are running through Docker and accessible via the API Gateway at `http://localhost:8080`.
