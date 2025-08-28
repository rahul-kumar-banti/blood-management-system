# Blood Bank Management System

A comprehensive blood bank management system built with Java Spring Boot and PostgreSQL, featuring a Swing desktop client for easy management.

## 🚀 Features

- **User Management**: Admin, Doctor, Nurse, Technician, Donor, and Recipient roles
- **Blood Inventory Management**: Track blood units, expiry dates, and availability
- **Donation Tracking**: Record and manage blood donations with health screening data
- **Request Management**: Handle blood requests from hospitals with priority levels
- **Security**: JWT-based authentication and role-based access control
- **Desktop Client**: Swing-based GUI for easy system management
- **RESTful API**: Comprehensive web services for integration
- **Real-time Monitoring**: Health checks and system status monitoring
- **Reporting**: Advanced analytics and reporting capabilities
- **Audit Trail**: Complete tracking of all system activities

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: PostgreSQL 12+
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with JWT
- **Build Tool**: Maven 3.6+
- **Desktop Client**: Java Swing
- **API**: RESTful web services
- **Documentation**: Markdown with comprehensive guides

## 📋 Prerequisites

- Java 17 or later
- Maven 3.6+
- PostgreSQL 12+
- pgAdmin (optional, for database management)

## 🚀 Quick Start

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

## 🔐 Default Login Credentials

- **Admin**: `admin` / `admin123`
- **Doctor**: `doctor1` / `doctor123`
- **Nurse**: `nurse1` / `nurse123`
- **Technician**: `tech1` / `tech123`
- **Donor**: `donor1` / `donor123`

## 📚 Documentation

### Core Documentation
- **[API Documentation](docs/API_DOCUMENTATION.md)** - Complete REST API reference
- **[User Manual](docs/USER_MANUAL.md)** - Comprehensive user guide
- **[Deployment Guide](docs/DEPLOYMENT_GUIDE.md)** - Production deployment instructions
- **[Terminology](docs/TERMINOLOGY.md)** - Medical and technical terms

### Quick Reference
- **[Setup Guide](SETUP_POSTGRES.md)** - PostgreSQL setup instructions
- **README** - This file with overview and quick start

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Token validation
- `POST /api/auth/change-password` - Password change

### Users
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/search` - Search users by criteria
- `GET /api/users/donors` - Get all donors
- `GET /api/users/donors/{bloodType}` - Get donors by blood type
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

### Blood Inventory
- `GET /api/inventory` - Get all inventory
- `GET /api/inventory/{id}` - Get inventory by ID
- `GET /api/inventory/type/{bloodType}` - Get by blood type
- `GET /api/inventory/available` - Get available units
- `GET /api/inventory/expiring-soon` - Get expiring units
- `GET /api/inventory/expired` - Get expired units
- `POST /api/inventory/add` - Add blood unit
- `PUT /api/inventory/{id}` - Update inventory
- `POST /api/inventory/{id}/remove` - Remove blood unit
- `POST /api/inventory/cleanup-expired` - Clean expired units

### Donations
- `GET /api/donations` - Get all donations
- `POST /api/donations` - Create donation record

### Requests
- `GET /api/requests` - Get all requests
- `POST /api/requests` - Create blood request
- `PUT /api/requests/{id}/status` - Update request status

### System Health
- `GET /api/health` - System health status

## 🏗️ Project Structure

```
src/main/java/com/bloodbank/
├── BloodBankApplication.java          # Main application class
├── config/                           # Configuration classes
│   ├── SecurityConfig.java          # Spring Security configuration
│   └── JwtAuthenticationFilter.java # JWT authentication filter
├── controller/                       # REST controllers
│   ├── AuthController.java          # Authentication endpoints
│   ├── UserController.java          # User management
│   ├── BloodInventoryController.java # Inventory management
│   ├── HealthController.java        # Health monitoring
│   └── DonationController.java      # Donation management
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
│   ├── RegisterRequest.java        # Registration request
│   ├── PasswordChangeRequest.java  # Password change
│   ├── UserSearchRequest.java      # User search criteria
│   └── UserUpdateRequest.java      # User update data
└── client/                         # Desktop client
    └── BloodBankSwingClient.java   # Swing GUI application
