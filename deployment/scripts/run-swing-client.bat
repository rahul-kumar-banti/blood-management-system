@echo off
REM Blood Bank Management System - Swing Client Launcher
REM For Windows

echo Starting Blood Bank Management System Swing Client...

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or later
    pause
    exit /b 1
)

REM Check if the application is built
if not exist "target\blood-bank-management-1.0.0.jar" (
    echo Building the application first...
    mvn clean install -DskipTests
    if errorlevel 1 (
        echo Error: Build failed
        pause
        exit /b 1
    )
)

REM Run the Swing client
echo Launching Swing client...
java -cp "target\classes;target\dependency\*" com.bloodbank.client.BloodBankSwingClient

pause
