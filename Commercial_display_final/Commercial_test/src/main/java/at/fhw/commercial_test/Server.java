package at.fhw.commercial_test;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Server extends Application {
    private static final int PORT = 12345;
    private static final String UPLOAD_DIR = "uploads";
    private static final ObservableList<String> activeFiles = FXCollections.observableArrayList();
    private static final Map<String, String> userCredentials = new HashMap<>(); // Username and Password storage
    private boolean isAuthenticated = false;
    private static final Logger LOGGER = LoggerConfig.getLogger();

    public static void main(String[] args) {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // Example credentials
        userCredentials.put("admin", "password123");

        new Thread(() -> startServer()).start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Upload Server");

        Label statusLabel = new Label("Server is running...");
        Button loginButton = new Button("Login");
        Button uploadButton = new Button("Upload File");
        Button activateButton = new Button("Activate Selected Files");

        uploadButton.setDisable(true);
        activateButton.setDisable(true);

        ListView<String> fileListView = new ListView<>();
        fileListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        updateFileList(fileListView);

        loginButton.setOnAction(e -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Login");
            dialog.setHeaderText("Enter your credentials");

            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
                        isAuthenticated = true;
                        uploadButton.setDisable(false);
                        activateButton.setDisable(false);
                        statusLabel.setText("Login successful.");
                    } else {
                        statusLabel.setText("Invalid credentials.");
                    }
                }
                return null;
            });

            dialog.showAndWait();
        });

        uploadButton.setOnAction(e -> {
            if (!isAuthenticated) {
                statusLabel.setText("You must log in to upload files.");
                return;
            }

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
                    LOGGER.info("Datei hochgeladen: " + file.getName());
                } catch (IOException ex) {
                    statusLabel.setText("Failed to upload file: " + ex.getMessage());
                    LOGGER.severe("Fehler beim Hochladen der Datei: " + ex.getMessage());
                }
            }
        });

        activateButton.setOnAction(e -> {
            if (!isAuthenticated) {
                statusLabel.setText("You must log in to activate files.");
                return;
            }

            ObservableList<String> selectedFiles = fileListView.getSelectionModel().getSelectedItems();

            // **Überprüfung: Aktivierte Dateien müssen zurückgesetzt werden**
            if (selectedFiles.size() > 4) {
                statusLabel.setText("Cannot activate more than 4 files at a time.");
                LOGGER.warning("Activation limit exceeded: Attempted to activate more than 4 files.");
                return;
            }

            // Setze die aktiven Dateien zurück und aktualisiere mit den neuen ausgewählten Dateien
            activeFiles.clear();
            activeFiles.addAll(selectedFiles);

            statusLabel.setText("Activated files: " + String.join(", ", activeFiles));
            LOGGER.info("Aktivierte Dateien: " + String.join(", ", activeFiles));
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(loginButton, uploadButton, activateButton, fileListView, statusLabel);

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
            if (activeFiles.isEmpty()) {
                out.writeInt(0); // Keine aktiven Dateien
                LOGGER.info("Keine aktiven Dateien an den Client gesendet.");
                return;
            }

            out.writeInt(activeFiles.size()); // Anzahl der aktiven Dateien senden
            for (String fileName : activeFiles) {
                File file = new File(UPLOAD_DIR, fileName);
                if (file.exists()) {
                    byte[] fileBytes = new byte[(int) file.length()];
                    try (FileInputStream fis = new FileInputStream(file)) {
                        fis.read(fileBytes);
                    }
                    out.writeUTF(file.getName());
                    out.writeLong(file.lastModified()); // Letztes Änderungsdatum
                    out.writeInt(fileBytes.length);
                    out.write(fileBytes);
                    LOGGER.info("Datei gesendet: " + fileName);
                } else {
                    LOGGER.warning("Datei existiert nicht: " + fileName);
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Fehler beim Senden der Dateien an den Client: " + e.getMessage());
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
