# User Management API Documentation

This document describes all the user management endpoints available in the Blood Bank Management System.

## Base URL
```
/users
```

## Authentication
Most endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### 1. Get All Users
**GET** `/users`
- **Description**: Retrieve all users (Admin only)
- **Authorization**: `ADMIN` role required
- **Response**: List of all users

### 2. Get User by ID
**GET** `/users/{id}`
- **Description**: Retrieve a specific user by ID
- **Authorization**: `ADMIN` role or own profile
- **Parameters**: `id` - User ID
- **Response**: User object or 404 if not found

### 3. Get Current User Profile
**GET** `/users/profile`
- **Description**: Retrieve the currently authenticated user's profile
- **Authorization**: Authenticated user
- **Response**: Current user's profile

### 4. Get All Donors
**GET** `/users/donors`
- **Description**: Retrieve all active donors
- **Authorization**: Public
- **Response**: List of active donors

### 5. Get Donors by Blood Type
**GET** `/users/donors/{bloodType}`
- **Description**: Retrieve donors with a specific blood type
- **Authorization**: Public
- **Parameters**: `bloodType` - Blood type (e.g., A_POSITIVE, O_NEGATIVE)
- **Response**: List of donors with specified blood type

### 6. Search Users (Simple)
**GET** `/users/search`
- **Description**: Search users with query parameters
- **Authorization**: Public
- **Query Parameters**:
  - `name` (optional): Search by name
  - `bloodType` (optional): Filter by blood type
  - `role` (optional): Filter by role
  - `active` (optional): Filter by active status
- **Response**: List of matching users

### 7. Search Users (Advanced)
**POST** `/users/search`
- **Description**: Advanced user search with request body
- **Authorization**: Public
- **Request Body**: `UserSearchRequest` object
- **Response**: List of matching users

### 8. Get User Statistics
**GET** `/users/stats`
- **Description**: Get comprehensive user statistics (Admin only)
- **Authorization**: `ADMIN` role required
- **Response**: Statistics object with counts and breakdowns

### 9. Update User
**PUT** `/users/{id}`
- **Description**: Update a specific user
- **Authorization**: `ADMIN` role or own profile
- **Parameters**: `id` - User ID
- **Request Body**: `UserUpdateRequest` object
- **Response**: Updated user object

### 10. Update Current User Profile
**PUT** `/users/profile`
- **Description**: Update the currently authenticated user's profile
- **Authorization**: Authenticated user
- **Request Body**: `UserUpdateRequest` object
- **Response**: Updated user profile

### 11. Change Password (Specific User)
**PUT** `/users/{id}/password`
- **Description**: Change password for a specific user
- **Authorization**: `ADMIN` role or own profile
- **Parameters**: `id` - User ID
- **Request Body**: `PasswordChangeRequest` object
- **Response**: Success message

### 12. Change Current User Password
**PUT** `/users/password`
- **Description**: Change the currently authenticated user's password
- **Authorization**: Authenticated user
- **Request Body**: `PasswordChangeRequest` object
- **Response**: Success message

### 13. Activate User
**PUT** `/users/{id}/activate`
- **Description**: Activate a deactivated user
- **Authorization**: `ADMIN` role required
- **Parameters**: `id` - User ID
- **Response**: Activated user object

### 14. Deactivate User
**PUT** `/users/{id}/deactivate`
- **Description**: Deactivate an active user
- **Authorization**: `ADMIN` role required
- **Parameters**: `id` - User ID
- **Response**: Deactivated user object

### 15. Delete User
**DELETE** `/users/{id}`
- **Description**: Soft delete a user (sets active to false)
- **Authorization**: `ADMIN` role required
- **Parameters**: `id` - User ID
- **Response**: 204 No Content

### 16. Bulk Activate Users
**POST** `/users/bulk-activate`
- **Description**: Activate multiple users at once
- **Authorization**: `ADMIN` role required
- **Request Body**: List of user IDs
- **Response**: Success message with count

### 17. Bulk Deactivate Users
**POST** `/users/bulk-deactivate`
- **Description**: Deactivate multiple users at once
- **Authorization**: `ADMIN` role required
- **Request Body**: List of user IDs
- **Response**: Success message with count

### 18. Validate Username
**GET** `/users/validate-username/{username}`
- **Description**: Check if a username is available
- **Authorization**: Public
- **Parameters**: `username` - Username to validate
- **Response**: Object with availability status

### 19. Validate Email
**GET** `/users/validate-email/{email}`
- **Description**: Check if an email is available
- **Authorization**: Public
- **Parameters**: `email` - Email to validate
- **Response**: Object with availability status

### 20. Get Available Roles
**GET** `/users/roles`
- **Description**: Get all available user roles
- **Authorization**: Public
- **Response**: Array of role values

### 21. Get Available Blood Types
**GET** `/users/blood-types`
- **Description**: Get all available blood types
- **Authorization**: Public
- **Response**: Array of blood type values

## Data Transfer Objects (DTOs)

### UserUpdateRequest
```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phoneNumber": "string",
  "bloodType": "A_POSITIVE|A_NEGATIVE|B_POSITIVE|B_NEGATIVE|AB_POSITIVE|AB_NEGATIVE|O_POSITIVE|O_NEGATIVE",
  "role": "ADMIN|DOCTOR|NURSE|TECHNICIAN|DONOR|RECIPIENT",
  "isActive": "boolean"
}
```

### PasswordChangeRequest
```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

### UserSearchRequest
```json
{
  "name": "string",
  "bloodType": "A_POSITIVE|A_NEGATIVE|B_POSITIVE|B_NEGATIVE|AB_POSITIVE|AB_NEGATIVE|O_POSITIVE|O_NEGATIVE",
  "role": "ADMIN|DOCTOR|NURSE|TECHNICIAN|DONOR|RECIPIENT",
  "isActive": "boolean",
  "page": "integer",
  "size": "integer"
}
```

## User Roles
- **ADMIN**: Full system access
- **DOCTOR**: Medical staff with elevated privileges
- **NURSE**: Nursing staff
- **TECHNICIAN**: Laboratory technicians
- **DONOR**: Blood donors
- **RECIPIENT**: Blood recipients

## Blood Types
- **A_POSITIVE**: A+
- **A_NEGATIVE**: A-
- **B_POSITIVE**: B+
- **B_NEGATIVE**: B-
- **AB_POSITIVE**: AB+
- **AB_NEGATIVE**: AB-
- **O_POSITIVE**: O+
- **O_NEGATIVE**: O-

## Error Responses

### Common HTTP Status Codes
- **200**: Success
- **201**: Created
- **204**: No Content
- **400**: Bad Request
- **401**: Unauthorized
- **403**: Forbidden
- **404**: Not Found
- **500**: Internal Server Error

### Error Response Format
```json
{
  "error": "Error message",
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/users/123"
}
```

## Security Notes

1. **Password Security**: Passwords are hashed using BCrypt before storage
2. **Role-Based Access**: Different endpoints require different user roles
3. **Profile Access**: Users can only access their own profile unless they have ADMIN role
4. **Soft Delete**: User deletion is soft (sets active to false) rather than hard delete
5. **Input Validation**: All inputs are validated for security and data integrity

## Rate Limiting
Currently, no rate limiting is implemented. Consider implementing rate limiting for production use.

## Logging
All user operations are logged for audit purposes, including:
- User creation, updates, and deletions
- Password changes
- Role changes
- Login attempts
- Bulk operations
