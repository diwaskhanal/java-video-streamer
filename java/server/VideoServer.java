package server;

import database.DBConnection;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoServer {
    private static final int PORT = 5001;
    private static final int DEFAULT_VIDEO_ID = 1;
    private static final int CHUNK_SIZE = 64 * 1024;
    private static final String FALLBACK_VIDEO_PATH = "/Users/diwas/Documents/java/test.mp4";
    private static final int MAX_CLIENT_THREADS = 8;

    public static void main(String[] args) {
        System.out.println("VideoServer starting on port " + PORT + "...");
        ExecutorService clientExecutor = Executors.newFixedThreadPool(MAX_CLIENT_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("VideoServer is running. Waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                clientExecutor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            clientExecutor.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                Socket socket = clientSocket;
                DataInputStream dataInput = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream())
            ) {
                String command = dataInput.readUTF();

                if ("GET_CATALOG".equals(command)) {
                    sendCatalog(dataOutput);
                    return;
                }

                if (command != null && command.startsWith("STREAM:")) {
                    int requestedVideoId = parseVideoId(command);
                    streamVideoById(requestedVideoId, dataOutput, socket.getInetAddress().getHostAddress());
                } else {
                    System.err.println("Unknown command from client: " + command);
                }
            } catch (IOException e) {
                System.err.println(
                    "Client request error for "
                        + clientSocket.getInetAddress().getHostAddress()
                        + ": "
                        + e.getMessage()
                );
            }
        }

        private void sendCatalog(DataOutputStream dataOutput) throws IOException {
            List<DBConnection.VideoItem> items = DBConnection.getVideoCatalog();
            System.out.println("Server built catalog with " + items.size() + " items");

            dataOutput.writeInt(items.size());
            for (DBConnection.VideoItem item : items) {
                dataOutput.writeInt(item.getId());
                dataOutput.writeUTF(item.getTitle() == null ? "Untitled" : item.getTitle());
            }
            dataOutput.flush();
        }

        private int parseVideoId(String command) {
            try {
                return Integer.parseInt(command.substring("STREAM:".length()).trim());
            } catch (NumberFormatException e) {
                System.err.println(
                    "Invalid stream command, defaulting to ID "
                        + DEFAULT_VIDEO_ID
                        + ": "
                        + command
                );
                return DEFAULT_VIDEO_ID;
            }
        }

        private void streamVideoById(int requestedVideoId, DataOutputStream dataOutput, String clientIp) throws IOException {
            String videoPath = DBConnection.getVideoFilePathById(requestedVideoId);
            if (videoPath == null || videoPath.isBlank()) {
                videoPath = FALLBACK_VIDEO_PATH;
                System.out.println(
                    "Using fallback video path for video ID "
                        + requestedVideoId
                        + ": "
                        + videoPath
                );
            }

            try (BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(videoPath))) {
                dataOutput.writeLong(new java.io.File(videoPath).length());

                byte[] buffer = new byte[CHUNK_SIZE];
                int bytesRead;
                long totalBytesSent = 0;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    dataOutput.write(buffer, 0, bytesRead);
                    totalBytesSent += bytesRead;
                }
                dataOutput.flush();

                System.out.println(
                    "Streaming finished for client "
                        + clientIp
                        + " | video ID: "
                        + requestedVideoId
                        + " | total bytes sent: "
                        + totalBytesSent
                );
            }
        }
    }
}
