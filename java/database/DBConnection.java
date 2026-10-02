package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    private static final String[] DB_URLS = {
        "jdbc:mysql://localhost:3306/video_db",
        "jdbc:mysql://localhost:3306/video_stream_db"
    };
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found in classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        SQLException lastError = null;
        for (String dbUrl : DB_URLS) {
            try {
                return DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD);
            } catch (SQLException e) {
                lastError = e;
            }
        }
        throw lastError != null ? lastError : new SQLException("Unable to connect to configured databases.");
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static String getVideoFilePathById(int videoId) {
        String sql = "SELECT file_path FROM videos WHERE id = ?";

        try (
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, videoId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("file_path");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching video file path: " + e.getMessage());
        }

        return null;
    }

    public static List<VideoItem> getVideoCatalog() {
        String sql = "SELECT id, title FROM videos ORDER BY id";
        List<VideoItem> catalog = new ArrayList<>();

        try (
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                catalog.add(new VideoItem(rs.getInt("id"), rs.getString("title")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching video catalog: " + e.getMessage());
        }

        return catalog;
    }

    public static int getNextVideoId(int currentVideoId) {
        String sql = "SELECT id FROM videos WHERE id > ? ORDER BY id ASC LIMIT 1";

        try (
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, currentVideoId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching next video id: " + e.getMessage());
        }

        return -1;
    }

    public static final class VideoItem {
        private final int id;
        private final String title;

        public VideoItem(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    public static void main(String[] args) {
        boolean isConnected = testConnection();
        System.out.println("Database connection status: " + (isConnected ? "SUCCESS" : "FAILED"));

        String samplePath = getVideoFilePathById(1);
        if (samplePath != null) {
            System.out.println("Video path for ID 1: " + samplePath);
        } else {
            System.out.println("No video found for ID 1.");
        }
    }
}
