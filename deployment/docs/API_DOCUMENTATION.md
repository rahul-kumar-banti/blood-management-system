# Blood Bank Management System - API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All API endpoints (except authentication endpoints) require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### 1. Authentication Endpoints

#### POST /auth/register
Register a new user in the system.

**Request Body:**
```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "role": "DONOR",
  "bloodType": "O_POSITIVE"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "newuser",
  "role": "DONOR"
}
```

**Status Codes:**
- `200 OK`: Registration successful
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Username or email already exists

#### POST /auth/login
Authenticate a user and receive a JWT token.

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

**Status Codes:**
- `200 OK`: Login successful
- `401 Unauthorized`: Invalid credentials
- `400 Bad Request`: Missing username or password

#### POST /auth/validate
Validate a JWT token.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
true
```

**Status Codes:**
- `200 OK`: Token is valid
- `401 Unauthorized`: Token is invalid or expired

#### POST /auth/change-password
Change user password. **Authenticated users only.**

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Request Body:**
```json
{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123"
}
```

**Response:**
```json
{
  "message": "Password changed successfully"
}
```

**Status Codes:**
- `200 OK`: Password changed successfully
- `400 Bad Request`: Invalid current password
- `401 Unauthorized`: Invalid token

### 2. User Management Endpoints

#### GET /users
Get all users in the system. **Admin only.**

**Headers:**
```
Authorization: Bearer <admin-jwt-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@bloodbank.com",
    "firstName": "System",
    "lastName": "Administrator",
    "phoneNumber": null,
    "role": "ADMIN",
    "bloodType": "O_POSITIVE",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

**Status Codes:**
- `200 OK`: Users retrieved successfully
- `403 Forbidden`: Insufficient permissions

#### GET /users/{id}
Get a specific user by ID.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Path Parameters:**
- `id`: User ID (Long)

