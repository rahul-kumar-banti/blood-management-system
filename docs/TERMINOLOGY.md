# Blood Bank Management System - Terminology

## Blood Bank Concepts

### Blood Group
Classification of blood based on antigens present on red blood cells. The system supports the standard ABO blood group system:
- **A_POSITIVE**: Blood type A with Rh factor positive
- **A_NEGATIVE**: Blood type A with Rh factor negative
- **B_POSITIVE**: Blood type B with Rh factor positive
- **B_NEGATIVE**: Blood type B with Rh factor negative
- **AB_POSITIVE**: Blood type AB with Rh factor positive
- **AB_NEGATIVE**: Blood type AB with Rh factor negative
- **O_POSITIVE**: Blood type O with Rh factor positive
- **O_NEGATIVE**: Blood type O with Rh factor negative (universal donor)

### Donor
An individual who donates blood to the blood bank. Donors must pass health screening and meet eligibility criteria.

### Recipient
An individual who receives blood from the blood bank, typically through a hospital or medical facility.

### Inventory
Stock of blood units available in the blood bank, including quantity, blood type, expiry date, and status.

## Java Technologies Used

### JDBC (Java Database Connectivity)
Java Database Connectivity is an API that provides methods to query and update data in a database. In this system, JDBC is used implicitly through Spring Data JPA, which provides a higher-level abstraction over JDBC.

### Swing
Java's GUI toolkit for building desktop applications. The system includes a comprehensive Swing-based desktop client that provides:
- User authentication interface
- Blood inventory management
- User management
- Donation tracking
- Request handling

### Spring Boot
A framework that simplifies the development of Spring applications by providing auto-configuration and starter dependencies.

### Spring Data JPA
Provides a repository abstraction over JPA, simplifying data access and reducing boilerplate code.

### Spring Security
Provides authentication and authorization features, including JWT token-based security.

### JWT (JSON Web Tokens)
A compact, URL-safe means of representing claims to be transferred between two parties, used for stateless authentication.

### Hibernate
An object-relational mapping (ORM) framework that implements the JPA specification, handling the conversion between Java objects and database records.

### Maven
A build automation tool used for managing project dependencies and building the application.

## System Architecture

### Layered Architecture
The system follows a standard layered architecture:
1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic
3. **Repository Layer**: Manages data access
4. **Entity Layer**: Represents database tables as Java objects

### RESTful API
The system exposes RESTful web services for:
- User authentication and management
- Blood inventory operations
- Donation tracking
- Request management

### Security Model
- Role-based access control (RBAC)
- JWT token authentication
- Secure password storage using BCrypt
- Endpoint-level security using Spring Security annotations

## Database Design

### Core Tables
- **users**: Stores user information and credentials
- **blood_inventory**: Tracks available blood units
- **donations**: Records blood donation details
- **requests**: Manages blood requests from hospitals

### Relationships
- Users can have multiple donations
- Blood inventory is linked to donations
- Requests are linked to requesting users
- All entities include audit fields (created_at, updated_at)

## Client-Server Communication

### HTTP Client
The Swing client uses Java's built-in HTTP client to communicate with the REST API:
- Asynchronous HTTP requests
- JSON request/response format
- Bearer token authentication
- Error handling and user feedback

### Data Synchronization
- Real-time data updates through API calls
- Local caching for improved performance
- User-friendly error messages and status updates
