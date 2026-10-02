#!/bin/bash

# Compilation Script for JavaFX Video Streaming System
# This script compiles all Java source files

echo "=========================================="
echo "  JavaFX Video Streaming - Compilation"
echo "=========================================="

# Configuration
JAVAFX_PATH="${JAVAFX_SDK_PATH:-$HOME/Documents/JavaLibraries/javafx-sdk-26/lib}"
MYSQL_CONNECTOR="mysql-connector-j-9.6.0.jar"

# Check if JavaFX path exists
if [ ! -d "$JAVAFX_PATH" ]; then
    echo "❌ Error: JavaFX SDK not found at: $JAVAFX_PATH"
    echo ""
    echo "Please either:"
    echo "  1. Set JAVAFX_SDK_PATH environment variable:"
    echo "     export JAVAFX_SDK_PATH=/path/to/javafx-sdk-26/lib"
    echo "  2. Install JavaFX SDK at: $HOME/Documents/JavaLibraries/javafx-sdk-26"
    echo ""
    exit 1
fi

# Check if MySQL connector exists
if [ ! -f "$MYSQL_CONNECTOR" ]; then
    echo "❌ Error: MySQL connector not found: $MYSQL_CONNECTOR"
    exit 1
fi

echo "✓ JavaFX SDK path: $JAVAFX_PATH"
echo "✓ MySQL Connector: $MYSQL_CONNECTOR"
echo ""

# Clean old class files
echo "🧹 Cleaning old class files..."
find . -name "*.class" -type f -delete

# Compile
echo "🔨 Compiling Java sources..."
javac --module-path "$JAVAFX_PATH" \
      --add-modules javafx.controls,javafx.media \
      -cp ".:$MYSQL_CONNECTOR" \
      database/DBConnection.java \
      server/VideoServer.java \
      client/VideoClient.java \
      -d .

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilation successful!"
    echo ""
    echo "Next steps:"
    echo "  1. Start the server: ./run-server.sh"
    echo "  2. Start the client: ./run-client.sh"
else
    echo ""
    echo "❌ Compilation failed!"
    exit 1
fi