**Response:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@bloodbank.com",
  "firstName": "System",
  "lastName": "Administrator",
  "role": "ADMIN",
  "bloodType": "O_POSITIVE",
  "isActive": true
}
```

**Status Codes:**
- `200 OK`: User found
- `404 Not Found`: User not found
- `403 Forbidden`: Insufficient permissions

#### GET /users/search
Search users by various criteria. **Admin only.**

**Headers:**
```
Authorization: Bearer <admin-jwt-token>
```

**Query Parameters:**
- `username`: Username filter (optional)
- `email`: Email filter (optional)
- `role`: Role filter (optional)
- `bloodType`: Blood type filter (optional)
- `isActive`: Active status filter (optional)

**Response:**
```json
[
  {
    "id": 5,
    "username": "donor1",
    "firstName": "Alice",
    "lastName": "Brown",
    "role": "DONOR",
    "bloodType": "O_POSITIVE",
    "isActive": true
  }
]
```

**Status Codes:**
- `200 OK`: Search results retrieved
- `403 Forbidden`: Insufficient permissions

#### GET /users/donors
Get all active donors in the system.

**Response:**
```json
[
  {
    "id": 5,
    "username": "donor1",
    "firstName": "Alice",
    "lastName": "Brown",
    "role": "DONOR",
    "bloodType": "O_POSITIVE",
    "isActive": true
  }
]
```

**Status Codes:**
- `200 OK`: Donors retrieved successfully

#### GET /users/donors/{bloodType}
Get donors by specific blood type.

**Path Parameters:**
- `bloodType`: Blood type (String, e.g., "O_POSITIVE", "A_NEGATIVE")

**Response:**
```json
[
  {
    "id": 5,
    "username": "donor1",
    "firstName": "Alice",
    "lastName": "Brown",
    "role": "DONOR",
    "bloodType": "O_POSITIVE",
    "isActive": true
  }
]
```

**Status Codes:**
- `200 OK`: Donors found
- `400 Bad Request`: Invalid blood type

#### PUT /users/{id}
Update a user's information.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Path Parameters:**
- `id`: User ID (Long)

**Request Body:**
```json
{
  "firstName": "Updated",
  "lastName": "Name",
  "email": "updated@example.com",
  "phoneNumber": "+1234567890",
  "bloodType": "A_POSITIVE"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "admin",
  "firstName": "Updated",
  "lastName": "Name",
  "email": "updated@example.com",
  "role": "ADMIN",
  "bloodType": "A_POSITIVE",
  "isActive": true
}
```

**Status Codes:**
- `200 OK`: User updated successfully
- `404 Not Found`: User not found
- `403 Forbidden`: Insufficient permissions

#### DELETE /users/{id}
Deactivate a user. **Admin only.**

**Headers:**
```
Authorization: Bearer <admin-jwt-token>
```

**Path Parameters:**
- `id`: User ID (Long)

**Status Codes:**
- `204 No Content`: User deactivated successfully
- `404 Not Found`: User not found
- `403 Forbidden`: Insufficient permissions

### 3. Blood Inventory Endpoints

#### GET /inventory
Get all blood inventory items.

**Response:**
```json
[
  {
    "id": 1,
    "bloodType": "O_POSITIVE",
    "quantity": 500,
    "unitOfMeasure": "ml",
    "expiryDate": "2024-02-01T00:00:00",
    "collectionDate": "2024-01-01T00:00:00",
    "donorId": 5,
    "batchNumber": "BATCH001",
    "status": "AVAILABLE",
    "notes": "Fresh donation"
  }
]
```

**Status Codes:**
- `200 OK`: Inventory retrieved successfully

#### GET /inventory/{id}
Get a specific inventory item by ID.

**Path Parameters:**
- `id`: Inventory ID (Long)

**Response:**
```json
{
  "id": 1,
  "bloodType": "O_POSITIVE",
  "quantity": 500,
  "unitOfMeasure": "ml",
  "expiryDate": "2024-02-01T00:00:00",
  "status": "AVAILABLE"
}
```

**Status Codes:**
- `200 OK`: Inventory item found
- `404 Not Found`: Inventory item not found

#### GET /inventory/type/{bloodType}
Get inventory items by blood type.

**Path Parameters:**
- `bloodType`: Blood type (String)

**Response:**
```json
[
  {
    "id": 1,
    "bloodType": "O_POSITIVE",
    "quantity": 500,
    "status": "AVAILABLE"
  }
]
```

**Status Codes:**
- `200 OK`: Inventory items found
- `400 Bad Request`: Invalid blood type

#### GET /inventory/available
Get all available (non-expired) inventory items.

**Response:**
```json
[
  {
    "id": 1,
    "bloodType": "O_POSITIVE",
    "quantity": 500,
    "status": "AVAILABLE",
    "expiryDate": "2024-02-01T00:00:00"
  }
]
```

**Status Codes:**
- `200 OK`: Available inventory retrieved

#### GET /inventory/expiring-soon
Get inventory items expiring within specified days. **Admin, Technician, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Query Parameters:**
- `days`: Number of days (default: 7)

**Response:**
```json
[
  {
    "id": 2,
    "bloodType": "A_NEGATIVE",
    "quantity": 100,
    "status": "AVAILABLE",
    "expiryDate": "2024-01-05T00:00:00",
    "daysUntilExpiry": 3
  }
]
```

**Status Codes:**
- `200 OK`: Expiring inventory retrieved
- `403 Forbidden`: Insufficient permissions

#### POST /inventory/add
Add a new blood unit to inventory. **Admin, Technician, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "bloodType": "A_POSITIVE",
  "quantity": 300,
  "unitOfMeasure": "ml",
  "expiryDate": "2024-02-15T00:00:00",
  "collectionDate": "2024-01-15T00:00:00",
  "donorId": 6,
  "batchNumber": "BATCH006",
  "status": "AVAILABLE",
  "notes": "Regular donation"
}
```

**Response:**
```json
{
  "id": 6,
  "bloodType": "A_POSITIVE",
  "quantity": 300,
  "status": "AVAILABLE"
}
```

**Status Codes:**
- `200 OK`: Blood unit added successfully
- `400 Bad Request`: Invalid input data
- `403 Forbidden`: Insufficient permissions

#### PUT /inventory/{id}
Update an inventory item. **Admin, Technician, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Path Parameters:**
- `id`: Inventory ID (Long)

**Request Body:**
```json
{
  "quantity": 400,
  "status": "RESERVED",
  "notes": "Updated notes"
}
```

**Response:**
```json
{
  "id": 1,
  "bloodType": "O_POSITIVE",
  "quantity": 400,
  "status": "RESERVED",
  "notes": "Updated notes"
}
```

**Status Codes:**
- `200 OK`: Inventory updated successfully
- `404 Not Found`: Inventory item not found
- `403 Forbidden`: Insufficient permissions

#### POST /inventory/{id}/remove
Remove blood units from inventory. **Admin, Doctor, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Path Parameters:**
- `id`: Inventory ID (Long)

**Query Parameters:**
- `quantity`: Amount to remove (Integer)

**Status Codes:**
- `204 No Content`: Blood units removed successfully
- `400 Bad Request`: Insufficient quantity
- `404 Not Found`: Inventory item not found
- `403 Forbidden`: Insufficient permissions

