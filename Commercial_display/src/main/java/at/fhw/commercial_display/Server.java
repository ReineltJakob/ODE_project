package at.fhw.commercial_test;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Application {
    private static final int PORT = 12345;
    private static final String UPLOAD_DIR = "uploads";
    private static final ObservableList<String> activeFiles = FXCollections.observableArrayList();

    public static void main(String[] args) {
        // Create upload directory if it doesn't exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // Start server thread
        new Thread(() -> startServer()).start();

        // Launch JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Upload Server");

        Label statusLabel = new Label("Server is running...");
        Button uploadButton = new Button("Upload File");
        Button activateButton = new Button("Activate Selected Files");

        ListView<String> fileListView = new ListView<>();
        fileListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        updateFileList(fileListView);

        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
            fileChooser.getExtensionFilters().add(imageFilter);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    File targetFile = new File(UPLOAD_DIR, file.getName());
                    copyFile(file, targetFile);
                    statusLabel.setText("File uploaded: " + file.getName());
                    updateFileList(fileListView);
                } catch (IOException ex) {
                    statusLabel.setText("Failed to upload file: " + ex.getMessage());
                }
            }
        });

        activateButton.setOnAction(e -> {
            activeFiles.clear();
            activeFiles.addAll(fileListView.getSelectionModel().getSelectedItems());
            statusLabel.setText("Activated files: " + String.join(", ", activeFiles));
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(uploadButton, activateButton, fileListView, statusLabel);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void updateFileList(ListView<String> fileListView) {
        File[] files = new File(UPLOAD_DIR).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        fileListView.getItems().clear();
        if (files != null) {
            for (File file : files) {
                fileListView.getItems().add(file.getName());
            }
        }
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
            out.writeInt(activeFiles.size());
            for (String fileName : activeFiles) {
                File file = new File(UPLOAD_DIR, fileName);
                byte[] fileBytes = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(fileBytes);
                }
                out.writeUTF(fileName);
                out.writeInt(fileBytes.length);
                out.write(fileBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File source, File destination) throws IOException {
        try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
