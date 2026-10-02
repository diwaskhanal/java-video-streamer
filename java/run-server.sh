#!/bin/bash

# Server Startup Script for JavaFX Video Streaming System

echo "=========================================="
echo "  JavaFX Video Streaming - Server"
echo "=========================================="

MYSQL_CONNECTOR="mysql-connector-j-9.6.0.jar"

# Check if compiled
if [ ! -f "server/VideoServer.class" ]; then
    echo "❌ Error: Server not compiled. Run ./compile.sh first."
    exit 1
fi

# Check MySQL connector
if [ ! -f "$MYSQL_CONNECTOR" ]; then
    echo "❌ Error: MySQL connector not found: $MYSQL_CONNECTOR"
    exit 1
fi

echo "🚀 Starting Video Server on port 5001..."
echo ""

java -cp ".:$MYSQL_CONNECTOR" server.VideoServer
