package at.fhw.commercial_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.*;
import java.util.logging.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client extends Application {

    private static final Logger LOGGER = LoggerConfig.getLogger();

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    private ObservableList<ImageData> imageList = FXCollections.observableArrayList();
    private GridPane imageGrid = new GridPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image Viewer Client");

        VBox layout = new VBox(10);

        Button refreshButton = new Button("Refresh Images");
        ComboBox<String> sortOptions = new ComboBox<>();
        Button removeButton = new Button("Remove Selected Images");
        Button undoButton = new Button("Undo Last Action");

        HBox buttonRow = new HBox(10, refreshButton, sortOptions, removeButton, undoButton);

        sortOptions.getItems().addAll("Sort by Upload Date", "Sort by Image Age");
        sortOptions.setValue("Sort by Upload Date");
        sortOptions.setOnAction(e -> {
            String selectedOption = sortOptions.getValue();
            sortImages(selectedOption);
        });

        refreshButton.setOnAction(e -> {
            LOGGER.info("Refresh Images-Button wurde gedrückt.");
            loadImages();
        });

        removeButton.setOnAction(e -> {
            LOGGER.info("Remove Selected Images-Button wurde gedrückt.");
            removeSelectedImages();
        });

        undoButton.setOnAction(e -> {
            LOGGER.info("Undo Last Action-Button wurde gedrückt.");
            undoAllRemovedImages();
        });

        layout.getChildren().addAll(buttonRow, imageGrid);

        Scene scene = new Scene(layout, 600, 630);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private final Stack<List<ImageData>> undoStack = new Stack<>(); // Stack to store lists of removed images

    private void loadImages() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            int fileCount = in.readInt();

            // **Wichtig: Liste und Stack zurücksetzen, um konsistente Daten zu gewährleisten**
            imageList.clear();
            undoStack.clear();

            for (int i = 0; i < fileCount; i++) {
                String fileName = in.readUTF();
                long lastModified = in.readLong();
                int fileLength = in.readInt();

                byte[] fileBytes = new byte[fileLength];
                in.readFully(fileBytes);

                File tempFile = File.createTempFile("temp_", fileName);
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(fileBytes);
                }

                imageList.add(new ImageData(tempFile.getAbsolutePath(), new Date(lastModified)));
                tempFile.deleteOnExit();
                LOGGER.info("Bild erfolgreich geladen: " + fileName + " (" + fileLength + " Bytes)");
            }

            updateImageViews();
        } catch (IOException e) {
            LOGGER.severe("Verbindung fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeSelectedImages() {
        List<ImageData> selectedImages = new ArrayList<>();

        for (ImageData imageData : imageList) {
            File imageFile = new File(imageData.getFileName());
            if (imageGrid.getChildren().stream().anyMatch(node -> {
                if (node instanceof ImageView) {
                    ImageView imageView = (ImageView) node;
                    return imageView.getImage().getUrl().equals(imageFile.toURI().toString());
                }
                return false;
            })) {
                selectedImages.add(imageData);
            }
        }

        if (!selectedImages.isEmpty()) {
            undoStack.push(new ArrayList<>(selectedImages)); // Store all removed items as a list in the stack
            imageList.removeAll(selectedImages);
            LOGGER.info("Bilder entfernt: " + selectedImages.size());
            updateImageViews();
        } else {
            LOGGER.info("Keine Bilder zum Entfernen ausgewählt.");
        }
    }

    private void undoAllRemovedImages() {
        if (!undoStack.isEmpty()) {
            List<ImageData> lastRemovedImages = undoStack.pop();
            imageList.addAll(lastRemovedImages);

            // **Aktualisiere die Ansicht vollständig**
            LOGGER.info("Alle zuletzt entfernten Bilder wurden wiederhergestellt: " + lastRemovedImages.size());
            updateImageViews();
        } else {
            LOGGER.info("Keine Aktion zum Rückgängigmachen vorhanden.");
        }
    }

    private void sortImages(String criteria) {
        if (criteria.equals("Sort by Upload Date")) {
            imageList.sort(Comparator.comparing(ImageData::getUploadDate));
        } else if (criteria.equals("Sort by Image Age")) {
            imageList.sort(Comparator.comparingLong(ImageData::getImageAgeInMinutes));
        }
        updateImageViews();
    }

    private void updateImageViews() {
        imageGrid.getChildren().clear();
        int columns = 2;

        for (int i = 0; i < imageList.size(); i++) {
            ImageData imageData = imageList.get(i);
            File imageFile = new File(imageData.getFileName());

            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);

                imageGrid.add(imageView, i % columns, i / columns);
            }
        }
    }
}
