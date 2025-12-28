package de.thm.se.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DataImporter implements CommandLineRunner {

    private final DatabaseCreator databaseCreator;

    public DataImporter() {
        this.databaseCreator = new DatabaseCreator();
    }

    @Override
    public void run(String... args) {
        try {
            // 1. Sicherstellen, dass die Tabellen existieren
            databaseCreator.createDatabase();

            // 2. Prüfen, ob bereits Daten existieren (um Doppel-Import zu vermeiden)
            if (isDatabaseEmpty()) {
                System.out.println("Datenbank ist leer. Starte Import aus initial_data.json...");
                importData();
            } else {
                System.out.println("Datenbank enthält bereits Daten. Import übersprungen.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDatabaseEmpty() throws SQLException {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM STUDIENGANG")) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            // Falls Tabelle noch nicht existiert (sollte durch createDatabase verhindert sein), als leer betrachten
            return true;
        }
        return true;
    }

    private void importData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Datei aus dem resources-Ordner laden
        InputStream inputStream = getClass().getResourceAsStream("/initial_data.json");
        if (inputStream == null) {
            System.err.println("initial_data.json nicht gefunden!");
            return;
        }

        JsonNode root = mapper.readTree(inputStream);

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false); // Transaktion starten

            try {
                // Die Reihenfolge ist wichtig wegen Foreign Keys!
                importFachbereich(conn, root.get("fachbereich"));
                importStudiengang(conn, root.get("studiengang"));
                importSemesterzeit(conn, root.get("semesterzeit"));
                importSemester(conn, root.get("semester"));
                importStudierende(conn, root.get("studierende"));
                importBetreuer(conn, root.get("betreuer"));
                importPruefungsordnung(conn, root.get("pruefungsordnung"));
                importArbeiten(conn, root.get("wissenschaftliche_arbeit"));
                importZeitdaten(conn, root.get("zeitdaten"));
                importNoten(conn, root.get("notenbestandteil"));

                // Falls SWS Berechnungen im JSON sind:
                // importSws(conn, root.get("sws_berechnung"));

                conn.commit(); // Alles speichern
                System.out.println("Import erfolgreich abgeschlossen.");
            } catch (Exception e) {
                conn.rollback(); // Bei Fehler alles rückgängig machen
                throw e;
            }
        }
    }

    // --- Hilfsmethoden für jede Tabelle ---

    private void importFachbereich(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO FACHBEREICH (fachbereich_id, bezeichnung, fbname) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("fachbereich_id").asInt());
                pstmt.setString(2, item.get("bezeichnung").asText());
                pstmt.setString(3, item.has("fbname") ? item.get("fbname").asText() : null);
                pstmt.executeUpdate();
            }
        }
        System.out.println("Fachbereiche importiert.");
    }

    private void importStudiengang(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO STUDIENGANG (studiengang_id, bezeichnung, abschluss, aktiv) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("studiengang_id").asInt());
                pstmt.setString(2, item.get("bezeichnung").asText());
                pstmt.setString(3, item.get("abschluss").asText());
                pstmt.setInt(4, item.get("aktiv").asInt());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Studiengänge importiert.");
    }

    private void importSemesterzeit(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO SEMESTERZEIT (semesterzeit_id, beginn, ende, bezeichnung) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("semesterzeit_id").asInt());
                pstmt.setString(2, item.get("beginn").asText());
                pstmt.setString(3, item.get("ende").asText());
                pstmt.setString(4, item.get("bezeichnung").asText());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Semesterzeiten importiert.");
    }

    private void importSemester(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO SEMESTER (semester_id, semesterzeit_id, bezeichnung, typ, jahr) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("semester_id").asInt());
                pstmt.setInt(2, item.get("semesterzeit_id").asInt());
                pstmt.setString(3, item.get("bezeichnung").asText());
                pstmt.setString(4, item.get("typ").asText());
                pstmt.setInt(5, item.get("jahr").asInt());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Semester importiert.");
    }

    private void importStudierende(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT OR IGNORE INTO STUDIERENDE (studierenden_id, matrikelnummer, vorname, nachname, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("studierenden_id").asInt());
                pstmt.setString(2, item.get("matrikelnummer").asText());
                pstmt.setString(3, item.get("vorname").asText());
                pstmt.setString(4, item.get("nachname").asText());
                pstmt.setString(5, item.get("email").asText());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Studierende importiert.");
    }

    private void importBetreuer(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO BETREUER (betreuer_id, vorname, nachname, titel, rolle) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("betreuer_id").asInt());
                pstmt.setString(2, item.get("vorname").asText());
                pstmt.setString(3, item.get("nachname").asText());
                pstmt.setString(4, item.get("titel").asText());
                pstmt.setString(5, item.get("rolle").asText());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Betreuer importiert.");
    }

    private void importPruefungsordnung(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO PRUEFUNGSORDNUNG (po_id, studiengang_id, bezeichnung, gueltig_ab) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("po_id").asInt());
                pstmt.setInt(2, item.get("studiengang_id").asInt());
                pstmt.setString(3, item.get("bezeichnung").asText());
                pstmt.setString(4, item.get("gueltig_ab").asText());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Prüfungsordnungen importiert.");
    }

    private void importArbeiten(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO WISSENSCHAFTLICHE_ARBEIT (arbeit_id, studierenden_id, studiengang_id, pruefungsordnung_id, semester_id, titel, typ, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("arbeit_id").asInt());
                pstmt.setInt(2, item.get("studierenden_id").asInt());
                pstmt.setInt(3, item.get("studiengang_id").asInt());
                pstmt.setInt(4, item.get("pruefungsordnung_id").asInt());
                pstmt.setInt(5, item.get("semester_id").asInt());
                pstmt.setString(6, item.get("titel").asText());
                pstmt.setString(7, item.get("typ").asText());
                pstmt.setString(8, item.get("status").asText());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Arbeiten importiert.");
    }

    private void importZeitdaten(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO ZEITDATEN (zeit_id, arbeit_id, anfangsdatum, abgabedatum, kolloquiumsdatum) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("zeit_id").asInt());
                pstmt.setInt(2, item.get("arbeit_id").asInt());
                // Datum kann null sein
                pstmt.setString(3, item.hasNonNull("anfangsdatum") ? item.get("anfangsdatum").asText() : null);
                pstmt.setString(4, item.hasNonNull("abgabedatum") ? item.get("abgabedatum").asText() : null);
                pstmt.setString(5, item.hasNonNull("kolloquiumsdatum") ? item.get("kolloquiumsdatum").asText() : null);
                pstmt.executeUpdate();
            }
        }
        System.out.println("Zeitdaten importiert.");
    }

    private void importNoten(Connection conn, JsonNode node) throws SQLException {
        String sql = "INSERT INTO NOTENBESTANDTEIL (noten_id, arbeit_id, betreuer_id, note_arbeit, note_kolloquium, rolle) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (JsonNode item : node) {
                pstmt.setInt(1, item.get("noten_id").asInt());
                pstmt.setInt(2, item.get("arbeit_id").asInt());
                pstmt.setInt(3, item.get("betreuer_id").asInt());

                if (item.hasNonNull("note_arbeit")) pstmt.setDouble(4, item.get("note_arbeit").asDouble());
                else pstmt.setNull(4, java.sql.Types.REAL);

                if (item.hasNonNull("note_kolloquium")) pstmt.setDouble(5, item.get("note_kolloquium").asDouble());
                else pstmt.setNull(5, java.sql.Types.REAL);

                pstmt.setString(6, item.get("rolle").asText());
                pstmt.executeUpdate();
            }
        }
        System.out.println("Notenbestandteile importiert.");
    }
}