package at.fhw.commercial_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client extends Application {
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

        sortOptions.getItems().addAll("Sort by Upload Date", "Sort by Image Age");
        sortOptions.setValue("Sort by Upload Date");
        sortOptions.setOnAction(e -> {
            String selectedOption = sortOptions.getValue();
            sortImages(selectedOption);
        });

        refreshButton.setOnAction(e -> loadImages());

        layout.getChildren().addAll(refreshButton, sortOptions, imageGrid);

        Scene scene = new Scene(layout, 600, 630);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadImages() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            int fileCount = in.readInt();
            imageList.clear();

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
            }

            updateImageViews();
        } catch (IOException e) {
            e.printStackTrace();
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