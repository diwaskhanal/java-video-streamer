# JavaFX Video Streaming System

A multi-threaded client-server video streaming application built with Java, JavaFX, Socket Programming, and MySQL. This project demonstrates advanced Java concepts including network programming, concurrent thread handling, database connectivity, and modern GUI design.

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-26-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Technical Highlights](#technical-highlights)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

- **Real-time Video Streaming**: Stream videos from server to client over TCP sockets
- **Multi-threaded Server**: Handle multiple concurrent client connections using thread pooling
- **Interactive JavaFX GUI**: Modern, responsive user interface with custom styling
- **Video Library Management**: Browse and select videos from a database-backed catalog
- **Media Controls**: Full playback controls (play, pause, stop, seek, volume)
- **Network Logging**: Real-time network activity monitoring
- **Database Integration**: MySQL-backed video metadata storage
- **Connection Resilience**: Automatic fallback and error handling

## 🏗️ Architecture

### Client-Server Model

```
┌─────────────────┐         TCP Socket         ┌─────────────────┐
│   VideoClient   │◄─────────────────────────►│   VideoServer   │
│   (JavaFX UI)   │    Port: 5001             │  (Multi-thread)  │
└─────────────────┘                           └─────────────────┘
        │                                              │
        │                                              │
        ▼                                              ▼
   MediaPlayer                                  ┌──────────────┐
   (Playback)                                   │    MySQL     │
                                                │   Database   │
                                                └──────────────┘
```

### Components

1. **VideoServer** - Multi-threaded server handling client connections and streaming video data
2. **VideoClient** - JavaFX-based client with media player and catalog browser
3. **DBConnection** - Database access layer for video metadata and file paths

## 🔧 Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
  - [Download JDK](https://www.oracle.com/java/technologies/downloads/)
  
- **JavaFX SDK** 26 or compatible version
  - [Download JavaFX](https://openjfx.io/)
  - Extract to a known location (e.g., `~/Documents/JavaLibraries/javafx-sdk-26`)

- **MySQL Server** 8.0 or higher
  - [Download MySQL](https://dev.mysql.com/downloads/mysql/)
  
- **MySQL Connector/J** (JDBC Driver)
  - Included in project as `mysql-connector-j-9.6.0.jar`
  - Or [download latest](https://dev.mysql.com/downloads/connector/j/)

## 📦 Installation

### 1. Clone the Repository

```bash
git clone git@github.com:diwaskhanal/javafx-video-streaming.git
cd javafx-video-streaming
```

### 2. Install JavaFX SDK

Download and extract JavaFX SDK to your preferred location. Note the path for compilation.

### 3. Setup MySQL Connector

The MySQL connector JAR is included in the `java/` directory. Alternatively, download and place it in the project root.

## 🗄️ Database Setup

### Create Database and Table

Run the following SQL commands in your MySQL client:

```sql
-- Create database
CREATE DATABASE video_stream_db;

-- Use the database
USE video_stream_db;

-- Create videos table
CREATE TABLE videos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO videos (title, file_path) VALUES 
    ('Sample Video 1', '/path/to/your/video1.mp4'),
    ('Sample Video 2', '/path/to/your/video2.mp4'),
    ('Demo Clip', '/path/to/your/video3.mp4');
```

### Configure Database Connection

Update database credentials in `java/database/DBConnection.java` if needed:

```java
private static final String[] DB_URLS = {
    "jdbc:mysql://localhost:3306/video_db",
    "jdbc:mysql://localhost:3306/video_stream_db"
};
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

### Add Your Video Files

1. Place `.mp4` video files in an accessible location
2. Update the `file_path` column in the database to point to your video files
3. Ensure the server has read permissions for these files

## 🚀 Usage

### Step 1: Compile the Project

Navigate to the `java/` directory and compile:

```bash
cd java

# Compile all components
javac --module-path /path/to/javafx-sdk-26/lib \
      --add-modules javafx.controls,javafx.media \
      -cp ".:mysql-connector-j-9.6.0.jar" \
      database/DBConnection.java \
      server/VideoServer.java \
      client/VideoClient.java \
      -d .
```

**Note**: Replace `/path/to/javafx-sdk-26/lib` with your actual JavaFX SDK path.

### Step 2: Start the Server

In one terminal:

```bash
# Start the video server
java -cp ".:mysql-connector-j-9.6.0.jar" server.VideoServer
```

You should see:
```
VideoServer starting on port 5001...
VideoServer is running. Waiting for client connections...
```

### Step 3: Launch the Client

In another terminal:

```bash
# Launch the JavaFX client
java --module-path /path/to/javafx-sdk-26/lib \
     --add-modules javafx.controls,javafx.media \
     -cp ".:mysql-connector-j-9.6.0.jar" \
     client.VideoClient
```

### Step 4: Stream Videos

1. The client GUI will open with a video library sidebar
2. Click on any video from the library to start streaming
3. Use the media controls to play, pause, seek, and adjust volume
4. Monitor network activity in the expandable log panel at the bottom

## 📁 Project Structure

```
javafx-video-streaming/
├── java/
│   ├── client/
│   │   ├── VideoClient.java      # JavaFX client application
│   │   └── style.css              # UI styling
│   ├── server/
│   │   └── VideoServer.java       # Multi-threaded socket server
│   ├── database/
│   │   └── DBConnection.java      # JDBC database layer
│   ├── mysql-connector-j-9.6.0.jar
│   └── README.md
├── JavaLibraries/                 # External libraries (gitignored)
├── docs/
│   └── database_schema.sql        # Database setup script
├── .gitignore
└── README.md
```

## 💡 Technical Highlights

### Advanced Java Concepts Demonstrated

- **Socket Programming**: TCP client-server communication
- **Multi-threading**: ExecutorService with thread pooling
- **JDBC**: Prepared statements and connection pooling
- **JavaFX**: Modern UI with FXML-less architecture
- **Stream Processing**: Chunked data transfer with buffering
- **Exception Handling**: Graceful error recovery
- **Resource Management**: Try-with-resources pattern

### Key Technologies

| Technology | Purpose |
|------------|---------|
| Java 17+ | Core programming language |
| JavaFX 26 | GUI framework and media player |
| Socket API | Network communication |
| JDBC | Database connectivity |
| MySQL | Video metadata storage |
| CSS | UI styling |

## 🎓 Educational Context

This project was developed as part of the **Advanced Java Programming (CSC409)** course at Tribhuvan University, BSc. CSIT program. It covers the following syllabus units:

- **Unit 1**: Java Programming Fundamentals
- **Unit 4**: Networking with Java
- **Unit 5**: Database Connectivity (JDBC)
- **Unit 6**: JavaFX and GUI Development

## 🐛 Troubleshooting

### Common Issues

**Issue**: `Connection refused` error in client
- **Solution**: Ensure the server is running before starting the client

**Issue**: `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
- **Solution**: Verify MySQL connector JAR is in classpath

**Issue**: Videos not playing
- **Solution**: Check file paths in database match actual video locations

**Issue**: JavaFX runtime components are missing
- **Solution**: Ensure `--module-path` and `--add-modules` flags are correctly set

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👤 Author

**Diwas Khanal**

- GitHub: [@diwaskhanal](https://github.com/diwaskhanal)

## 🙏 Acknowledgments

- Tribhuvan University, Department of Computer Science and Information Technology
- JavaFX community for excellent documentation
- MySQL for robust database management

---

⭐ If you found this project helpful, please consider giving it a star!
