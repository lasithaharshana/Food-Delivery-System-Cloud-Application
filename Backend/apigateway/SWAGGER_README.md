# API Gateway Swagger Documentation

## Accessing Swagger UI

Once the API Gateway service is running, you can access the Swagger documentation at:

- **Swagger UI**: http://localhost:8080/docs
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Features

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration  
- `GET /api/auth/validate` - JWT token validation

### User Management Endpoints
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users` - Get all users (with pagination)
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/active` - Get active users
- `GET /api/users/inactive` - Get inactive users
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (requires RESTAURANT role)
- `PATCH /api/users/{id}/activate` - Activate user
- `PATCH /api/users/{id}/deactivate` - Deactivate user

## Authentication

Most endpoints require JWT authentication. In Swagger UI:

1. Click the "Authorize" button
2. Enter: `Bearer <your-jwt-token>`
3. Click "Authorize"

## Example JWT Token

To get a JWT token, first register or login using the authentication endpoints.

Example login request:
```json
{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

## Configuration

The Swagger UI is configured with the following settings:
- Operations sorted by HTTP method
- Tags sorted alphabetically  
- "Try it out" feature enabled
- Filtering enabled for easy navigation

## Development

To run the API Gateway locally:
```bash
mvn spring-boot:run
```

The service will start on port 8080 and Swagger UI will be available at `/docs`.
