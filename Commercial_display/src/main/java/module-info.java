module at.fhw.commercial_display {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens at.fhw.commercial_display to javafx.fxml;
    exports at.fhw.commercial_display;
}