@echo off
REM Server Startup Script for JavaFX Video Streaming System (Windows)

echo ==========================================
echo   JavaFX Video Streaming - Server
echo ==========================================

set MYSQL_CONNECTOR=mysql-connector-j-9.6.0.jar

REM Check if compiled
if not exist "server\VideoServer.class" (
    echo Error: Server not compiled. Run compile.bat first.
    exit /b 1
)

REM Check MySQL connector
if not exist "%MYSQL_CONNECTOR%" (
    echo Error: MySQL connector not found: %MYSQL_CONNECTOR%
    exit /b 1
)

echo Starting Video Server on port 5001...
echo.

java -cp ".;%MYSQL_CONNECTOR%" server.VideoServer
