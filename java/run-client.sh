#!/bin/bash

# Client Startup Script for JavaFX Video Streaming System

echo "=========================================="
echo "  JavaFX Video Streaming - Client"
echo "=========================================="

# Configuration
JAVAFX_PATH="${JAVAFX_SDK_PATH:-$HOME/Documents/JavaLibraries/javafx-sdk-26/lib}"
MYSQL_CONNECTOR="mysql-connector-j-9.6.0.jar"

# Check if compiled
if [ ! -f "client/VideoClient.class" ]; then
    echo "❌ Error: Client not compiled. Run ./compile.sh first."
    exit 1
fi

# Check JavaFX
if [ ! -d "$JAVAFX_PATH" ]; then
    echo "❌ Error: JavaFX SDK not found at: $JAVAFX_PATH"
    echo "Set JAVAFX_SDK_PATH environment variable or install at default location."
    exit 1
fi

# Check MySQL connector
if [ ! -f "$MYSQL_CONNECTOR" ]; then
    echo "❌ Error: MySQL connector not found: $MYSQL_CONNECTOR"
    exit 1
fi

echo "🚀 Launching Video Client..."
echo ""

java --module-path "$JAVAFX_PATH" \
     --add-modules javafx.controls,javafx.media \
     -cp ".:$MYSQL_CONNECTOR" \
     client.VideoClient
