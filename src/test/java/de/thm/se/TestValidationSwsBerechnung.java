package de.thm.se;

import de.thm.se.backend.DataAcessLayer.SwsBerechnungDAO;
import de.thm.se.backend.DataAcessLayer.WissenschaftlicheArbeitDAO;
import de.thm.se.backend.model.SwsBerechnung;
import de.thm.se.backend.model.WissenschaftlicheArbeit;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test-Klasse für SwsBerechnung-Validierung
 */
public class TestValidationSwsBerechnung {

    private static List<Integer> testArbeitIds = new ArrayList<>();
    private static List<Integer> testBetreuerIds = new ArrayList<>();
    private static List<Integer> erstellteSwsIds = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║    SWS-BERECHNUNG VALIDIERUNG TESTEN   ║");
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

            SwsBerechnungDAO dao = new SwsBerechnungDAO();

            // Test 1: Gültige SWS-Berechnung
            test1GueltigeSws(dao);

            // Test 2: Negativer SWS-Wert (Fehler)
            test2NegativerSwsWert(dao);

            // Test 3: Unrealistisch hoher SWS-Wert (Fehler)
            test3UnrealistischHoherWert(dao);

            // Test 4: Ungültige Rolle (Fehler)
            test4UngueltigeRolle(dao);

            // Test 5: Datum in der Zukunft (Fehler)
            test5DatumInZukunft(dao);

            // Test 6: Sehr kleiner SWS-Wert (Warnung)
            test6SehrKleinerWert(dao);

            System.out.println();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║    ALLE TESTS ABGESCHLOSSEN            ║");
            System.out.println("╚════════════════════════════════════════╝");

        } finally {
            // Cleanup: Testdaten aufräumen
            System.out.println();
            aufraumen();
        }
    }

    private static boolean setupTestdaten() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  TESTDATEN WERDEN ANGELEGT             │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.println();

        try {
            // Basisdaten anlegen
            erstelleBasisdaten();

            // Betreuer anlegen
            erstelleBetreuer();

            System.out.println();
            System.out.println("✓ Testdaten erfolgreich angelegt!");
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

    private static void erstelleBetreuer() throws SQLException {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO BETREUER (betreuer_id, vorname, nachname, titel, email, rolle) " +
                             "VALUES (1, 'Prof. Dr.', 'Mustermann', 'Professor', 'prof@test.de', 'Referent')")) {
            pstmt.executeUpdate();
            testBetreuerIds.add(1);
            System.out.println("  ✓ Test-Betreuer angelegt (ID: 1)");
        }
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

    private static void test1GueltigeSws(SwsBerechnungDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 1: Gültige SWS-Berechnung");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = erstelleTestArbeit("Test 1: Gültige SWS-Berechnung");

            SwsBerechnung sws = new SwsBerechnung();
            sws.setArbeitId(arbeitId);
            sws.setBetreuerId(1);
            sws.setSemesterId(1);
            sws.setPruefungsordnungId(1);
            sws.setSwsWert(1.5f);
            sws.setRolle("Referent");
            sws.setBerechnetAm(LocalDate.now());

            int id = dao.create(sws);
            erstellteSwsIds.add(id);
            System.out.println("✓ ERFOLG: SWS-Berechnung gespeichert (ID: " + id + ")");
            System.out.println("  SWS-Wert: " + sws.getSwsWert());
            System.out.println("  Rolle: " + sws.getRolle());
            System.out.println("  Berechnet am: " + sws.getBerechnetAm());
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

    private static void test2NegativerSwsWert(SwsBerechnungDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 2: Negativer SWS-Wert");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = 999; // Dummy-ID für Validierungstests

            SwsBerechnung sws = new SwsBerechnung();
            sws.setArbeitId(arbeitId);
            sws.setBetreuerId(1);
            sws.setSemesterId(1);
            sws.setPruefungsordnungId(1);
            sws.setSwsWert(-2.0f); // FEHLER: Negativ
            sws.setRolle("Referent");
            sws.setBerechnetAm(LocalDate.now());

            dao.create(sws);
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

    private static void test3UnrealistischHoherWert(SwsBerechnungDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 3: Unrealistisch hoher SWS-Wert");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = 999; // Dummy-ID

            SwsBerechnung sws = new SwsBerechnung();
            sws.setArbeitId(arbeitId);
            sws.setBetreuerId(1);
            sws.setSemesterId(1);
            sws.setPruefungsordnungId(1);
            sws.setSwsWert(5.0f); // FEHLER: Zu hoch
            sws.setRolle("Referent");
            sws.setBerechnetAm(LocalDate.now());

            dao.create(sws);
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

    private static void test4UngueltigeRolle(SwsBerechnungDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 4: Ungültige Rolle");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = 999; // Dummy-ID

            SwsBerechnung sws = new SwsBerechnung();
            sws.setArbeitId(arbeitId);
            sws.setBetreuerId(1);
            sws.setSemesterId(1);
            sws.setPruefungsordnungId(1);
            sws.setSwsWert(1.0f);
            sws.setRolle("Professor"); // FEHLER: Ungültige Rolle
            sws.setBerechnetAm(LocalDate.now());

            dao.create(sws);
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

    private static void test5DatumInZukunft(SwsBerechnungDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 5: Berechnungsdatum in der Zukunft");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = 999; // Dummy-ID

            SwsBerechnung sws = new SwsBerechnung();
            sws.setArbeitId(arbeitId);
            sws.setBetreuerId(1);
            sws.setSemesterId(1);
            sws.setPruefungsordnungId(1);
            sws.setSwsWert(0.5f);
            sws.setRolle("Referent");
            sws.setBerechnetAm(LocalDate.now().plusDays(10)); // FEHLER: Zukunft

            dao.create(sws);
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

    private static void test6SehrKleinerWert(SwsBerechnungDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 6: Sehr kleiner SWS-Wert (Warnung)");
        System.out.println("───────────────────────────────────────");

        try {
            int arbeitId = 999; // Dummy-ID

            SwsBerechnung sws = new SwsBerechnung();
            sws.setArbeitId(arbeitId);
            sws.setBetreuerId(1);
            sws.setSemesterId(1);
            sws.setPruefungsordnungId(1);
            sws.setSwsWert(0.2f); // Warnung: Sehr klein
            sws.setRolle("Referent");
            sws.setBerechnetAm(LocalDate.now());

            dao.create(sws);
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

    private static void aufraumen() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  TESTDATEN WERDEN AUFGERÄUMT           │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.println();

        try (Connection conn = DatabaseConnection.connect()) {

            // SWS-Berechnungen löschen
            if (!erstellteSwsIds.isEmpty()) {
                for (Integer id : erstellteSwsIds) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM SWS_BERECHNUNG WHERE sws_id = ?")) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                        System.out.println("  ✓ SWS-Berechnung gelöscht (ID: " + id + ")");
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