# Multi-stage build for Blood Bank Management System
FROM maven:3.9.5-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:17-jre-slim

# Install necessary packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -r bloodbank && useradd -r -g bloodbank bloodbank

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/blood-bank-management-1.0.0.jar app.jar

# Copy configuration files
COPY src/main/resources/application-prod.properties ./config/
COPY database/init.sql ./database/

# Create logs directory
RUN mkdir -p logs && chown -R bloodbank:bloodbank /app

# Switch to non-root user
USER bloodbank

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