#### GET /inventory/expired
Get expired inventory items. **Admin or Technician only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
[
  {
    "id": 2,
    "bloodType": "A_NEGATIVE",
    "quantity": 100,
    "status": "EXPIRED",
    "expiryDate": "2024-01-01T00:00:00"
  }
]
```

**Status Codes:**
- `200 OK`: Expired inventory retrieved
- `403 Forbidden`: Insufficient permissions

#### POST /inventory/cleanup-expired
Clean up expired inventory items. **Admin or Technician only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "message": "Expired inventory cleaned up successfully",
  "itemsRemoved": 5
}
```

**Status Codes:**
- `200 OK`: Cleanup completed
- `403 Forbidden`: Insufficient permissions

### 4. Donation Management Endpoints

#### GET /donations
Get all donation records. **Admin, Doctor, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "donorId": 5,
    "bloodType": "O_POSITIVE",
    "quantity": 500,
    "donationDate": "2024-01-01T00:00:00",
    "status": "APPROVED",
    "healthScreeningPassed": true
  }
]
```

**Status Codes:**
- `200 OK`: Donations retrieved successfully
- `403 Forbidden`: Insufficient permissions

#### POST /donations
Create a new donation record. **Admin, Doctor, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "donorId": 5,
  "bloodType": "O_POSITIVE",
  "quantity": 500,
  "unitOfMeasure": "ml",
  "healthScreeningPassed": true,
  "hemoglobinLevel": 14.2,
  "bloodPressure": "120/80",
  "pulseRate": 72,
  "temperature": 98.6
}
```

**Response:**
```json
{
  "id": 6,
  "donorId": 5,
  "bloodType": "O_POSITIVE",
  "quantity": 500,
  "status": "APPROVED"
}
```

**Status Codes:**
- `200 OK`: Donation created successfully
- `400 Bad Request`: Invalid input data
- `403 Forbidden`: Insufficient permissions

### 5. Request Management Endpoints

#### GET /requests
Get all blood requests. **Admin, Doctor, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "hospitalName": "City General Hospital",
    "patientName": "Patient A",
    "bloodType": "O_POSITIVE",
    "quantity": 200,
    "priority": "HIGH",
    "status": "PENDING"
  }
]
```

**Status Codes:**
- `200 OK`: Requests retrieved successfully
- `403 Forbidden`: Insufficient permissions

#### POST /requests
Create a new blood request. **Doctor or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "hospitalName": "City General Hospital",
  "patientName": "Patient D",
  "bloodType": "A_NEGATIVE",
  "quantity": 250,
  "unitOfMeasure": "ml",
  "requiredDate": "2024-01-20T00:00:00",
  "priority": "URGENT",
  "reason": "Emergency surgery",
  "doctorName": "Dr. Smith",
  "contactNumber": "+1234567890"
}
```

**Response:**
```json
{
  "id": 4,
  "hospitalName": "City General Hospital",
  "patientName": "Patient D",
  "bloodType": "A_NEGATIVE",
  "quantity": 250,
  "status": "PENDING"
}
```

**Status Codes:**
- `200 OK`: Request created successfully
- `400 Bad Request`: Invalid input data
- `403 Forbidden`: Insufficient permissions

#### PUT /requests/{id}/status
Update request status. **Admin, Doctor, or Nurse only.**

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Path Parameters:**
- `id`: Request ID (Long)

**Request Body:**
```json
{
  "status": "APPROVED",
  "notes": "Blood units allocated"
}
```

**Response:**
```json
{
  "id": 1,
  "status": "APPROVED",
  "notes": "Blood units allocated"
}
```

**Status Codes:**
- `200 OK`: Status updated successfully
- `404 Not Found`: Request not found
- `403 Forbidden`: Insufficient permissions

### 6. System Health Endpoints

#### GET /health
Get system health status.

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-01T00:00:00",
  "database": "UP",
  "version": "1.0.0"
}
```

**Status Codes:**
- `200 OK`: System is healthy
- `503 Service Unavailable`: System issues detected

## Error Responses

All endpoints return consistent error responses:

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/endpoint"
}
```

## Rate Limiting

Currently, no rate limiting is implemented. Consider implementing rate limiting for production use.

## CORS

CORS is enabled for all origins (`*`) for development purposes. Restrict this in production.

## Security Notes

- Passwords are hashed using BCrypt
- JWT tokens expire after 24 hours
- Role-based access control is enforced at the endpoint level
- All sensitive operations require authentication
- Consider implementing HTTPS in production

## API Versioning

The current API version is v1. Future versions will be available at `/api/v2`, `/api/v3`, etc.

## Pagination

For endpoints that return large datasets, pagination parameters are supported:
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)
- `sort`: Sort field (default: "id")

Example:
```
GET /api/users?page=0&size=10&sort=username
```

## Response Headers

All responses include standard HTTP headers:
- `Content-Type: application/json`
- `X-Request-ID`: Unique request identifier for tracking
- `X-Total-Count`: Total number of items (for paginated responses)
