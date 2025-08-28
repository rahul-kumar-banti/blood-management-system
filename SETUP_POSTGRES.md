# Setup Guide for Existing PostgreSQL Installation

Since you already have PostgreSQL and pgAdmin installed, follow these steps to set up the blood bank management system.

## Prerequisites Check

✅ **PostgreSQL**: Already installed  
✅ **pgAdmin**: Already installed  
✅ **Java 17+**: Required for the application  
✅ **Maven**: Required for building the project  

## Step 1: Create the Database

### Option A: Using pgAdmin
1. Open pgAdmin
2. Connect to your PostgreSQL server
3. Right-click on "Databases" → "Create" → "Database"
4. Enter database name: `bloodbank`
5. Click "Save"

### Option B: Using psql Command Line
```bash
psql -U postgres
CREATE DATABASE bloodbank;
\q
```

## Step 2: Update Database Configuration

Edit `src/main/resources/application.properties` and update these values:

```properties
# Update with your actual PostgreSQL credentials
spring.datasource.username=your_actual_username
spring.datasource.password=your_actual_password

# If your PostgreSQL is not on the default port, update the port number
spring.datasource.url=jdbc:postgresql://localhost:5432/bloodbank
```

## Step 3: Initialize the Database

### Option A: Using pgAdmin
1. Open pgAdmin and connect to the `bloodbank` database
2. Open the Query Tool (SQL icon)
3. Copy and paste the contents of `database/init.sql`
4. Execute the script (F5 or click the Execute button)

### Option B: Using psql Command Line
```bash
psql -U your_username -d bloodbank -f database/init.sql
```

## Step 4: Build and Run the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Step 5: Test the System

### Test with Default Users
Use these credentials to test the system:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `admin123` | ADMIN | Full system access |
| `doctor1` | `doctor123` | DOCTOR | Medical staff access |
| `nurse1` | `nurse123` | NURSE | Nursing staff access |
| `tech1` | `tech123` | TECHNICIAN | Laboratory staff access |
| `donor1` | `donor123` | DONOR | Blood donor access |

### Test API Endpoints

#### 1. Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "role": "DONOR",
    "bloodType": "O_POSITIVE"
  }'
```

#### 2. Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

#### 3. Get blood inventory:
```bash
curl -X GET http://localhost:8080/api/inventory \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Step 6: Run the Swing Desktop Client

```bash
# After building the project
java -cp target/classes com.bloodbank.client.BloodBankSwingClient
```

## Troubleshooting

### Common Issues

#### 1. Connection Refused
- Ensure PostgreSQL service is running
- Check if PostgreSQL is listening on the correct port (default: 5432)
- Verify firewall settings

#### 2. Authentication Failed
- Double-check username and password in `application.properties`
- Ensure the user has access to the `bloodbank` database
- Check if the user has necessary permissions

#### 3. Database Not Found
- Verify the database name is correct
- Ensure the database was created successfully
- Check if you're connected to the right PostgreSQL instance

#### 4. Port Already in Use
- The application uses port 8080 by default
- If port 8080 is busy, change it in `application.properties`:
  ```properties
  server.port=8081
  ```

### PostgreSQL Service Management

#### Windows
```cmd
# Check service status
sc query postgresql-x64-15

# Start service
net start postgresql-x64-15

# Stop service
net stop postgresql-x64-15
```

#### macOS/Linux
```bash
# Check service status
sudo systemctl status postgresql

# Start service
sudo systemctl start postgresql

# Stop service
sudo systemctl stop postgresql
```

### pgAdmin Connection Issues
- Ensure pgAdmin is connecting to the correct PostgreSQL instance
- Check if the connection uses the right port and credentials
- Verify the PostgreSQL service is running

## Verification

After setup, you should be able to:

1. ✅ Access the application at `http://localhost:8080`
2. ✅ Connect to the `bloodbank` database in pgAdmin
3. ✅ See sample data in the database tables
4. ✅ Login with default users
5. ✅ Use the Swing desktop client
6. ✅ Make API calls with authentication

## Next Steps

Once the basic system is running:

1. **Customize the configuration** for your environment
2. **Add more users** through the registration API
3. **Create a web frontend** using the REST API
4. **Implement additional features** like reporting and analytics
5. **Set up monitoring** and logging for production use

## Support

If you encounter issues:
1. Check the application logs for error messages
2. Verify database connectivity in pgAdmin
3. Ensure all prerequisites are met
4. Check the troubleshooting section above
