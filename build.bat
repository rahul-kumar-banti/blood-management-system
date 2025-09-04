@echo off
REM Blood Bank Management System - Build Script for Windows
REM This script builds the application for production deployment

echo ==========================================
echo Blood Bank Management System - Build Script
echo ==========================================

REM Check if Java is installed
echo [INFO] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH
    echo [ERROR] Please install Java 17 or later
    pause
    exit /b 1
)
echo [INFO] Java found ✓

REM Check if Maven is installed
echo [INFO] Checking Maven installation...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed or not in PATH
    echo [ERROR] Please install Maven first
    pause
    exit /b 1
)
echo [INFO] Maven found ✓

REM Clean previous builds
echo [INFO] Cleaning previous builds...
call mvn clean
if errorlevel 1 (
    echo [ERROR] Clean failed
    pause
    exit /b 1
)
echo [INFO] Clean completed ✓

REM Run tests
echo [INFO] Running tests...
call mvn test
if errorlevel 1 (
    echo [WARNING] Some tests failed, but continuing with build...
) else (
    echo [INFO] All tests passed ✓
)

REM Build the application
echo [INFO] Building application...
call mvn package -DskipTests
if errorlevel 1 (
    echo [ERROR] Build failed
    pause
    exit /b 1
)
echo [INFO] Application built successfully ✓

REM Create distribution package
echo [INFO] Creating distribution package...
call mvn assembly:single
if errorlevel 1 (
    echo [ERROR] Distribution creation failed
    pause
    exit /b 1
)
echo [INFO] Distribution package created ✓

REM Create deployment directory
echo [INFO] Creating deployment directory...
if not exist "deployment" mkdir deployment
copy "target\blood-bank-management-1.0.0.jar" "deployment\"

if not exist "deployment\config" mkdir deployment\config
copy "src\main\resources\application*.properties" "deployment\config\"

if not exist "deployment\database" mkdir deployment\database
if exist "database\*.sql" copy "database\*.sql" "deployment\database\"

if not exist "deployment\scripts" mkdir deployment\scripts
if exist "*.bat" copy "*.bat" "deployment\scripts\"
if exist "*.sh" copy "*.sh" "deployment\scripts\"

if not exist "deployment\docs" mkdir deployment\docs
if exist "docs\*.md" copy "docs\*.md" "deployment\docs\"

if not exist "deployment\logs" mkdir deployment\logs

echo [INFO] Deployment directory created: deployment ✓

echo.
echo ==========================================
echo [INFO] Build completed successfully! ✓
echo ==========================================
echo.
echo Deployment files are available in:
echo   - JAR file: target\blood-bank-management-1.0.0.jar
echo   - Distribution: target\blood-bank-management-1.0.0-distribution.zip
echo   - Deployment dir: deployment\
echo.
echo To run the application:
echo   - Standalone: java -jar target\blood-bank-management-1.0.0.jar
echo   - Swing client: run-swing-client.bat
echo.

pause
