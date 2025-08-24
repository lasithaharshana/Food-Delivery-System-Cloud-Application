# Authentication System Documentation

## Overview
This frontend application has been updated to connect with the real authentication API at `http://localhost:8081/api/auth/`.

## API Endpoints

### Register
- **URL**: `POST http://localhost:8081/api/auth/register`
- **Description**: Register a new user (customer or restaurant)

#### Request Body for Customer:
```json
{
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "role": "CUSTOMER",
  "address": "123 Main St, City"
}
```

#### Request Body for Restaurant:
```json
{
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "role": "RESTAURANT",
  "restaurantName": "John's Restaurant",
  "address": "123 Main St, City"
}
```

### Login
- **URL**: `POST http://localhost:8081/api/auth/login`
- **Description**: Authenticate existing user

#### Request Body:
```json
{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

## Response Format
Both endpoints return the same response structure:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 2,
    "username": "john_doe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "role": "CUSTOMER",
    "isActive": true,
    "createdAt": "2025-08-24T04:19:10.753064",
    "updatedAt": "2025-08-24T04:19:10.753104",
    "address": "123 Main St, City",
    "restaurantName": "John's Restaurant" // Only for RESTAURANT role
  }
}
```

## Frontend Implementation

### Authentication Context
The `AuthContext` manages the authentication state and provides:
- `login(credentials)`: Authenticate user
- `register(data)`: Register new user
- `logout()`: Clear authentication state
- `user`: Current user object
- `isAuthenticated`: Authentication status
- `accessToken`: JWT token for API calls

### Token Storage
- Access tokens are stored in `localStorage` as `accessToken`
- User data is stored in `localStorage` as `user`
- Utility functions in `lib/utils.ts` handle token management

### User Interface Updates
- **Dashboard**: Displays real user name and role
- **Profile**: Shows actual user information (name, email, phone, address, restaurant name)
- **Navigation**: Displays user's full name and role
- **Forms**: Updated to match API request format

### Role-Based Routing
- **CUSTOMER**: Routes to `/dashboard`, `/orders`, `/profile`
- **RESTAURANT**: Routes to `/restaurant`, `/restaurant/menu`, `/restaurant/orders`

## Usage Examples

### Registering a New Customer
```typescript
const { register } = useAuth();

await register({
  username: "john_doe",
  email: "john.doe@example.com",
  password: "password123",
  firstName: "John",
  lastName: "Doe",
  phoneNumber: "+1234567890",
  role: "CUSTOMER",
  address: "123 Main St, City"
});
```

### Logging In
```typescript
const { login } = useAuth();

await login({
  usernameOrEmail: "john_doe",
  password: "password123"
});
```

### Making Authenticated API Calls
```typescript
import { apiCall } from '@/lib/utils';

const response = await apiCall('/api/protected-endpoint', {
  method: 'POST',
  body: JSON.stringify(data)
});
```

## Security Features
- JWT tokens are automatically included in API requests
- Tokens are stored securely in localStorage
- Automatic token validation on app load
- Proper error handling for authentication failures

## Error Handling
The system handles various authentication errors:
- Invalid credentials
- User already exists
- Network errors
- Server errors

All errors are displayed to the user via toast notifications.

## Testing
To test the authentication system:
1. Start the backend server at `http://localhost:8081`
2. Start the frontend with `npm run dev`
3. Try registering a new user
4. Try logging in with existing credentials
5. Verify that user information is displayed correctly throughout the app
