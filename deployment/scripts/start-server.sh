#!/bin/bash

# Blood Bank Management System - Server Startup Script
# For macOS and Linux

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Configuration
JAR_FILE="target/blood-bank-management-1.0.0.jar"
PID_FILE="bloodbank.pid"
LOG_FILE="logs/bloodbank.log"
PROFILE=${1:-prod}

echo "=========================================="
echo "Blood Bank Management System - Server Startup"
echo "=========================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    print_error "Java is not installed or not in PATH"
    print_error "Please install Java 17 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17 or later is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    print_error "JAR file not found: $JAR_FILE"
    print_error "Please run build.sh first"
    exit 1
fi

# Check if server is already running
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        print_warning "Server is already running with PID: $PID"
        print_warning "Use stop-server.sh to stop it first"
        exit 1
    else
        print_warning "Stale PID file found, removing it"
        rm -f "$PID_FILE"
    fi
fi

# Create logs directory
mkdir -p logs

# Set JVM options
JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Set application properties
APP_OPTS="--spring.profiles.active=$PROFILE"

# Start the server
print_status "Starting Blood Bank Management System..."
print_status "Profile: $PROFILE"
print_status "JAR file: $JAR_FILE"
print_status "Log file: $LOG_FILE"

nohup java $JVM_OPTS -jar "$JAR_FILE" $APP_OPTS > "$LOG_FILE" 2>&1 &
SERVER_PID=$!

# Save PID
echo $SERVER_PID > "$PID_FILE"

# Wait a moment and check if server started successfully
sleep 3

if ps -p "$SERVER_PID" > /dev/null 2>&1; then
    print_status "Server started successfully with PID: $SERVER_PID"
    print_status "Logs are being written to: $LOG_FILE"
    print_status "Server should be available at: http://localhost:8080/api"
    print_status "Health check: http://localhost:8080/api/health"
    echo ""
    print_status "To stop the server, run: ./stop-server.sh"
    print_status "To view logs: tail -f $LOG_FILE"
else
    print_error "Failed to start server"
    print_error "Check logs: $LOG_FILE"
    rm -f "$PID_FILE"
    exit 1
fi
