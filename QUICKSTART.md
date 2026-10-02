# Quick Start Guide

## For First-Time Setup

### 1. Prerequisites Checklist
- [ ] Java JDK 17+ installed
- [ ] JavaFX SDK 26 downloaded and extracted
- [ ] MySQL Server running
- [ ] MySQL Connector JAR in `java/` directory

### 2. Database Setup (5 minutes)
```bash
# Login to MySQL
mysql -u root -p

# Run the schema file
source docs/database_schema.sql

# Update video file paths in the database
UPDATE videos SET file_path = '/absolute/path/to/your/video.mp4' WHERE id = 1;
```

### 3. Configure JavaFX Path

**Linux/Mac:**
```bash
export JAVAFX_SDK_PATH="$HOME/Documents/JavaLibraries/javafx-sdk-26/lib"
```

**Windows:** Edit the `.bat` files and update the `JAVAFX_PATH` variable.

### 4. Compile and Run

```bash
cd java

# Compile
./compile.sh          # Linux/Mac
compile.bat           # Windows

# Terminal 1 - Start server
./run-server.sh       # Linux/Mac
run-server.bat        # Windows

# Terminal 2 - Start client
./run-client.sh       # Linux/Mac
run-client.bat        # Windows
```

## Common Issues

**"Connection refused"**
→ Start the server before the client

**"ClassNotFoundException: com.mysql.cj.jdbc.Driver"**
→ Ensure `mysql-connector-j-9.6.0.jar` is in the `java/` directory

**"JavaFX SDK not found"**
→ Set `JAVAFX_SDK_PATH` environment variable or update script paths

**Videos not playing**
→ Check file paths in database match actual video locations

## Project Structure at a Glance

```
├── java/
│   ├── client/          # JavaFX GUI client
│   ├── server/          # Multi-threaded streaming server
│   ├── database/        # JDBC connection layer
│   ├── compile.sh/bat   # Build scripts
│   └── run-*.sh/bat     # Execution scripts
├── docs/
│   └── database_schema.sql
└── README.md            # Full documentation
```

## Testing the Application

1. Start server (should show "VideoServer is running...")
2. Launch client (GUI window opens)
3. Click any video in the library sidebar
4. Video streams and plays automatically
5. Use controls: Play/Pause, Stop, Next, Seek, Volume

## Need Help?

- Check the full [README.md](README.md) for detailed documentation
- Review [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines
- Open an issue on GitHub for bugs or questions
