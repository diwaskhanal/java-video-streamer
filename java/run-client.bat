@echo off
REM Client Startup Script for JavaFX Video Streaming System (Windows)

echo ==========================================
echo   JavaFX Video Streaming - Client
echo ==========================================

REM Configuration - Update these paths for your system
set JAVAFX_PATH=C:\Program Files\Java\javafx-sdk-26\lib
set MYSQL_CONNECTOR=mysql-connector-j-9.6.0.jar

REM Check if compiled
if not exist "client\VideoClient.class" (
    echo Error: Client not compiled. Run compile.bat first.
    exit /b 1
)

REM Check JavaFX
if not exist "%JAVAFX_PATH%" (
    echo Error: JavaFX SDK not found at: %JAVAFX_PATH%
    echo Please update JAVAFX_PATH in this script
    exit /b 1
)

REM Check MySQL connector
if not exist "%MYSQL_CONNECTOR%" (
    echo Error: MySQL connector not found: %MYSQL_CONNECTOR%
    exit /b 1
)

echo Launching Video Client...
echo.

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media -cp ".;%MYSQL_CONNECTOR%" client.VideoClient
