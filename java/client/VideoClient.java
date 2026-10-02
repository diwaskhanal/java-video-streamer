package client;

import database.DBConnection;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.stage.Stage;

public class VideoClient extends Application {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5001;
    private static final int DEFAULT_VIDEO_ID = 1;

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Label placeholderLabel;
    private Label statusLabel;
    private TextArea networkLogArea;
    private VBox sidebarList;
    private Button playPauseButton;
    private Button stopButton;
    private Button nextButton;
    private Slider progressSlider;
    private Slider volumeSlider;
    private final List<DBConnection.VideoItem> videoCatalog = new ArrayList<>();
    private int currentVideoId = DEFAULT_VIDEO_ID;
    private String currentVideoTitle = "Video 1";
    private boolean isUserSeeking;

    @Override
    public void start(Stage primaryStage) {
        mediaView = new MediaView();
        mediaView.setFitWidth(840);
        mediaView.setFitHeight(460);
        mediaView.setPreserveRatio(true);
        mediaView.getStyleClass().add("video-view");

        placeholderLabel = new Label(
            "Welcome! Please select a video from the library to begin streaming."
        );
        placeholderLabel.getStyleClass().add("placeholder-label");
        StackPane videoContainer = new StackPane(mediaView, placeholderLabel);
        videoContainer.getStyleClass().add("video-container");
        StackPane.setAlignment(placeholderLabel, Pos.CENTER);

        statusLabel = new Label("Ready to stream");
        statusLabel.getStyleClass().add("status-label");

        playPauseButton = new Button("Play");
        stopButton = new Button("Stop");
        nextButton = new Button("Next");
        playPauseButton.getStyleClass().add("control-button");
        stopButton.getStyleClass().add("control-button");
        nextButton.getStyleClass().add("control-button");

        playPauseButton.setOnAction(event -> togglePlayPause());
        stopButton.setOnAction(event -> stopPlayback());
        nextButton.setOnAction(event -> handleNext());

        progressSlider = new Slider(0, 100, 0);
        progressSlider.getStyleClass().add("progress-slider");
        progressSlider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            isUserSeeking = isChanging;
            if (!isChanging) {
                seekFromProgressSlider();
            }
        });
        progressSlider.setOnMouseReleased(event -> seekFromProgressSlider());

        volumeSlider = new Slider(0, 1, 0.8);
        volumeSlider.getStyleClass().add("volume-slider");
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue());
            }
        });
        Label volumeLabel = new Label("Volume");
        volumeLabel.getStyleClass().add("status-label");
        Label progressLabel = new Label("Progress");
        progressLabel.getStyleClass().add("status-label");

        HBox controlBar = new HBox(
            10,
            playPauseButton,
            stopButton,
            nextButton,
            progressLabel,
            progressSlider,
            volumeLabel,
            volumeSlider
        );
        controlBar.setAlignment(Pos.CENTER_LEFT);
        controlBar.getStyleClass().add("control-bar");
        HBox.setHgrow(volumeSlider, Priority.ALWAYS);

        VBox leftPane = new VBox(12, videoContainer, controlBar, statusLabel);
        leftPane.getStyleClass().add("left-pane");
        leftPane.setPadding(new Insets(8));
        VBox.setVgrow(videoContainer, Priority.ALWAYS);

        Label sidebarTitle = new Label("Video Library");
        sidebarTitle.getStyleClass().add("sidebar-title");
        sidebarList = new VBox(8);
        sidebarList.getStyleClass().add("sidebar-list");
        ScrollPane sidebarScroll = new ScrollPane(sidebarList);
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.getStyleClass().add("sidebar-scroll");

        VBox rightPane = new VBox(10, sidebarTitle, sidebarScroll);
        rightPane.getStyleClass().add("sidebar-pane");
        rightPane.setPadding(new Insets(8));
        VBox.setVgrow(sidebarScroll, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane(leftPane, rightPane);
        splitPane.setDividerPositions(0.75);
        splitPane.getStyleClass().add("app-split-pane");

        networkLogArea = new TextArea();
        networkLogArea.setEditable(false);
        networkLogArea.setWrapText(true);
        networkLogArea.setPrefRowCount(5);
        networkLogArea.setPromptText("Network log will appear here...");
        networkLogArea.getStyleClass().add("network-log");

        TitledPane logPane = new TitledPane("Network Log", networkLogArea);
        logPane.setExpanded(false);
        logPane.getStyleClass().add("log-pane");

        BorderPane root = new BorderPane();
        root.setCenter(splitPane);
        root.setBottom(logPane);
        BorderPane.setMargin(logPane, new Insets(8, 12, 12, 12));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 1180, 720);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setTitle("Video Streaming Client");
        primaryStage.setScene(scene);
        initialize();
        fetchCatalogFromServer();
        primaryStage.show();
        appendNetworkLog("UI initialized. Waiting to start stream.");
    }

    private void initialize() {
        appendNetworkLog("Requesting catalog from server...");
    }

    private void fetchCatalogFromServer() {
        Thread catalogThread = new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInput = new DataInputStream(socket.getInputStream())
            ) {
                dataOutput.writeUTF("GET_CATALOG");
                dataOutput.flush();

                int count = dataInput.readInt();
                List<DBConnection.VideoItem> items = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    int id = dataInput.readInt();
                    String title = dataInput.readUTF();
                    items.add(new DBConnection.VideoItem(id, title));
                }

                Platform.runLater(() -> populateSidebar(items));
                Platform.runLater(() -> statusLabel.setText("Catalog loaded from server"));
                appendNetworkLog("Catalog received. Items: " + items.size());
            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("Catalog request failed"));
                appendNetworkLog("Catalog fetch failed: " + e.getMessage());
            }
        });
        catalogThread.setName("catalog-fetch-thread");
        catalogThread.setDaemon(true);
        catalogThread.start();
    }

    private void populateSidebar(List<DBConnection.VideoItem> items) {
        videoCatalog.clear();
        videoCatalog.addAll(items);
        System.out.println("Sidebar items: " + items.size());
        sidebarList.getChildren().clear();

        for (DBConnection.VideoItem item : items) {
            Button itemButton = new Button(item.getId() + ". " + item.getTitle());
            itemButton.setMaxWidth(Double.MAX_VALUE);
            itemButton.getStyleClass().add("sidebar-item");
            itemButton.setOnAction(event -> {
                currentVideoId = item.getId();
                currentVideoTitle = item.getTitle();
                startStreamingForVideo(currentVideoId, currentVideoTitle);
            });
            sidebarList.getChildren().add(itemButton);
        }

        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            placeholderLabel.setVisible(false);
        }
    }

    private void startStreamingForVideo(int videoId, String title) {
        statusLabel.setText("Connecting to server...");
        appendNetworkLog(
            "Attempting connection to " + SERVER_HOST + ":" + SERVER_PORT + " for video ID " + videoId
        );

        Thread streamThread = new Thread(() -> {
            try {
                File tempVideoFile = File.createTempFile("streamed_video_" + videoId + "_", ".mp4");
                tempVideoFile.deleteOnExit();
                appendNetworkLog("Temp file created for " + title + ": " + tempVideoFile.getAbsolutePath());

                receiveStreamToFile(videoId, tempVideoFile);
                playVideo(tempVideoFile);
            } catch (ConnectException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Connection refused on port " + SERVER_PORT);
                    showConnectionRefusedAlert();
                });
                appendNetworkLog("Connection refused. Check if server is running on port " + SERVER_PORT);
            } catch (IOException e) {
                final String message = "Streaming failed for video " + videoId + ": " + e.getMessage();
                Platform.runLater(() -> statusLabel.setText(message));
                appendNetworkLog(message);
            }
        });
        streamThread.setName("video-stream-thread-" + videoId);
        streamThread.setDaemon(true);
        streamThread.start();
    }

    private void receiveStreamToFile(int videoId, File outputFile) throws IOException {
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile)
        ) {
            Platform.runLater(() -> statusLabel.setText("Receiving stream bytes..."));
            appendNetworkLog("Connected to server: " + socket.getRemoteSocketAddress());

            dataOutput.writeUTF("STREAM:" + videoId);
            dataOutput.flush();
            appendNetworkLog("Sent request payload: STREAM:" + videoId);

            long totalLength = dataInput.readLong();
            byte[] buffer = new byte[64 * 1024];
            long totalBytes = 0;

            while (totalBytes < totalLength) {
                int bytesToRead = (int) Math.min(buffer.length, totalLength - totalBytes);
                int bytesRead = dataInput.read(buffer, 0, bytesToRead);
                if (bytesRead == -1) {
                    break;
                }
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            appendNetworkLog("Stream complete. Total bytes received: " + totalBytes);
        }
    }

    private void playVideo(File videoFile) {
        Platform.runLater(() -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }

                Media media = new Media(videoFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setVolume(volumeSlider.getValue());
                progressSlider.setValue(0);
                isUserSeeking = false;
                attachProgressListeners(mediaPlayer);

                mediaPlayer.setOnReady(() -> {
                    statusLabel.setText("Playing: " + currentVideoTitle);
                    appendNetworkLog("Media ready for: " + currentVideoTitle);
                    playPauseButton.setText("Pause");
                    mediaPlayer.play();
                });
                mediaPlayer.setOnPlaying(() -> placeholderLabel.setVisible(false));

                mediaPlayer.setOnError(() -> {
                    statusLabel.setText("Media error: " + mediaPlayer.getError());
                    appendNetworkLog("Media player error: " + mediaPlayer.getError());
                });
            } catch (Exception e) {
                statusLabel.setText("Cannot play received file: " + e.getMessage());
                appendNetworkLog("Playback setup failed: " + e.getMessage());
            }
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            if (!videoCatalog.isEmpty()) {
                DBConnection.VideoItem first = videoCatalog.get(0);
                currentVideoId = first.getId();
                currentVideoTitle = first.getTitle();
            }
            startStreamingForVideo(currentVideoId, currentVideoTitle);
            return;
        }

        MediaPlayer.Status status = mediaPlayer.getStatus();
        if (status == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("Play");
            appendNetworkLog("Playback paused");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("Pause");
            appendNetworkLog("Playback resumed");
        }
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playPauseButton.setText("Play");
            statusLabel.setText("Playback stopped");
            appendNetworkLog("Playback stopped by user");
            placeholderLabel.setVisible(true);
        }
    }

    private void handleNext() {
        if (videoCatalog == null || videoCatalog.isEmpty()) {
            appendNetworkLog("No next video available.");
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < videoCatalog.size(); i++) {
            if (videoCatalog.get(i).getId() == currentVideoId) {
                currentIndex = i;
                break;
            }
        }
        int nextIndex = currentIndex == -1 ? 0 : (currentIndex + 1) % videoCatalog.size();
        DBConnection.VideoItem nextVideo = videoCatalog.get(nextIndex);

        currentVideoId = nextVideo.getId();
        currentVideoTitle = nextVideo.getTitle();
        appendNetworkLog("Switching to next video: " + currentVideoId + " - " + currentVideoTitle);
        startStreamingForVideo(currentVideoId, currentVideoTitle);
    }

    private void attachProgressListeners(MediaPlayer player) {
        player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (isUserSeeking) {
                return;
            }
            Duration totalDuration = player.getTotalDuration();
            if (totalDuration == null || totalDuration.isUnknown() || totalDuration.lessThanOrEqualTo(Duration.ZERO)) {
                return;
            }
            double progress = (newTime.toMillis() / totalDuration.toMillis()) * 100.0;
            progressSlider.setValue(progress);
        });
    }

    private void seekFromProgressSlider() {
        if (mediaPlayer == null) {
            return;
        }
        Duration totalDuration = mediaPlayer.getTotalDuration();
        if (totalDuration == null || totalDuration.isUnknown() || totalDuration.lessThanOrEqualTo(Duration.ZERO)) {
            return;
        }
        double targetMillis = (progressSlider.getValue() / 100.0) * totalDuration.toMillis();
        mediaPlayer.seek(Duration.millis(targetMillis));
    }

    private void appendNetworkLog(String message) {
        Platform.runLater(() -> networkLogArea.appendText(message + System.lineSeparator()));
    }

    private void showConnectionRefusedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection Error");
        alert.setHeaderText("Server connection refused");
        alert.setContentText("Unable to connect to localhost:" + SERVER_PORT + ". "
            + "Please start the server and try again.");
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
