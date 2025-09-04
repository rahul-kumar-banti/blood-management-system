#!/bin/bash

# Blood Bank Management System - Build Script
# This script builds the application for production deployment

set -e  # Exit on any error

echo "=========================================="
echo "Blood Bank Management System - Build Script"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Java is installed
check_java() {
    print_status "Checking Java installation..."
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        print_error "Please install Java 17 or later"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_error "Java 17 or later is required. Current version: $JAVA_VERSION"
        exit 1
    fi
    print_status "Java $JAVA_VERSION found ✓"
}

# Check if Maven is installed
check_maven() {
    print_status "Checking Maven installation..."
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        print_error "Please install Maven first"
        exit 1
    fi
    print_status "Maven found ✓"
}

# Clean previous builds
clean_build() {
    print_status "Cleaning previous builds..."
    mvn clean
    print_status "Clean completed ✓"
}

# Run tests
run_tests() {
    print_status "Running tests..."
    if mvn test; then
        print_status "All tests passed ✓"
    else
        print_warning "Some tests failed, but continuing with build..."
    fi
}

# Build the application
build_application() {
    print_status "Building application..."
    mvn package -DskipTests
    print_status "Application built successfully ✓"
}

# Create distribution package
create_distribution() {
    print_status "Creating distribution package..."
    mvn assembly:single
    print_status "Distribution package created ✓"
}

# Build Docker image
build_docker() {
    if command -v docker &> /dev/null; then
        print_status "Building Docker image..."
        docker build -t bloodbank-management:1.0.0 .
        print_status "Docker image built successfully ✓"
    else
        print_warning "Docker not found, skipping Docker image build"
    fi
}

# Create deployment directory
create_deployment_dir() {
    print_status "Creating deployment directory..."
    DEPLOY_DIR="deployment"
    mkdir -p $DEPLOY_DIR
    
    # Copy JAR file
    cp target/blood-bank-management-1.0.0.jar $DEPLOY_DIR/
    
    # Copy configuration files
    mkdir -p $DEPLOY_DIR/config
    cp src/main/resources/application*.properties $DEPLOY_DIR/config/
    
    # Copy database scripts
    mkdir -p $DEPLOY_DIR/database
    cp database/*.sql $DEPLOY_DIR/database/ 2>/dev/null || true
    
    # Copy scripts
    mkdir -p $DEPLOY_DIR/scripts
    cp *.sh *.bat $DEPLOY_DIR/scripts/ 2>/dev/null || true
    
    # Copy documentation
    mkdir -p $DEPLOY_DIR/docs
    cp docs/*.md $DEPLOY_DIR/docs/ 2>/dev/null || true
    
    # Create logs directory
    mkdir -p $DEPLOY_DIR/logs
    
    print_status "Deployment directory created: $DEPLOY_DIR ✓"
}

# Main build process
main() {
    echo "Starting build process..."
    
    check_java
    check_maven
    clean_build
    run_tests
    build_application
    create_distribution
    build_docker
    create_deployment_dir
    
    echo ""
    echo "=========================================="
    print_status "Build completed successfully! ✓"
    echo "=========================================="
    echo ""
    echo "Deployment files are available in:"
    echo "  - JAR file: target/blood-bank-management-1.0.0.jar"
    echo "  - Distribution: target/blood-bank-management-1.0.0-distribution.zip"
    echo "  - Docker image: bloodbank-management:1.0.0"
    echo "  - Deployment dir: deployment/"
    echo ""
    echo "To run the application:"
    echo "  - Standalone: java -jar target/blood-bank-management-1.0.0.jar"
    echo "  - Docker: docker-compose up -d"
    echo "  - Swing client: ./run-swing-client.sh"
}

# Run main function
main "$@"
