# Blood Bank Management System - Ready to Ship! 🚀

## 🎯 What You Get

A complete, production-ready blood bank management system with:
- **REST API** with JWT authentication
- **Swing Desktop Client** for easy management
- **PostgreSQL Database** with full schema
- **Docker Support** for containerized deployment
- **Comprehensive Documentation** and deployment guides

## 🚀 Quick Start (3 Steps)

### 1. Build the Application
```bash
# Linux/macOS
./build.sh

# Windows
build.bat
```

### 2. Start the Server
```bash
# Linux/macOS
./start-server.sh

# Windows
start-server.bat
```

### 3. Access the System
- **API**: http://localhost:8080/api
- **Swing Client**: Run `./run-swing-client.sh` (Linux/macOS) or `run-swing-client.bat` (Windows)
- **Health Check**: http://localhost:8080/api/health

## 📦 What's Included

### Core Application
- ✅ **Spring Boot REST API** (Port 8080)
- ✅ **JWT Authentication** with role-based access
- ✅ **PostgreSQL Database** with complete schema
- ✅ **Swing Desktop Client** for easy management
- ✅ **Comprehensive API Documentation**

### Features
- 👥 **User Management** (Admin, Staff, Users)
- 🩸 **Blood Inventory Management** (8 blood types)
- 💉 **Donation Tracking** (Pending, Approved, Completed)
- 📋 **Blood Request Management** (Priority-based)
- 🔐 **Secure Authentication** (JWT tokens)
- 📊 **Real-time Inventory Updates**

### Deployment Options
- 🐳 **Docker & Docker Compose** (Ready to deploy)
- 📦 **Standalone JAR** (Java 17+ required)
- 🏗️ **Maven Build** (Full build pipeline)
- 📋 **Production Scripts** (Start/Stop/Health checks)

## 🛠️ System Requirements

### Minimum Requirements
- **Java**: 17 or later
- **Maven**: 3.6 or later
- **PostgreSQL**: 12 or later
- **Memory**: 2GB RAM
- **Disk Space**: 1GB free space

### Optional (for Docker)
- **Docker**: 20.10 or later
- **Docker Compose**: 2.0 or later

## 📁 Project Structure

```
blood_bank/
├── src/main/java/com/bloodbank/          # Java source code
├── src/main/resources/                   # Configuration files
├── database/                             # Database scripts
├── docs/                                # Documentation
├── target/                              # Build artifacts
├── Dockerfile                           # Docker configuration
├── docker-compose.yml                   # Docker Compose setup
├── build.sh / build.bat                 # Build scripts
├── start-server.sh / start-server.bat   # Server startup
├── stop-server.sh / stop-server.bat     # Server shutdown
├── run-swing-client.sh / .bat           # Client launcher
└── DEPLOYMENT_GUIDE.md                  # Complete deployment guide
```

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
export DATABASE_URL="jdbc:postgresql://localhost:5432/bloodbank"
export DATABASE_USERNAME="bloodbank"
export DATABASE_PASSWORD="secure_password"

# JWT Configuration
export JWT_SECRET="your-secret-key"

# Server Configuration
export SERVER_PORT="8080"
export CONTEXT_PATH="/api"
```

### Application Profiles
- **dev**: Development with debug logging
- **prod**: Production with optimized settings

## 🐳 Docker Deployment

### Quick Docker Start
```bash
# Build and start everything
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f bloodbank-app
```

### Manual Docker Build
```bash
# Build image
docker build -t bloodbank-management:1.0.0 .

# Run container
docker run -p 8080:8080 bloodbank-management:1.0.0
```

## 🗄️ Database Setup

### Automatic Setup (Docker)
Database is automatically initialized with Docker Compose.

### Manual Setup
```bash
# Create database
createdb bloodbank

# Initialize schema
psql -d bloodbank -f database/init-prod.sql
```

### Default Credentials
- **Admin User**: `admin` / `admin123`
- **Database**: `bloodbank` / `secure_password`

## 📊 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/change-password` - Change password

### Users
- `GET /api/users` - List users
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Blood Inventory
- `GET /api/inventory` - List inventory
- `POST /api/inventory` - Add blood units
- `PUT /api/inventory/{id}` - Update inventory

### Health Check
- `GET /api/health` - System health status

## 🖥️ Desktop Client

The Swing client provides a user-friendly interface for:
- User management
- Blood inventory tracking
- Donation management
- Blood request handling

### Launch Client
```bash
# Linux/macOS
./run-swing-client.sh

# Windows
run-swing-client.bat
```

## 🔒 Security Features

- **JWT Authentication** with configurable expiration
- **Role-based Access Control** (Admin, Staff, User)
- **Password Hashing** with BCrypt
- **SQL Injection Protection** with JPA
- **CORS Configuration** for web clients
- **Security Headers** in production

## 📈 Monitoring & Health

### Health Checks
- **Application Health**: `GET /api/health`
- **Database Connectivity**: Automatic checks
- **Memory Usage**: JVM monitoring
- **Log Files**: Structured logging with rotation

### Logs
```bash
# View application logs
tail -f logs/bloodbank.log

# Docker logs
docker-compose logs -f bloodbank-app
```

## 🚀 Production Deployment

### 1. Build for Production
```bash
./build.sh
```

### 2. Deploy with Docker
```bash
docker-compose -f docker-compose.yml up -d
```

### 3. Deploy Standalone
```bash
# Configure environment
export DATABASE_URL="jdbc:postgresql://your-db:5432/bloodbank"
export JWT_SECRET="your-production-secret"

# Start application
java -jar target/blood-bank-management-1.0.0.jar --spring.profiles.active=prod
```

## 📚 Documentation

- **API Documentation**: `docs/API_DOCUMENTATION.md`
- **Deployment Guide**: `DEPLOYMENT_GUIDE.md`
- **User Manual**: `docs/USER_MANUAL.md`
- **Architecture**: `docs/ARCHITECTURE_DIAGRAMS_README.md`

## 🆘 Support & Troubleshooting

### Common Issues
1. **Port 8080 in use**: Change `SERVER_PORT` environment variable
2. **Database connection failed**: Check PostgreSQL is running and credentials
3. **Java version error**: Ensure Java 17+ is installed
4. **Memory issues**: Increase JVM heap size with `-Xmx` parameter

### Getting Help
- Check logs: `logs/bloodbank.log`
- Health check: `http://localhost:8080/api/health`
- Database status: `psql -d bloodbank -c "SELECT 1"`

## 🎉 Ready to Ship!

Your blood bank management system is now **production-ready** with:
- ✅ Complete build pipeline
- ✅ Docker containerization
- ✅ Production configuration
- ✅ Comprehensive documentation
- ✅ Health monitoring
- ✅ Security features
- ✅ Easy deployment scripts

**Just run `./build.sh` and you're ready to go!** 🚀

---

*Built with ❤️ using Spring Boot, PostgreSQL, and Java*
