# Blood Bank Management System - Deployment Guide

## Overview

This guide provides comprehensive instructions for deploying the Blood Bank Management System in various environments, from development to production.

## Prerequisites

### System Requirements

#### Minimum Requirements
- **CPU**: 2 cores, 2.0 GHz
- **RAM**: 4 GB
- **Storage**: 20 GB available space
- **OS**: Linux (Ubuntu 18.04+), macOS 10.14+, or Windows 10+

#### Recommended Requirements
- **CPU**: 4+ cores, 3.0 GHz
- **RAM**: 8+ GB
- **Storage**: 50+ GB available space (SSD preferred)
- **OS**: Linux (Ubuntu 20.04+), macOS 11+, or Windows 11+

### Software Dependencies

#### Required Software
- **Java**: OpenJDK 17 or Oracle JDK 17
- **Maven**: 3.6+ for building the application
- **PostgreSQL**: 12+ for database
- **Git**: For version control

#### Optional Software
- **pgAdmin**: PostgreSQL administration tool
- **Docker**: For containerized deployment
- **Nginx**: For reverse proxy and load balancing
- **Redis**: For caching (future enhancement)

## Environment Setup

### Development Environment

#### 1. Install Java 17
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# macOS (using Homebrew)
brew install openjdk@17

# Windows
# Download from Oracle or use Chocolatey: choco install openjdk17
```

#### 2. Install Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# Windows
# Download from Apache Maven website or use Chocolatey: choco install maven
```

#### 3. Install PostgreSQL
```bash
# Ubuntu/Debian
sudo apt install postgresql postgresql-contrib

# macOS
brew install postgresql

# Windows
# Download from PostgreSQL website or use Chocolatey: choco install postgresql
```

#### 4. Verify Installation
```bash
java -version
mvn -version
psql --version
```

### Production Environment

#### 1. Server Preparation
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install security updates
sudo apt install unattended-upgrades
sudo dpkg-reconfigure -plow unattended-upgrades

# Configure firewall
sudo ufw enable
sudo ufw allow ssh
sudo ufw allow 8080
sudo ufw allow 5432
```

#### 2. Create Application User
```bash
# Create dedicated user for the application
sudo useradd -m -s /bin/bash bloodbank
sudo usermod -aG sudo bloodbank

# Switch to application user
sudo su - bloodbank
```

## Database Setup

### 1. PostgreSQL Configuration

#### Create Database and User
```sql
-- Connect as postgres user
sudo -u postgres psql

-- Create database
CREATE DATABASE bloodbank;

-- Create application user
CREATE USER bloodbank_user WITH PASSWORD 'secure_password_here';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE bloodbank TO bloodbank_user;

-- Connect to bloodbank database
\c bloodbank

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO bloodbank_user;

-- Exit
\q
```

#### 2. Run Initialization Script
```bash
# Navigate to project directory
cd /path/to/blood_bank

# Run initialization script
psql -U bloodbank_user -d bloodbank -f database/init.sql
```

#### 3. Database Optimization
```sql
-- Enable connection pooling
ALTER SYSTEM SET max_connections = 200;
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';

-- Restart PostgreSQL
sudo systemctl restart postgresql
```

## Application Configuration

### 1. Environment-Specific Properties

#### Development Configuration
```properties
# src/main/resources/application-dev.properties
spring.profiles.active=dev
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.com.bloodbank=DEBUG
```

#### Production Configuration
```properties
# src/main/resources/application-prod.properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.bloodbank=WARN
server.port=8080
```

### 2. Security Configuration

#### JWT Secret Generation
```bash
# Generate secure JWT secret
openssl rand -base64 64

# Update application.properties
jwt.secret=your_generated_secret_here
```

#### Password Encryption
```bash
# Generate BCrypt hash for admin password
java -cp target/classes com.bloodbank.util.PasswordEncoder admin123
```

### 3. Logging Configuration

#### Logback Configuration
```xml
<!-- src/main/resources/logback-spring.xml -->
<configuration>
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="CONSOLE" />
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/bloodbank.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/bloodbank.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>
</configuration>
```

## Building and Deployment

### 1. Application Build

#### Development Build
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package -DskipTests
```

#### Production Build
```bash
# Production build with optimizations
mvn clean package -Pprod -DskipTests

# Verify JAR file
ls -la target/blood-bank-management-*.jar
```

### 2. Deployment Methods

#### Method 1: Direct JAR Execution
```bash
# Create deployment directory
mkdir -p /opt/bloodbank
cd /opt/bloodbank

# Copy JAR file
cp /path/to/blood-bank-management-*.jar bloodbank.jar

# Create startup script
cat > start.sh << 'EOF'
#!/bin/bash
export JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"
export SPRING_PROFILES_ACTIVE=prod
export DB_PASSWORD=your_db_password

java $JAVA_OPTS -jar bloodbank.jar
EOF

chmod +x start.sh

# Run application
./start.sh
```

#### Method 2: Systemd Service
```bash
# Create systemd service file
sudo tee /etc/systemd/system/bloodbank.service << EOF
[Unit]
Description=Blood Bank Management System
After=network.target postgresql.service

[Service]
Type=simple
User=bloodbank
WorkingDirectory=/opt/bloodbank
ExecStart=/usr/bin/java -Xms512m -Xmx2g -XX:+UseG1GC -jar bloodbank.jar
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=DB_PASSWORD=your_db_password
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable bloodbank
sudo systemctl start bloodbank

# Check status
sudo systemctl status bloodbank
```

