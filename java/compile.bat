@echo off
REM Compilation Script for JavaFX Video Streaming System (Windows)

echo ==========================================
echo   JavaFX Video Streaming - Compilation
echo ==========================================

REM Configuration - Update these paths for your system
set JAVAFX_PATH=C:\Program Files\Java\javafx-sdk-26\lib
set MYSQL_CONNECTOR=mysql-connector-j-9.6.0.jar

REM Check if JavaFX exists
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

echo JavaFX SDK path: %JAVAFX_PATH%
echo MySQL Connector: %MYSQL_CONNECTOR%
echo.

echo Cleaning old class files...
del /S /Q *.class 2>nul

echo Compiling Java sources...
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media -cp ".;%MYSQL_CONNECTOR%" database\DBConnection.java server\VideoServer.java client\VideoClient.java -d .

if %errorlevel% equ 0 (
    echo.
    echo Compilation successful!
    echo.
    echo Next steps:
    echo   1. Start the server: run-server.bat
    echo   2. Start the client: run-client.bat
) else (
    echo.
    echo Compilation failed!
    exit /b 1
)
