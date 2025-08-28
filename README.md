# Blood Bank Management System

A comprehensive blood bank management system built with Java Spring Boot and PostgreSQL, featuring a Swing desktop client for easy management.

## Features

- **User Management**: Admin, Doctor, Nurse, Technician, Donor, and Recipient roles
- **Blood Inventory Management**: Track blood units, expiry dates, and availability
- **Donation Tracking**: Record and manage blood donations with health screening data
- **Request Management**: Handle blood requests from hospitals with priority levels
- **Security**: JWT-based authentication and role-based access control
- **Desktop Client**: Swing-based GUI for easy system management

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **Desktop Client**: Java Swing
- **API**: RESTful web services

## Prerequisites

- Java 17 or later
- Maven 3.6+
- PostgreSQL 12+
- pgAdmin (optional, for database management)

## Quick Start

### 1. Database Setup

Your PostgreSQL is already running on port 5432. Create the database:

```sql
CREATE DATABASE bloodbank;
```

Run the initialization script:
```bash
psql -U rahul -d bloodbank -f database/init.sql
```

### 2. Update Configuration

Update `src/main/resources/application.properties` with your PostgreSQL credentials:
```properties
spring.datasource.username=rahul
spring.datasource.password=8004
```

### 3. Build and Run

Build the application:
```bash
mvn clean install -DskipTests
```

Run the Spring Boot application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080/api`

### 4. Run Desktop Client

**On macOS/Linux:**
```bash
chmod +x run-swing-client.sh
./run-swing-client.sh
```

**On Windows:**
```bash
run-swing-client.bat
```

## Default Login Credentials

- **Admin**: `admin` / `admin123`
- **Doctor**: `doctor1` / `doctor123`
- **Nurse**: `nurse1` / `nurse123`
- **Technician**: `tech1` / `tech123`
- **Donor**: `donor1` / `donor123`

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Token validation

### Users
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

### Blood Inventory
- `GET /api/inventory` - Get all inventory
- `GET /api/inventory/{id}` - Get inventory by ID
- `POST /api/inventory/add` - Add blood unit
- `PUT /api/inventory/{id}` - Update inventory
- `POST /api/inventory/{id}/remove` - Remove blood unit

## Project Structure

```
src/main/java/com/bloodbank/
├── BloodBankApplication.java          # Main application class
├── config/                           # Configuration classes
│   ├── SecurityConfig.java          # Spring Security configuration
│   └── JwtAuthenticationFilter.java # JWT authentication filter
├── controller/                       # REST controllers
│   ├── AuthController.java          # Authentication endpoints
│   ├── UserController.java          # User management
│   └── BloodInventoryController.java # Inventory management
├── entity/                          # JPA entities
│   ├── User.java                   # User entity
│   ├── BloodInventory.java         # Blood inventory entity
│   ├── Donation.java               # Donation entity
│   └── Request.java                # Blood request entity
├── repository/                      # Data access layer
│   ├── UserRepository.java         # User repository
│   └── BloodInventoryRepository.java # Inventory repository
├── service/                         # Business logic layer
│   ├── UserService.java            # User management service
│   ├── BloodInventoryService.java  # Inventory service
│   ├── AuthService.java            # Authentication service
│   └── JwtService.java             # JWT service
├── dto/                            # Data Transfer Objects
│   ├── AuthRequest.java            # Login request
│   ├── AuthResponse.java           # Authentication response
│   └── RegisterRequest.java        # Registration request
└── client/                         # Desktop client
    └── BloodBankSwingClient.java   # Swing GUI application
```

## Desktop Client Features

The Swing desktop client provides:
- **Login Panel**: Secure authentication
- **Blood Inventory Tab**: View and manage blood units
- **Users Tab**: Manage system users and donors
- **Donations Tab**: Track blood donations
- **Requests Tab**: Handle blood requests from hospitals

## Troubleshooting

### Common Issues

1. **Database Connection Error**: Ensure PostgreSQL is running and credentials are correct
2. **Port Already in Use**: Change server.port in application.properties
3. **Build Errors**: Run `mvn clean install -DskipTests` to skip tests during initial setup

### Logs

Check application logs for detailed error information. Logging is configured at DEBUG level for development.

## Development

### Adding New Features

1. Create entity classes in the `entity` package
2. Add repository interfaces in the `repository` package
3. Implement business logic in the `service` package
4. Create REST endpoints in the `controller` package
5. Update the Swing client to include new functionality

### Testing

Run tests with:
```bash
mvn test
```

## License

This project is for educational and demonstration purposes.

## Support

For issues or questions, check the logs and ensure all prerequisites are met.
