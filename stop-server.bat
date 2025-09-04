@echo off
REM Blood Bank Management System - Server Shutdown Script for Windows

echo ==========================================
echo Blood Bank Management System - Server Shutdown
echo ==========================================

REM Configuration
set PID_FILE=bloodbank.pid

REM Check if PID file exists
if not exist "%PID_FILE%" (
    echo [WARNING] PID file not found: %PID_FILE%
    echo [WARNING] Server may not be running
    pause
    exit /b 0
)

REM Read PID from file
set /p PID=<%PID_FILE%

REM Check if process is running
tasklist /FI "PID eq %PID%" 2>NUL | find /I "%PID%" >NUL
if errorlevel 1 (
    echo [WARNING] Process with PID %PID% is not running
    echo [WARNING] Removing stale PID file
    del "%PID_FILE%"
    pause
    exit /b 0
)

echo [INFO] Stopping Blood Bank Management System (PID: %PID%)...

REM Try graceful shutdown first
taskkill /PID %PID% /T >NUL 2>&1

REM Wait for graceful shutdown
for /L %%i in (1,1,10) do (
    tasklist /FI "PID eq %PID%" 2>NUL | find /I "%PID%" >NUL
    if errorlevel 1 (
        echo [INFO] Server stopped gracefully
        del "%PID_FILE%"
        pause
        exit /b 0
    )
    echo|set /p="."
    timeout /t 1 /nobreak >NUL
)

echo.

REM Force kill if still running
tasklist /FI "PID eq %PID%" 2>NUL | find /I "%PID%" >NUL
if not errorlevel 1 (
    echo [WARNING] Graceful shutdown failed, forcing shutdown...
    taskkill /PID %PID% /F /T >NUL 2>&1
    
    tasklist /FI "PID eq %PID%" 2>NUL | find /I "%PID%" >NUL
    if not errorlevel 1 (
        echo [ERROR] Failed to stop server
        pause
        exit /b 1
    ) else (
        echo [INFO] Server stopped forcefully
    )
)

REM Clean up PID file
del "%PID_FILE%"

echo [INFO] Server shutdown completed
pause
