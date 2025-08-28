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

### Blood Components
- **Whole Blood**: Complete blood including red cells, white cells, platelets, and plasma
- **Red Blood Cells (RBCs)**: Oxygen-carrying cells, most commonly transfused
- **Plasma**: Liquid portion of blood containing proteins and clotting factors
- **Platelets**: Cell fragments essential for blood clotting
- **Cryoprecipitate**: Plasma derivative rich in clotting factors

### Donor
An individual who donates blood to the blood bank. Donors must pass health screening and meet eligibility criteria.

**Donor Eligibility Criteria:**
- Age: 18-65 years (varies by region)
- Weight: Minimum 50kg (110 lbs)
- Hemoglobin level: ≥12.5 g/dL for females, ≥13.0 g/dL for males
- Blood pressure: Systolic 90-180 mmHg, Diastolic 50-100 mmHg
- Pulse rate: 50-100 beats per minute
- Temperature: <37.5°C (99.5°F)

### Recipient
An individual who receives blood from the blood bank, typically through a hospital or medical facility.

### Inventory
Stock of blood units available in the blood bank, including quantity, blood type, expiry date, and status.

**Inventory Status Types:**
- **AVAILABLE**: Ready for use
- **RESERVED**: Allocated to a specific request
- **IN_USE**: Currently being transfused
- **EXPIRED**: Past expiry date, must be discarded
- **QUARANTINED**: Under investigation or testing

### Blood Unit
Standard measurement of blood collection:
- **Standard Unit**: 450ml ± 45ml (whole blood)
- **Double Unit**: 900ml ± 90ml (apheresis)
- **Pediatric Unit**: 100-250ml (for children)

### Expiry Management
- **Whole Blood**: 21-35 days (depending on anticoagulant)
- **Red Blood Cells**: 35-42 days
- **Platelets**: 5-7 days
- **Plasma**: 1 year (frozen)
- **Cryoprecipitate**: 1 year (frozen)

## Medical Terminology

### Hemoglobin (Hb)
Protein in red blood cells that carries oxygen. Normal levels:
- **Males**: 13.5-17.5 g/dL
- **Females**: 12.0-15.5 g/dL

### Blood Pressure
Force of blood against artery walls:
- **Normal**: <120/80 mmHg
- **Prehypertension**: 120-139/80-89 mmHg
- **Hypertension**: ≥140/90 mmHg

### Vital Signs
- **Pulse Rate**: 60-100 beats per minute (adults)
- **Temperature**: 36.5-37.5°C (97.7-99.5°F)
- **Respiratory Rate**: 12-20 breaths per minute

### Blood Transfusion
Process of transferring blood or blood components from donor to recipient.

**Transfusion Reactions:**
- **Acute Hemolytic**: Immediate immune response
- **Febrile Non-Hemolytic**: Fever without hemolysis
- **Allergic**: Mild to severe allergic response
- **Transfusion-Related Acute Lung Injury (TRALI)**: Respiratory distress

## System Architecture Concepts

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

**REST Principles:**
- **Stateless**: Each request contains all necessary information
- **Uniform Interface**: Consistent resource identification and manipulation
- **Cacheable**: Responses can be cached when appropriate
- **Client-Server**: Separation of concerns between client and server

### Security Model
- **Role-Based Access Control (RBAC)**: Users have specific roles with defined permissions
- **JWT Token Authentication**: Stateless authentication using JSON Web Tokens
- **Secure Password Storage**: BCrypt hashing with salt
- **Endpoint-Level Security**: Spring Security annotations for method-level protection

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

**Swing Components Used:**
- **JFrame**: Main application window
- **JTabbedPane**: Tabbed interface for different sections
- **JTable**: Data display and editing
- **JPanel**: Container for organizing components
- **JButton**: User interaction elements

### Spring Boot
A framework that simplifies the development of Spring applications by providing auto-configuration and starter dependencies.

**Spring Boot Features:**
- **Auto-configuration**: Automatic bean configuration based on classpath
- **Starter Dependencies**: Simplified dependency management
- **Embedded Servers**: Built-in Tomcat server
- **Actuator**: Production-ready monitoring and metrics

### Spring Data JPA
Provides a repository abstraction over JPA, simplifying data access and reducing boilerplate code.

**Key Features:**
- **Repository Pattern**: Standardized data access interface
- **Query Methods**: Automatic query generation from method names
- **Pagination**: Built-in support for large dataset handling
- **Sorting**: Dynamic sorting capabilities

