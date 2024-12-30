package at.fhw.commercial_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class Client extends Application {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image Viewer Client");

        GridPane gridPane = new GridPane();
        ImageView[] imageViews = new ImageView[4];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView();
            imageViews[i].setFitWidth(300);
            imageViews[i].setFitHeight(300);
            imageViews[i].setPreserveRatio(true);
            gridPane.add(imageViews[i], i % 2, i / 2);
        }

        Button refreshButton = new Button("Refresh Images");
        refreshButton.setOnAction(e -> {
            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {
                int fileCount = in.readInt();
                for (ImageView imageView : imageViews) {
                    imageView.setImage(null);
                }

                for (int i = 0; i < fileCount && i < imageViews.length; i++) {
                    String fileName = in.readUTF();
                    int fileLength = in.readInt();
                    byte[] fileBytes = new byte[fileLength];
                    in.readFully(fileBytes);

                    File tempFile = File.createTempFile("temp_", fileName);
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        fos.write(fileBytes);
                    }
                    imageViews[i].setImage(new Image(tempFile.toURI().toString()));
                    tempFile.deleteOnExit();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        gridPane.add(refreshButton, 0, 2, 2, 1);

        Scene scene = new Scene(gridPane, 600, 630);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
