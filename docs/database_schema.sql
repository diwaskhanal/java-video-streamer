-- Video Streaming System Database Schema
-- Database: video_stream_db
-- Purpose: Store video metadata and file paths for the streaming application

-- Create database
CREATE DATABASE IF NOT EXISTS video_stream_db;

-- Use the database
USE video_stream_db;

-- Drop table if exists (for clean setup)
DROP TABLE IF EXISTS videos;

-- Create videos table
CREATE TABLE videos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    description TEXT,
    duration_seconds INT,
    file_size_mb DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data
-- Note: Update file paths to match your actual video locations
INSERT INTO videos (title, file_path, description, duration_seconds, file_size_mb) VALUES
    ('Sample Video 1', '/path/to/your/video1.mp4', 'First sample video', 120, 12.5),
    ('Sample Video 2', '/path/to/your/video2.mp4', 'Second sample video', 180, 38.2),
    ('Demo Clip', '/path/to/your/video3.mp4', 'Demo video clip', 90, 4.5),
    ('Test Video', '/path/to/your/video4.mp4', 'Test video for streaming', 150, 39.0);

-- Verify data insertion
SELECT * FROM videos;

-- Grant privileges (if needed)
-- GRANT ALL PRIVILEGES ON video_stream_db.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;
