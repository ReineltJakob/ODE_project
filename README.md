# ODE_project

Projektübersicht
1. Server-Komponente:
Entwickelt mit JavaFX und dient als zentrale Verwaltungsstelle für Bilddateien.

Funktionen:
    
Authentifizierung: Benutzer müssen sich mit einem Benutzername-Passwort-Konto anmelden (Beispiel: admin mit password123).

Datei-Upload: Benutzer können Bilder (.jpg, .png) auswählen und auf den Server hochladen. Diese werden in einem definierten Verzeichnis (uploads)     gespeichert.

Aktivierung von Bildern: Benutzer können bis zu 4 Bilder gleichzeitig aktivieren, die dann für die Übertragung an Clients bereitgestellt werden.

Protokollierung: Alle wichtigen Ereignisse (z. B. Datei-Uploads, Fehler, Benutzeraktionen) werden mit einem Logger dokumentiert.


2. Client-Komponente:
JavaFX-Anwendung, die es ermöglicht, die vom Server bereitgestellten Bilder anzuzeigen und zu verwalten.

Funktionen:

Bilder abrufen: Der Client lädt die aktivierten Bilder vom Server herunter.

Bilder anzeigen: Die Bilder werden in einer Gitteransicht dargestellt.

Sortieren: Bilder können nach ihrem Upload-Datum oder nach ihrer relativen "Alter" (Zeit seit Upload) sortiert werden.

Entfernen: Der Benutzer kann Bilder aus der Ansicht entfernen.

Rückgängigmachen: Entfernte Bilder können mit der "Undo"-Funktion wiederhergestellt werden.


3.Datenmodell:

Die Klasse ImageData speichert Metadaten der Bilder, wie den Dateinamen und das Upload-Datum, und berechnet das relative Alter der Bilder.


4.Logger-System:

Der Logger dokumentiert alle wichtigen Aktionen und Fehler in einer Datei (application.log) und dient der Nachverfolgbarkeit und Fehleranalyse.
