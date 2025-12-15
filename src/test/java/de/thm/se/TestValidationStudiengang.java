package de.thm.se;

import de.thm.se.backend.DataAcessLayer.StudiengangDAO;
import de.thm.se.backend.model.Studiengang;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test-Klasse für Studiengang-Validierung
 */
public class TestValidationStudiengang {
    private static List<Integer> erstellteStudiengangIds = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║    STUDIENGANG-VALIDIERUNG TESTEN      ║");
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

            StudiengangDAO dao = new StudiengangDAO();

            // Test 1: Gültiger Studiengang
            test1GueltigerStudiengang(dao);

            // Test 2: Leere Bezeichnung (Fehler)
            test2LeereBezeichnung(dao);

            // Test 3: Zu kurzes Kürzel (Fehler)
            test3ZuKurzesKuerzel(dao);

            // Test 4: Ungültiger Abschluss (Fehler)
            test4UngueltigerAbschluss(dao);

            // Test 5: Bezeichnung mit Kleinbuchstabe (Fehler)
            test5BezeichnungKleinbuchstabe(dao);

            // Test 6: Kürzel mit Sonderzeichen (Fehler)
            test6KuerzelMitSonderzeichen(dao);

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
            // Fachbereich anlegen (falls noch nicht vorhanden)
            erstelleFachbereich();

            System.out.println();
            System.out.println("✓ Testdaten erfolgreich angelegt!");
            return true;

        } catch (SQLException e) {
            System.err.println("✗ FEHLER: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void erstelleFachbereich() throws SQLException {
        // Prüfen ob FACHBEREICH-Tabelle existiert, falls nicht - überspringen
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS FACHBEREICH (" +
                             "fachbereich_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "bezeichnung TEXT NOT NULL)")) {
            pstmt.executeUpdate();
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO FACHBEREICH (fachbereich_id, bezeichnung) " +
                             "VALUES (1, 'Test-Fachbereich')")) {
            pstmt.executeUpdate();
            System.out.println("  ✓ Test-Fachbereich angelegt/überprüft (ID: 1)");
        }
    }

    private static void test1GueltigerStudiengang(StudiengangDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 1: Gültiger Studiengang");
        System.out.println("───────────────────────────────────────");

        try {
            Studiengang studiengang = new Studiengang();
            studiengang.setFachbereichId(1);
            studiengang.setBezeichnung("Informatik");
            studiengang.setKuerzel("INF");
            studiengang.setAbschlusstitel("Bachelor of Science Informatik");
            studiengang.setAbschluss("B.Sc.");
            studiengang.setAktiv(true);

            int id = dao.create(studiengang);
            erstellteStudiengangIds.add(id);
            System.out.println("✓ ERFOLG: Studiengang gespeichert (ID: " + id + ")");
            System.out.println("  Bezeichnung: " + studiengang.getBezeichnung());
            System.out.println("  Kürzel: " + studiengang.getKuerzel());
            System.out.println("  Abschluss: " + studiengang.getAbschluss());
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

    private static void test2LeereBezeichnung(StudiengangDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 2: Leere Bezeichnung");
        System.out.println("───────────────────────────────────────");

        try {
            Studiengang studiengang = new Studiengang();
            studiengang.setFachbereichId(1);
            studiengang.setBezeichnung(""); // FEHLER: Leer
            studiengang.setKuerzel("TST");
            studiengang.setAbschlusstitel("Test Abschluss");
            studiengang.setAbschluss("B.Sc.");
            studiengang.setAktiv(true);

            dao.create(studiengang);
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

    private static void test3ZuKurzesKuerzel(StudiengangDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 3: Zu kurzes Kürzel");
        System.out.println("───────────────────────────────────────");

        try {
            Studiengang studiengang = new Studiengang();
            studiengang.setFachbereichId(1);
            studiengang.setBezeichnung("Testfach");
            studiengang.setKuerzel("T"); // FEHLER: Nur 1 Zeichen
            studiengang.setAbschlusstitel("Test Abschluss");
            studiengang.setAbschluss("B.Sc.");
            studiengang.setAktiv(true);

            dao.create(studiengang);
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

    private static void test4UngueltigerAbschluss(StudiengangDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 4: Ungültiger Abschluss");
        System.out.println("───────────────────────────────────────");

        try {
            Studiengang studiengang = new Studiengang();
            studiengang.setFachbereichId(1);
            studiengang.setBezeichnung("Testfach");
            studiengang.setKuerzel("TST");
            studiengang.setAbschlusstitel("Test Abschluss");
            studiengang.setAbschluss("Bachelor"); // FEHLER: Ungültiger Abschluss
            studiengang.setAktiv(true);

            dao.create(studiengang);
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

    private static void test5BezeichnungKleinbuchstabe(StudiengangDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 5: Bezeichnung mit Kleinbuchstabe");
        System.out.println("───────────────────────────────────────");

        try {
            Studiengang studiengang = new Studiengang();
            studiengang.setFachbereichId(1);
            studiengang.setBezeichnung("informatik"); // FEHLER: Kleinbuchstabe
            studiengang.setKuerzel("INF");
            studiengang.setAbschlusstitel("Bachelor of Science");
            studiengang.setAbschluss("B.Sc.");
            studiengang.setAktiv(true);

            dao.create(studiengang);
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

    private static void test6KuerzelMitSonderzeichen(StudiengangDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 6: Kürzel mit Sonderzeichen");
        System.out.println("───────────────────────────────────────");

        try {
            Studiengang studiengang = new Studiengang();
            studiengang.setFachbereichId(1);
            studiengang.setBezeichnung("Testfach");
            studiengang.setKuerzel("T$T"); // FEHLER: Sonderzeichen
            studiengang.setAbschlusstitel("Bachelor of Science");
            studiengang.setAbschluss("B.Sc.");
            studiengang.setAktiv(true);

            dao.create(studiengang);
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

            // Studiengänge löschen
            if (!erstellteStudiengangIds.isEmpty()) {
                for (Integer id : erstellteStudiengangIds) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM STUDIENGANG WHERE studiengang_id = ?")) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                        System.out.println("  ✓ Studiengang gelöscht (ID: " + id + ")");
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