```

## 🖥️ Desktop Client Features

The Swing desktop client provides:
- **Login Panel**: Secure authentication
- **Dashboard**: System overview and quick actions
- **Blood Inventory Tab**: View and manage blood units
- **Users Tab**: Manage system users and donors
- **Donations Tab**: Track blood donations
- **Requests Tab**: Handle blood requests from hospitals
- **Reports Tab**: Generate and export reports

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Granular permissions by user role
- **Password Security**: BCrypt hashing with salt
- **Session Management**: Configurable token expiry
- **Input Validation**: Comprehensive data validation
- **Audit Logging**: Complete activity tracking

## 📊 Monitoring and Health

- **Health Checks**: System and database status
- **Performance Metrics**: Response times and throughput
- **Error Tracking**: Comprehensive error logging
- **System Resources**: Memory and disk usage monitoring

## 🚀 Deployment Options

### Development
- Direct JAR execution
- Maven Spring Boot plugin
- Hot reload with spring-boot-devtools

### Production
- Systemd service
- Docker containers
- Load balancer with Nginx
- SSL/TLS encryption
- Database clustering

## 🧪 Testing

Run tests with:
```bash
mvn test
```

Test coverage includes:
- Unit tests for services
- Integration tests for controllers
- Repository layer testing
- Security configuration testing

## 🔧 Configuration

### Environment Profiles
- **Development**: Debug logging, auto DDL updates
- **Production**: Info logging, DDL validation
- **Testing**: Test database, minimal logging

### Customization
- Database connection settings
- JWT configuration
- Logging levels
- Server ports and context paths

## 📈 Performance Optimization

- **Connection Pooling**: HikariCP for database connections
- **Caching**: Hibernate second-level cache
- **Query Optimization**: Database indexing strategies
- **JVM Tuning**: Garbage collection optimization
- **Load Balancing**: Multiple application instances

## 🔄 Database Management

### Schema Management
- **Auto DDL**: Development mode schema updates
- **Migration Scripts**: Production schema changes
- **Data Initialization**: Sample data and users
- **Backup Strategies**: Automated backup procedures

### Optimization
- **Indexing**: Performance-critical queries
- **Partitioning**: Large table management
- **Connection Pooling**: Efficient resource usage
- **Query Analysis**: Performance monitoring

## 🌐 API Integration

### RESTful Design
- **Standard HTTP Methods**: GET, POST, PUT, DELETE
- **JSON Data Format**: Consistent request/response structure
- **Status Codes**: Proper HTTP status code usage
- **Error Handling**: Comprehensive error responses

### Client Libraries
- **Java HTTP Client**: Built-in HTTP client usage
- **JavaScript/TypeScript**: Fetch API examples
- **Python**: Requests library examples
- **cURL**: Command-line testing

## 📱 Future Enhancements

### Planned Features
- **Mobile Application**: iOS and Android clients
- **Web Dashboard**: Browser-based management interface
- **Real-time Notifications**: WebSocket-based updates
- **Advanced Reporting**: Business intelligence dashboards
- **Machine Learning**: Predictive analytics for inventory

### Scalability Improvements
- **Microservices Architecture**: Service decomposition
- **Message Queues**: Asynchronous processing
- **Distributed Caching**: Redis integration
- **Container Orchestration**: Kubernetes deployment

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

### Code Standards
- Follow Java coding conventions
- Use meaningful variable and method names
- Add comprehensive documentation
- Include unit tests for new features

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**: Ensure PostgreSQL is running and credentials are correct
2. **Port Already in Use**: Change server.port in application.properties
3. **Build Errors**: Run `mvn clean install -DskipTests` to skip tests during initial setup
4. **Memory Issues**: Adjust JVM heap size for large datasets
5. **Authentication Failures**: Verify JWT secret configuration

### Logs and Debugging

Check application logs for detailed error information:
```bash
# View application logs
tail -f logs/bloodbank.log

# Check system resources
htop
df -h
free -h

# Monitor database
sudo -u postgres pg_stat_activity
```

## 📄 License

This project is for educational and demonstration purposes.

## 🆘 Support

### Getting Help
- **Documentation**: Check the docs/ folder first
- **Issues**: Create an issue in the repository
- **Email**: support@bloodbank.com
- **Community**: Join our user forums

### System Requirements
- **Minimum**: 4GB RAM, 20GB storage, Java 17
- **Recommended**: 8GB+ RAM, 50GB+ storage, SSD

## 📊 System Status

- **Current Version**: 1.0.0
- **Last Updated**: January 2024
- **Java Version**: 17+
- **Spring Boot**: 3.2.0
- **Database**: PostgreSQL 12+
- **Security**: JWT + Spring Security
- **Client**: Java Swing Desktop App

---

**Built with ❤️ for blood bank management**

For issues or questions, check the logs and ensure all prerequisites are met. Refer to the comprehensive documentation in the `docs/` folder for detailed information.
