package de.thm.se;

import de.thm.se.backend.DataAcessLayer.ZeitdatenDAO;
import de.thm.se.backend.DataAcessLayer.WissenschaftlicheArbeitDAO;
import de.thm.se.backend.model.Zeitdaten;
import de.thm.se.backend.model.WissenschaftlicheArbeit;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test-Klasse für Zeitdaten-Validierung
 * Legt automatisch Testdaten an und räumt am Ende auf
 */
public class TestValidationZeitdaten {

    private static List<Integer> testArbeitIds = new ArrayList<>();
    private static List<Integer> erstellteZeitdatenIds = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║    ZEITDATEN-VALIDIERUNG TESTEN        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        try {
            // Setup: Testdaten anlegen
            if (!setupTestdaten()) {
                System.err.println("Setup fehlgeschlagen. Test wird abgebrochen.");
                return;
            }

            System.out.println();
            System.out.println("═══════════════════════════════════════");
            System.out.println("  VALIDIERUNGSTESTS STARTEN");
            System.out.println("═══════════════════════════════════════");
            System.out.println();

            ZeitdatenDAO dao = new ZeitdatenDAO();

            // Test 1: Gültige Zeitdaten
            test1GueltigeZeitdaten(dao);

            // Test 2: Anfangsdatum nach Abgabedatum (Fehler)
            test2FalscheReihenfolge(dao);

            // Test 3: Bearbeitungszeit zu kurz (Fehler)
            test3BearbeitungszeitZuKurz(dao);

            // Test 4: Kolloquium vor Abgabe (Fehler)
            test4KolloquiumVorAbgabe(dao);

            // Test 5: Ohne Kolloquiumsdatum (sollte funktionieren)
            test5OhneKolloquium(dao);

            System.out.println();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║    ALLE TESTS ABGESCHLOSSEN            ║");
            System.out.println("╚════════════════════════════════════════╝");

        } finally {
            // Cleanup: Testdaten aufräumen
            System.out.println();
            cleanDB();
        }
    }

    private static boolean setupTestdaten() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  TESTDATEN WERDEN ANGELEGT             │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.println();

        try {
            // Basisdaten anlegen (falls noch nicht vorhanden)
            erstelleBasisdaten();

            System.out.println();
            System.out.println("✓ Basisdaten erfolgreich angelegt!");
            return true;

        } catch (SQLException e) {
            System.err.println("✗ FEHLER: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void erstelleBasisdaten() throws SQLException {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO STUDIERENDE (studierenden_id, matrikelnummer, vorname, nachname, email) " +
                             "VALUES (1, 'TEST001', 'Test', 'User', 'test@test.de')")) {
            pstmt.executeUpdate();
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO STUDIENGANG (studiengang_id, bezeichnung, kuerzel, abschluss, aktiv) " +
                             "VALUES (1, 'Test-Studiengang', 'TST', 'Test', 1)")) {
            pstmt.executeUpdate();
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO SEMESTERZEIT (semesterzeit_id, beginn, ende, bezeichnung) " +
                             "VALUES (1, '2024-01-01', '2024-12-31', 'Test 2024')")) {
            pstmt.executeUpdate();
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO SEMESTER (semester_id, semesterzeit_id, bezeichnung, typ, jahr) " +
                             "VALUES (1, 1, 'Test WS', 'Test', 2024)")) {
            pstmt.executeUpdate();
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO PRUEFUNGSORDNUNG (po_id, studiengang_id, bezeichnung, gueltig_ab, sws_referent, sws_korreferent) " +
                             "VALUES (1, 1, 'Test-PO', '2024-01-01', 4, 2)")) {
            pstmt.executeUpdate();
        }

        System.out.println("  ✓ Basisdaten angelegt/überprüft");
    }

    private static int erstelleTestArbeit(String titel) throws SQLException {
        WissenschaftlicheArbeitDAO arbeitDao = new WissenschaftlicheArbeitDAO();

        WissenschaftlicheArbeit arbeit = new WissenschaftlicheArbeit();
        arbeit.setStudierendenId(1);
        arbeit.setStudiengangId(1);
        arbeit.setPruefungsordnungId(1);
        arbeit.setSemesterId(1);
        arbeit.setTitel(titel);
        arbeit.setTyp("Bachelor");
        arbeit.setStatus("Geplant");

        int id = arbeitDao.create(arbeit);
        testArbeitIds.add(id);
        System.out.println("  → Test-Arbeit angelegt: " + titel + " (ID: " + id + ")");
        return id;
    }

    private static void test1GueltigeZeitdaten(ZeitdatenDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 1: Gültige Zeitdaten");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = erstelleTestArbeit("Test 1: Gültige Zeitdaten");

            Zeitdaten zeit = new Zeitdaten();
            zeit.setArbeitId(arbeitId);
            zeit.setAnfangsdatum(LocalDate.now().minusMonths(3));
            zeit.setAbgabedatum(LocalDate.now().plusMonths(3));
            zeit.setKolloquiumsdatum(LocalDate.now().plusMonths(4));

            int id = dao.create(zeit);
            erstellteZeitdatenIds.add(id);
            System.out.println("✓ ERFOLG: Zeitdaten gespeichert (ID: " + id + ")");
            System.out.println("  Anfang: " + zeit.getAnfangsdatum());
            System.out.println("  Abgabe: " + zeit.getAbgabedatum());
            System.out.println("  Kolloquium: " + zeit.getKolloquiumsdatum());
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✗ FEHLER: Unerwarteter Validierungsfehler!");
            System.out.println(e.getMessage());
            System.out.println();
        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getClass().getSimpleName());
            System.out.println("  " + e.getMessage());
            System.out.println();
        }
    }

    private static void test2FalscheReihenfolge(ZeitdatenDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 2: Anfangsdatum nach Abgabedatum");
        System.out.println("───────────────────────────────────────");

        try {
            // Für Tests die fehlschlagen sollen, brauchen wir keine Arbeit
            // weil die Validierung schon vorher abbricht
            int arbeitId = 999; // Dummy-ID für Validierungstests

            Zeitdaten zeit = new Zeitdaten();
            zeit.setArbeitId(arbeitId);
            zeit.setAnfangsdatum(LocalDate.now().plusMonths(3)); // FEHLER: Nach Abgabe
            zeit.setAbgabedatum(LocalDate.now());
            zeit.setKolloquiumsdatum(LocalDate.now().plusMonths(1));

            dao.create(zeit);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Validierungsfehler korrekt erkannt:");
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test3BearbeitungszeitZuKurz(ZeitdatenDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 3: Bearbeitungszeit zu kurz");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = 999; // Dummy-ID für Validierungstests

            Zeitdaten zeit = new Zeitdaten();
            zeit.setArbeitId(arbeitId);
            zeit.setAnfangsdatum(LocalDate.now());
            zeit.setAbgabedatum(LocalDate.now().plusDays(3)); // FEHLER: Nur 3 Tage

            dao.create(zeit);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Validierungsfehler korrekt erkannt:");
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test4KolloquiumVorAbgabe(ZeitdatenDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 4: Kolloquium vor Abgabe");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = 999; // Dummy-ID für Validierungstests

            Zeitdaten zeit = new Zeitdaten();
            zeit.setArbeitId(arbeitId);
            zeit.setAnfangsdatum(LocalDate.now().minusMonths(3));
            zeit.setAbgabedatum(LocalDate.now().plusMonths(3));
            zeit.setKolloquiumsdatum(LocalDate.now()); // FEHLER: Vor Abgabe

            dao.create(zeit);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Validierungsfehler korrekt erkannt:");
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test5OhneKolloquium(ZeitdatenDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 5: Ohne Kolloquiumsdatum (optional)");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = erstelleTestArbeit("Test 5: Ohne Kolloquiumsdatum");

            Zeitdaten zeit = new Zeitdaten();
            zeit.setArbeitId(arbeitId);
            zeit.setAnfangsdatum(LocalDate.now().minusMonths(2));
            zeit.setAbgabedatum(LocalDate.now().plusMonths(4));
            // Kein Kolloquiumsdatum gesetzt - sollte OK sein

            int id = dao.create(zeit);
            erstellteZeitdatenIds.add(id);
            System.out.println("✓ ERFOLG: Zeitdaten ohne Kolloquium gespeichert (ID: " + id + ")");
            System.out.println("  Anfang: " + zeit.getAnfangsdatum());
            System.out.println("  Abgabe: " + zeit.getAbgabedatum());
            System.out.println("  Kolloquium: nicht gesetzt");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✗ FEHLER: Unerwarteter Validierungsfehler!");
            System.out.println(e.getMessage());
            System.out.println();
        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getClass().getSimpleName());
            System.out.println("  " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    private static void cleanDB() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  TESTDATEN WERDEN AUFGERÄUMT           │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.println();

        try (Connection conn = DatabaseConnection.connect()) {

            // Zeitdaten löschen
            if (!erstellteZeitdatenIds.isEmpty()) {
                for (Integer id : erstellteZeitdatenIds) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM ZEITDATEN WHERE zeit_id = ?")) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                        System.out.println("  ✓ Zeitdaten gelöscht (ID: " + id + ")");
                    }
                }
            }

            // Test-Arbeiten löschen
            if (!testArbeitIds.isEmpty()) {
                for (Integer id : testArbeitIds) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM WISSENSCHAFTLICHE_ARBEIT WHERE arbeit_id = ?")) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                        System.out.println("  ✓ Test-Arbeit gelöscht (ID: " + id + ")");
                    }
                }
            }

            System.out.println();
            System.out.println("✓ Cleanup abgeschlossen!");
            System.out.println("  (Basisdaten bleiben für zukünftige Tests)");

        } catch (SQLException e) {
            System.err.println("  ✗ Fehler beim Aufräumen: " + e.getMessage());
        }
    }
}