# ODE_project

Beschreibung:
Dieses Programm besteht aus einem Client- und einem Server-Teil, die gemeinsam eine Bildverwaltungsanwendung implementieren. Die Anwendung ermöglicht das Hochladen, Anzeigen und Verwalten von Bildern sowie deren Übertragung zwischen einem Server und mehreren Clients.

--Server:

Verarbeitet Datei-Uploads vom Client.

Unterstützt die Aktivierung von bis zu 4 Dateien gleichzeitig.

Stellt aktive Dateien für Clients bereit.

--Client:

Lädt Bilder vom Server herunter.

Zeigt heruntergeladene Bilder in einer Galerie an.

Unterstützt Sortieren, Entfernen und Rückgängigmachen von Aktionen.

Features
--Server

Benutzer-Login mit vordefinierten Anmeldedaten.

Hochladen von Dateien (.jpg, .png) in ein spezielles Upload-Verzeichnis.

Aktivieren von bis zu 4 Dateien gleichzeitig.

Netzwerkkommunikation, um aktive Dateien an Clients zu senden.

--Client

Anzeige heruntergeladener Bilder in einer Galerie.

Sortieren von Bildern nach Upload-Datum oder Alter.

Entfernen ausgewählter Bilder aus der Galerie.

Rückgängigmachen der letzten Entfernen-Aktion.

Kommunikation mit dem Server zur Bildübertragung.

Projektstruktur

Das Projekt ist in mehrere Klassen unterteilt:

Server

ServerApp: Hauptklasse des Servers. Verantwortlich für die Benutzeroberfläche und die Steuerung der Serverfunktionalität.

FileManager: Verwaltet Dateien, einschließlich Hochladen, Aktivieren und Aktualisieren der Dateiliste.

ServerNetwork: Verwaltet die Netzwerkkommunikation und stellt aktive Dateien für Clients bereit.

LoggerConfig: Konfiguriert das zentrale Logging-System für die Anwendung.

Client

Client: Hauptklasse des Clients. Enthält die gesamte Logik zur Anzeige und Verwaltung von Bildern sowie zur Kommunikation mit dem Server.

ImageData: Datenklasse, die Metadaten zu Bildern speichert (z. B. Dateiname, Upload-Datum).

Verwendung

--Server

Starten Sie die ServerApp.

Melden Sie sich mit einem der vordefinierten Benutzer an:

Benutzername: admin, Passwort: password123

Benutzername: user1, Passwort: mypassword

Benutzername: guest, Passwort: guest123

Laden Sie Dateien über die Upload-Schaltfläche hoch.

Aktivieren Sie bis zu 4 Dateien über die Dateiliste.

--Client

Starten Sie die Client-Anwendung.

Klicken Sie auf Refresh Images, um Bilder vom Server herunterzuladen.

Sortieren Sie die Bilder nach Upload-Datum oder Alter.

Entfernen Sie Bilder und machen Sie diese Aktionen rückgängig.
