# Blood Bank Management System - Deployment Guide

## Overview
This guide provides comprehensive instructions for deploying the Blood Bank Management System in various environments.

## Prerequisites

### System Requirements
- **Java**: 17 or later
- **Maven**: 3.6 or later
- **PostgreSQL**: 12 or later
- **Memory**: Minimum 2GB RAM
- **Disk Space**: Minimum 1GB free space

### Optional
- **Docker**: 20.10 or later (for containerized deployment)
- **Docker Compose**: 2.0 or later

## Quick Start

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

### 3. Access the Application
- **API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/health
- **Swing Client**: Run `./run-swing-client.sh` (Linux/macOS) or `run-swing-client.bat` (Windows)

## Deployment Options

### Option 1: Standalone JAR Deployment

#### 1. Build the Application
```bash
./build.sh
```

#### 2. Configure Database
```bash
# Create PostgreSQL database
createdb bloodbank

# Run initialization script
psql -d bloodbank -f database/init-prod.sql
```

#### 3. Configure Environment Variables
```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/bloodbank"
export DATABASE_USERNAME="bloodbank"
export DATABASE_PASSWORD="secure_password"
export JWT_SECRET="your-secret-key"
```

#### 4. Start the Application
```bash
java -jar target/blood-bank-management-1.0.0.jar --spring.profiles.active=prod
```

### Option 2: Docker Deployment

#### 1. Build Docker Image
```bash
docker build -t bloodbank-management:1.0.0 .
```

#### 2. Run with Docker Compose
```bash
docker-compose up -d
```

#### 3. Access the Application
- **API**: http://localhost:8080/api
- **Database**: localhost:5432

### Option 3: Production Deployment

#### 1. Create Production Environment
```bash
# Create production directory
mkdir -p /opt/bloodbank
cd /opt/bloodbank

# Extract distribution
unzip blood-bank-management-1.0.0-distribution.zip
```

#### 2. Configure Production Database
```bash
# Create production database
sudo -u postgres createdb bloodbank_prod

# Run production initialization
sudo -u postgres psql -d bloodbank_prod -f database/init-prod.sql
```

#### 3. Configure Production Settings
```bash
# Edit production configuration
nano config/application-prod.properties

# Set environment variables
export DATABASE_URL="jdbc:postgresql://localhost:5432/bloodbank_prod"
export DATABASE_USERNAME="bloodbank"
export DATABASE_PASSWORD="your-secure-password"
export JWT_SECRET="your-production-secret"
```

#### 4. Create System Service (Linux)
```bash
# Create systemd service file
sudo nano /etc/systemd/system/bloodbank.service
```

Add the following content:
```ini
[Unit]
Description=Blood Bank Management System
After=network.target postgresql.service

[Service]
Type=simple
User=bloodbank
WorkingDirectory=/opt/bloodbank
ExecStart=/usr/bin/java -jar blood-bank-management-1.0.0.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 5. Start the Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable bloodbank
sudo systemctl start bloodbank
sudo systemctl status bloodbank
```

## Configuration

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/bloodbank` |
| `DATABASE_USERNAME` | Database username | `bloodbank` |
| `DATABASE_PASSWORD` | Database password | `secure_password` |
| `JWT_SECRET` | JWT signing secret | Generated secret |
| `SERVER_PORT` | Server port | `8080` |
| `CONTEXT_PATH` | API context path | `/api` |
| `LOG_FILE_PATH` | Log file path | `./logs/bloodbank.log` |

### Application Profiles
- **dev**: Development configuration with debug logging
- **prod**: Production configuration with optimized settings

## Monitoring and Maintenance

### Health Checks
- **Health Endpoint**: `GET /api/health`
- **Metrics Endpoint**: `GET /api/actuator/metrics`

### Log Management
```bash
# View logs
tail -f logs/bloodbank.log

# Rotate logs (using logrotate)
sudo nano /etc/logrotate.d/bloodbank
```

### Database Maintenance
```bash
# Backup database
pg_dump bloodbank > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore database
psql bloodbank < backup_file.sql
```

## Security Considerations

### 1. Database Security
- Use strong passwords
- Enable SSL connections
- Restrict database access by IP
- Regular security updates

### 2. Application Security
- Use strong JWT secrets
- Enable HTTPS in production
- Regular security audits
- Keep dependencies updated

### 3. Network Security
- Use firewalls
- Implement rate limiting
- Monitor access logs
- Use VPN for remote access

## Troubleshooting

### Common Issues

#### 1. Database Connection Failed
```bash
# Check database status
sudo systemctl status postgresql

# Check connection
psql -h localhost -U bloodbank -d bloodbank
```

#### 2. Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

#### 3. Memory Issues
```bash
# Check memory usage
free -h

# Increase JVM heap size
java -Xmx2048m -jar blood-bank-management-1.0.0.jar
```

### Log Analysis
```bash
# Search for errors
grep -i error logs/bloodbank.log

# Search for specific patterns
grep -i "database" logs/bloodbank.log
```

## Performance Optimization

### 1. JVM Tuning
```bash
# Production JVM options
java -Xms1g -Xmx2g -XX:+UseG1GC -XX:+UseStringDeduplication \
     -jar blood-bank-management-1.0.0.jar
```

### 2. Database Optimization
- Create appropriate indexes
- Regular VACUUM and ANALYZE
- Monitor query performance
- Use connection pooling

### 3. Application Optimization
- Enable compression
- Use caching where appropriate
- Monitor response times
- Optimize database queries

## Backup and Recovery

### 1. Database Backup
```bash
# Daily backup script
#!/bin/bash
BACKUP_DIR="/opt/backups/bloodbank"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump bloodbank > $BACKUP_DIR/bloodbank_$DATE.sql
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
```

### 2. Application Backup
```bash
# Backup application files
tar -czf bloodbank_app_$(date +%Y%m%d).tar.gz /opt/bloodbank
```

## Support and Maintenance

### Regular Tasks
- Monitor system resources
- Check application logs
- Update dependencies
- Backup database
- Security updates

### Contact Information
- **Technical Support**: [Your Support Email]
- **Documentation**: [Your Documentation URL]
- **Issue Tracking**: [Your Issue Tracker URL]

## License
[Your License Information]
