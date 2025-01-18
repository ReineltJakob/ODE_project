package at.fhw.commercial_test;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {

    private static final Logger LOGGER = Logger.getLogger("CommercialTestLogger");

    static {
        try {
            // Log-Datei konfigurieren
            FileHandler fileHandler = new FileHandler("application.log", true); // 'true' = Anhängen an bestehende Datei
            fileHandler.setFormatter(new SimpleFormatter()); // Einfaches Textformat
            fileHandler.setLevel(Level.ALL); // Protokolliere alle Level
            LOGGER.addHandler(fileHandler);

            // Logger-Level auf "ALL" setzen, damit alles protokolliert wird
            LOGGER.setLevel(Level.ALL);

            // Abschalten des Standard-Konsolen-Handlers (optional)
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.OFF); // Entferne Standard-Konsolen-Output
        } catch (IOException e) {
            System.err.println("Fehler beim Initialisieren des Loggers: " + e.getMessage());
        }
    }

    /**
     * Gibt den zentralen Logger zurück.
     *
     * @return Logger
     */
    public static Logger getLogger() {
        return LOGGER;
    }
}
