# Auth Service

This is the authentication service for the Food Delivery System Cloud Application. It provides user registration, login functionality with JWT tokens, and comprehensive user management capabilities.

## Features

- **User Registration**: Support for both CUSTOMER and RESTAURANT roles
- **User Authentication**: Login with JWT token generation
- **Role-based Authorization**: Different permissions for customers and restaurants
- **User Management**: Complete CRUD operations for users
- **JWT Security**: Secure token-based authentication
- **Swagger Documentation**: Interactive API documentation with authentication
- **MySQL Database**: Persistent data storage
- **Docker Support**: Containerized deployment
- **Health Checks**: Built-in monitoring endpoints

## Tech Stack

- **Java 21**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **MySQL**
- **JWT (JSON Web Tokens)**
- **Swagger/OpenAPI 3**
- **Docker**
- **Maven**

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login

### User Management Endpoints (Authenticated)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users` - Get all users (with pagination support)
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/active` - Get active users
- `GET /api/users/inactive` - Get inactive users
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Restaurant role only)
- `PATCH /api/users/{id}/activate` - Activate user
- `PATCH /api/users/{id}/deactivate` - Deactivate user

### Documentation
- `GET /swagger-ui.html` - Swagger UI
- `GET /api-docs` - OpenAPI specification

### Health Check
- `GET /actuator/health` - Service health status

## Configuration

### Application Properties

```properties
# Server Configuration
server.port=8081

# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=password

# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890123456789012345678901234567890
jwt.expiration=86400000
```

## User Roles

### CUSTOMER
- Can register and login
- Can view and update their own profile
- Standard customer permissions

### RESTAURANT
- Can register and login
- Must provide restaurant name and address during registration
- Can perform all user management operations
- Can delete users
- Enhanced permissions for restaurant management

## Authentication

The service uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Running the Application

### Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.6+

### Local Development

1. Clone the repository
2. Configure MySQL database settings in `application.properties`
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Docker Deployment

1. Build the Docker image:
   ```bash
   docker build -t auth-service .
   ```

2. Run the container:
   ```bash
   docker run -p 8081:8081 \
     -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/auth_db \
     -e SPRING_DATASOURCE_USERNAME=root \
     -e SPRING_DATASOURCE_PASSWORD=password \
     auth-service
   ```

## API Documentation

Once the application is running, you can access the interactive API documentation at:
- http://localhost:8081/swagger-ui.html

The Swagger UI includes:
- Complete API documentation
- Interactive testing capabilities
- JWT authentication support
- Request/response examples

## Authentication in Swagger

1. Start the application
2. Navigate to http://localhost:8081/swagger-ui.html
3. Register a new user using the `/api/auth/register` endpoint
4. Login using the `/api/auth/login` endpoint to get a JWT token
5. Click the "Authorize" button in Swagger UI
6. Enter `Bearer <your-jwt-token>` in the authorization field
7. You can now test authenticated endpoints

## Database Schema

The service automatically creates the following table:

### Users Table
- `id` (BIGINT, Primary Key)
- `username` (VARCHAR, Unique)
- `email` (VARCHAR, Unique)
- `password` (VARCHAR, Encrypted)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `phone_number` (VARCHAR)
- `role` (ENUM: CUSTOMER, RESTAURANT)
- `is_active` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- `restaurant_name` (VARCHAR, for restaurant users)
- `restaurant_address` (VARCHAR, for restaurant users)

## Error Handling

The service includes comprehensive error handling with meaningful error messages:

- `400 Bad Request` - Invalid input data
- `401 Unauthorized` - Authentication required
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `500 Internal Server Error` - Server error

## Security Features

- Password encryption using BCrypt
- JWT token-based authentication
- Role-based authorization
- CORS configuration
- Input validation
- SQL injection prevention
- XSS protection

## Testing

Run tests with:
```bash
./mvnw test
```

The service includes:
- Unit tests for services
- Integration tests for controllers
- Security tests

## Monitoring

Health check endpoint provides:
- Application status
- Database connectivity
- System metrics

Access monitoring at: http://localhost:8081/actuator/health

## Environment Variables

For production deployment, configure these environment variables:

- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `JWT_EXPIRATION` - Token expiration time (milliseconds)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
