module at.fhw.commercial_test {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.logging;

    opens at.fhw.commercial_test to javafx.fxml;
    exports at.fhw.commercial_test;
}