### Spring Security
Provides authentication and authorization features, including JWT token-based security.

**Security Features:**
- **Authentication**: User identity verification
- **Authorization**: Access control based on roles
- **Session Management**: Secure session handling
- **CSRF Protection**: Cross-site request forgery prevention

### JWT (JSON Web Tokens)
A compact, URL-safe means of representing claims to be transferred between two parties, used for stateless authentication.

**JWT Structure:**
- **Header**: Algorithm and token type
- **Payload**: Claims and user data
- **Signature**: Verification signature

**JWT Claims:**
- **Subject (sub)**: User identifier
- **Issued At (iat)**: Token creation time
- **Expiration (exp)**: Token expiry time
- **Custom Claims**: Role, permissions, etc.

### Hibernate
An object-relational mapping (ORM) framework that implements the JPA specification, handling the conversion between Java objects and database records.

**Hibernate Features:**
- **Object-Relational Mapping**: Java objects to database tables
- **Lazy Loading**: On-demand data loading
- **Caching**: First and second-level caching
- **Transaction Management**: ACID compliance

### Maven
A build automation tool used for managing project dependencies and building the application.

**Maven Features:**
- **Dependency Management**: Centralized dependency resolution
- **Build Lifecycle**: Standardized build process
- **Plugin System**: Extensible build functionality
- **Project Structure**: Convention over configuration

## Database Design Concepts

### Core Tables
- **users**: Stores user information and credentials
- **blood_inventory**: Tracks available blood units
- **donations**: Records blood donation details
- **requests**: Manages blood requests from hospitals

### Database Relationships
- **One-to-Many**: Users can have multiple donations
- **One-to-Many**: Users can have multiple requests
- **One-to-One**: Blood inventory is linked to donations
- **Many-to-Many**: Users can be associated with multiple roles

### Audit Fields
All entities include audit fields for tracking:
- **created_at**: Record creation timestamp
- **updated_at**: Last modification timestamp
- **created_by**: User who created the record
- **updated_by**: User who last modified the record

### Data Integrity
- **Foreign Key Constraints**: Maintain referential integrity
- **Check Constraints**: Validate data ranges and formats
- **Unique Constraints**: Prevent duplicate data
- **Not Null Constraints**: Ensure required data presence

## Client-Server Communication

### HTTP Client
The Swing client uses Java's built-in HTTP client to communicate with the REST API:
- **Asynchronous HTTP Requests**: Non-blocking API calls
- **JSON Request/Response Format**: Standard data exchange format
- **Bearer Token Authentication**: Secure API access
- **Error Handling**: User-friendly error messages and status updates

### Data Synchronization
- **Real-time Updates**: Immediate data refresh through API calls
- **Local Caching**: Improved performance with local data storage
- **Conflict Resolution**: Handling concurrent data modifications
- **Offline Support**: Basic functionality when network is unavailable

### API Communication Patterns
- **Request-Response**: Standard HTTP request/response cycle
- **Polling**: Periodic data updates for real-time information
- **WebSocket**: Future enhancement for real-time communication
- **Event-Driven**: Notification-based updates

## System Monitoring and Health

### Health Checks
- **Database Connectivity**: Verify database availability
- **Memory Usage**: Monitor system memory consumption
- **Disk Space**: Check available storage
- **Response Time**: Measure API performance

### Logging and Monitoring
- **Structured Logging**: Consistent log format for analysis
- **Log Levels**: DEBUG, INFO, WARN, ERROR, FATAL
- **Performance Metrics**: Response times and throughput
- **Error Tracking**: Detailed error information for debugging

### Security Monitoring
- **Authentication Failures**: Track failed login attempts
- **Authorization Violations**: Monitor access control violations
- **API Usage Patterns**: Analyze normal vs. suspicious activity
- **Token Expiry**: Monitor JWT token lifecycle

## Future Enhancements

### Planned Features
- **Mobile Application**: iOS and Android clients
- **Web Dashboard**: Browser-based management interface
- **Reporting System**: Advanced analytics and reporting
- **Integration APIs**: Hospital information system integration
- **Machine Learning**: Predictive analytics for inventory management

### Scalability Considerations
- **Microservices Architecture**: Service decomposition for scalability
- **Load Balancing**: Distribute traffic across multiple instances
- **Database Sharding**: Horizontal database scaling
- **Caching Strategy**: Redis or similar for performance optimization
- **Message Queues**: Asynchronous processing for high load
