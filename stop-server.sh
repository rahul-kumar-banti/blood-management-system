#!/bin/bash

# Blood Bank Management System - Server Shutdown Script
# For macOS and Linux

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
PID_FILE="bloodbank.pid"

echo "=========================================="
echo "Blood Bank Management System - Server Shutdown"
echo "=========================================="

# Check if PID file exists
if [ ! -f "$PID_FILE" ]; then
    print_warning "PID file not found: $PID_FILE"
    print_warning "Server may not be running"
    exit 0
fi

# Read PID from file
PID=$(cat "$PID_FILE")

# Check if process is running
if ! ps -p "$PID" > /dev/null 2>&1; then
    print_warning "Process with PID $PID is not running"
    print_warning "Removing stale PID file"
    rm -f "$PID_FILE"
    exit 0
fi

print_status "Stopping Blood Bank Management System (PID: $PID)..."

# Try graceful shutdown first
kill -TERM "$PID" 2>/dev/null

# Wait for graceful shutdown
for i in {1..10}; do
    if ! ps -p "$PID" > /dev/null 2>&1; then
        print_status "Server stopped gracefully"
        rm -f "$PID_FILE"
        exit 0
    fi
    echo -n "."
    sleep 1
done

echo ""

# Force kill if still running
if ps -p "$PID" > /dev/null 2>&1; then
    print_warning "Graceful shutdown failed, forcing shutdown..."
    kill -KILL "$PID" 2>/dev/null
    
    if ps -p "$PID" > /dev/null 2>&1; then
        print_error "Failed to stop server"
        exit 1
    else
        print_status "Server stopped forcefully"
    fi
fi

# Clean up PID file
rm -f "$PID_FILE"

print_status "Server shutdown completed"
