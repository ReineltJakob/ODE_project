package at.fhw.commercial_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
/*
public class ImageViewer extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Pfad zur Bilddatei (ersetze durch deinen Bildpfad)
        String imagePath = "C:/Users/reine/IdeaProjects/Commercial_test/uploaded_files/HandIn2_Reinelt.PNG";

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.out.println("Bilddatei nicht gefunden: " + imagePath);
            return;
        }

        // Lade das Bild
        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView(image);

        // Bildgröße anpassen
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        // Layout erstellen
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 600, 400);

        // Fenster konfigurieren
        primaryStage.setTitle("Bildanzeige");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/