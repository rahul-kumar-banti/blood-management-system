#!/bin/bash

# Blood Bank Management System - Swing Client Launcher
# For macOS and Linux

echo "Starting Blood Bank Management System Swing Client..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "Error: Java 17 or later is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Check if the application is built
if [ ! -f "target/blood-bank-management-1.0.0.jar" ]; then
    echo "Building the application first..."
    if ! command -v mvn &> /dev/null; then
        echo "Error: Maven is not installed or not in PATH"
        echo "Please install Maven first"
        exit 1
    fi
    mvn clean install -DskipTests
fi

# Run the Swing client
echo "Launching Swing client..."
java -cp "target/classes:target/dependency/*" com.bloodbank.client.BloodBankSwingClient