#### Method 3: Docker Deployment
```dockerfile
# Dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app

COPY target/blood-bank-management-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms512m", "-Xmx2g", "-XX:+UseG1GC", "-jar", "app.jar"]
```

```bash
# Build Docker image
docker build -t bloodbank:latest .

# Run container
docker run -d \
  --name bloodbank \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_PASSWORD=your_db_password \
  bloodbank:latest
```

## Reverse Proxy Configuration

### 1. Nginx Configuration

#### Basic Configuration
```nginx
# /etc/nginx/sites-available/bloodbank
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Health check endpoint
    location /health {
        proxy_pass http://localhost:8080/api/health;
        access_log off;
    }
}
```

#### SSL Configuration (Production)
```nginx
# Install Certbot for Let's Encrypt
sudo apt install certbot python3-certbot-nginx

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com

# Nginx will automatically update configuration with SSL
```

### 2. Load Balancer Configuration

#### Multiple Instances
```nginx
upstream bloodbank_backend {
    server localhost:8080;
    server localhost:8081;
    server localhost:8082;
}

server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://bloodbank_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Monitoring and Maintenance

### 1. Health Checks

#### Application Health
```bash
# Check application status
curl http://localhost:8080/api/health

# Check database connectivity
curl http://localhost:8080/api/health/db

# Check system resources
curl http://localhost:8080/api/health/system
```

#### System Monitoring
```bash
# Monitor system resources
htop
iotop
df -h

# Monitor application logs
tail -f /opt/bloodbank/logs/bloodbank.log

# Monitor PostgreSQL
sudo -u postgres pg_stat_activity
```

### 2. Backup and Recovery

#### Database Backup
```bash
# Create backup directory
mkdir -p /opt/backups

# Daily backup script
cat > /opt/backups/backup_db.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/bloodbank_$DATE.sql"

pg_dump -U bloodbank_user -d bloodbank > "$BACKUP_FILE"
gzip "$BACKUP_FILE"

# Keep only last 7 days of backups
find $BACKUP_DIR -name "bloodbank_*.sql.gz" -mtime +7 -delete
EOF

chmod +x /opt/backups/backup_db.sh

# Add to crontab
echo "0 2 * * * /opt/backups/backup_db.sh" | crontab -
```

#### Application Backup
```bash
# Backup application files
tar -czf /opt/backups/bloodbank_app_$(date +%Y%m%d).tar.gz /opt/bloodbank/
```

### 3. Performance Tuning

#### JVM Optimization
```bash
# Production JVM options
export JAVA_OPTS="-Xms1g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"
```

#### Database Optimization
```sql
-- Analyze table statistics
ANALYZE;

-- Create indexes for frequently queried columns
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_inventory_blood_type ON blood_inventory(blood_type);
CREATE INDEX idx_inventory_expiry ON blood_inventory(expiry_date);
```

## Troubleshooting

### 1. Common Issues

#### Application Won't Start
```bash
# Check Java version
java -version

# Check port availability
netstat -tlnp | grep 8080

# Check logs
tail -f /opt/bloodbank/logs/bloodbank.log
```

#### Database Connection Issues
```bash
# Test database connectivity
psql -h localhost -U bloodbank_user -d bloodbank

# Check PostgreSQL status
sudo systemctl status postgresql

# Check PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-*.log
```

#### Memory Issues
```bash
# Check memory usage
free -h

# Check JVM memory
jstat -gc <pid>

# Adjust heap size in startup script
```

### 2. Performance Issues

#### Slow Response Times
```bash
# Check database performance
EXPLAIN ANALYZE SELECT * FROM users WHERE role = 'DONOR';

# Check application metrics
curl http://localhost:8080/api/actuator/metrics

# Monitor slow queries
sudo tail -f /var/log/postgresql/postgresql-*.log | grep "duration:"
```

## Security Considerations

### 1. Network Security
```bash
# Configure firewall rules
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80
sudo ufw allow 443
sudo ufw enable
```

### 2. Application Security
```properties
# Disable debug endpoints in production
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=never

# Enable HTTPS only
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_keystore_password
```

### 3. Database Security
```sql
-- Restrict database access
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO bloodbank_user;

-- Enable SSL connections
ALTER SYSTEM SET ssl = on;
ALTER SYSTEM SET ssl_ciphers = 'HIGH:MEDIUM:+3DES:!aNULL';
```

## Scaling Considerations

### 1. Horizontal Scaling
```bash
# Multiple application instances
# Instance 1: Port 8080
java -jar bloodbank.jar --server.port=8080

# Instance 2: Port 8081
java -jar bloodbank.jar --server.port=8081

# Instance 3: Port 8082
java -jar bloodbank.jar --server.port=8082
```

### 2. Database Scaling
```sql
-- Read replicas for reporting
-- Master database for writes
-- Replica databases for reads

-- Connection pooling
-- Use PgBouncer or similar for connection management
```

### 3. Caching Strategy
```properties
# Redis configuration for future enhancement
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=your_redis_password
```

## Conclusion

This deployment guide covers the essential steps for deploying the Blood Bank Management System in various environments. Always test deployment procedures in a staging environment before applying to production.

For additional support or questions, refer to the project documentation or create an issue in the project repository.
