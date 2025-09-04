@echo off
REM Blood Bank Management System - Server Startup Script for Windows

echo ==========================================
echo Blood Bank Management System - Server Startup
echo ==========================================

REM Configuration
set JAR_FILE=target\blood-bank-management-1.0.0.jar
set PID_FILE=bloodbank.pid
set LOG_FILE=logs\bloodbank.log
set PROFILE=%1
if "%PROFILE%"=="" set PROFILE=prod

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH
    echo [ERROR] Please install Java 17 or later
    pause
    exit /b 1
)

REM Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo [ERROR] JAR file not found: %JAR_FILE%
    echo [ERROR] Please run build.bat first
    pause
    exit /b 1
)

REM Check if server is already running
if exist "%PID_FILE%" (
    set /p PID=<%PID_FILE%
    tasklist /FI "PID eq %PID%" 2>NUL | find /I "%PID%" >NUL
    if not errorlevel 1 (
        echo [WARNING] Server is already running with PID: %PID%
        echo [WARNING] Use stop-server.bat to stop it first
        pause
        exit /b 1
    ) else (
        echo [WARNING] Stale PID file found, removing it
        del "%PID_FILE%"
    )
)

REM Create logs directory
if not exist "logs" mkdir logs

REM Set JVM options
set JVM_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseStringDeduplication

REM Set application properties
set APP_OPTS=--spring.profiles.active=%PROFILE%

REM Start the server
echo [INFO] Starting Blood Bank Management System...
echo [INFO] Profile: %PROFILE%
echo [INFO] JAR file: %JAR_FILE%
echo [INFO] Log file: %LOG_FILE%

start /B java %JVM_OPTS% -jar "%JAR_FILE%" %APP_OPTS% > "%LOG_FILE%" 2>&1

REM Get the PID of the Java process
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO CSV ^| find "java.exe"') do (
    set SERVER_PID=%%i
    set SERVER_PID=!SERVER_PID:"=!
    goto :found
)

:found
if defined SERVER_PID (
    echo %SERVER_PID% > "%PID_FILE%"
    echo [INFO] Server started successfully with PID: %SERVER_PID%
    echo [INFO] Logs are being written to: %LOG_FILE%
    echo [INFO] Server should be available at: http://localhost:8080/api
    echo [INFO] Health check: http://localhost:8080/api/health
    echo.
    echo [INFO] To stop the server, run: stop-server.bat
    echo [INFO] To view logs: type %LOG_FILE%
) else (
    echo [ERROR] Failed to start server
    echo [ERROR] Check logs: %LOG_FILE%
    if exist "%PID_FILE%" del "%PID_FILE%"
    pause
    exit /b 1
)

pause
