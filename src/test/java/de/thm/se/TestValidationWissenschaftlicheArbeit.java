package de.thm.se;

import de.thm.se.backend.DataAcessLayer.WissenschaftlicheArbeitDAO;
import de.thm.se.backend.model.WissenschaftlicheArbeit;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * Test der ALLE Testdaten am Ende wieder löscht
 * Funktioniert korrekt mit SQLite
 */
public class TestValidationWissenschaftlicheArbeit {

    private static List<Integer> erstellteArbeitIds = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║    TEST MIT KOMPLETT-CLEANUP           ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        try {
            // Phase 1: Setup
            if (!setupTestdaten()) {
                System.err.println("Setup fehlgeschlagen.");
                return;
            }

            System.out.println();
            System.out.println("═══════════════════════════════════════");
            System.out.println("   VALIDIERUNGSTESTS STARTEN");
            System.out.println("═══════════════════════════════════════");
            System.out.println();

            // Phase 2: Tests
            WissenschaftlicheArbeitDAO dao = new WissenschaftlicheArbeitDAO();

            test1GueltigeDaten(dao);
            test2LeereTitel(dao);
            test3UngueltigerTyp(dao);
            test4MehrereFehler(dao);

            System.out.println();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║    ALLE TESTS ABGESCHLOSSEN            ║");
            System.out.println("╚════════════════════════════════════════╝");

        } finally {
            // Phase 3: Komplett aufräumen
            System.out.println();
            cleanDB();
        }
    }

    private static boolean setupTestdaten() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  TESTDATEN WERDEN ANGELEGT             │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.println();

        try (Connection conn = DatabaseConnection.connect()) {

            // 1. Studierenden anlegen
            String sqlStudent = """
                INSERT INTO STUDIERENDE 
                (matrikelnummer, vorname, nachname, email)
                VALUES ('TEST999', 'Test', 'User', 'test@test.de')
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlStudent)) {
                pstmt.executeUpdate();
                System.out.println("  ✓ Test-Studierender angelegt");
            }

            // 2. Studiengang anlegen
            String sqlStudiengang = """
                INSERT INTO STUDIENGANG 
                (bezeichnung, kuerzel, abschluss, aktiv)
                VALUES ('Test-Studiengang', 'TST', 'Test', 1)
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlStudiengang)) {
                pstmt.executeUpdate();
                System.out.println("  ✓ Test-Studiengang angelegt");
            }

            // 3. Semesterzeit anlegen
            String sqlSemesterzeit = """
                INSERT INTO SEMESTERZEIT 
                (beginn, ende, bezeichnung)
                VALUES ('2024-01-01', '2024-12-31', 'Test-Semester')
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlSemesterzeit)) {
                pstmt.executeUpdate();
                System.out.println("  ✓ Test-Semesterzeit angelegt");
            }

            // 4. Semester anlegen (referenziert Semesterzeit)
            String sqlSemester = """
                INSERT INTO SEMESTER 
                (semesterzeit_id, bezeichnung, typ, jahr)
                VALUES (
                    (SELECT semesterzeit_id FROM SEMESTERZEIT WHERE bezeichnung = 'Test-Semester'),
                    'Test 2024', 'Test', 2024
                )
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlSemester)) {
                pstmt.executeUpdate();
                System.out.println("  ✓ Test-Semester angelegt");
            }

            // 5. Prüfungsordnung anlegen (referenziert Studiengang)
            String sqlPO = """
                INSERT INTO PRUEFUNGSORDNUNG 
                (studiengang_id, bezeichnung, gueltig_ab, sws_referent, sws_korreferent)
                VALUES (
                    (SELECT studiengang_id FROM STUDIENGANG WHERE bezeichnung = 'Test-Studiengang'),
                    'Test-PO', '2024-01-01', 4, 2
                )
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPO)) {
                pstmt.executeUpdate();
                System.out.println("  ✓ Test-Prüfungsordnung angelegt");
            }

            System.out.println();
            System.out.println("✓ Alle Testdaten erfolgreich angelegt!");
            return true;

        } catch (SQLException e) {
            System.err.println("✗ FEHLER: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void test1GueltigeDaten(WissenschaftlicheArbeitDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 1: Gültige Daten speichern");
        System.out.println("───────────────────────────────────────");

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // IDs aus der Datenbank holen
            var rs = stmt.executeQuery("SELECT studierenden_id FROM STUDIERENDE WHERE matrikelnummer = 'TEST999'");
            int studierendenId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT studiengang_id FROM STUDIENGANG WHERE bezeichnung = 'Test-Studiengang'");
            int studiengangId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT po_id FROM PRUEFUNGSORDNUNG WHERE bezeichnung = 'Test-PO'");
            int poId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT semester_id FROM SEMESTER WHERE bezeichnung = 'Test 2024'");
            int semesterId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            WissenschaftlicheArbeit arbeit = new WissenschaftlicheArbeit();
            arbeit.setStudierendenId(studierendenId);
            arbeit.setStudiengangId(studiengangId);
            arbeit.setPruefungsordnungId(poId);
            arbeit.setSemesterId(semesterId);
            arbeit.setTitel("Test-Arbeit: Microservice-Architektur");
            arbeit.setTyp("Bachelor");
            arbeit.setStatus("Geplant");

            int id = dao.create(arbeit);
            erstellteArbeitIds.add(id);
            System.out.println("✓ ERFOLG: Arbeit gespeichert (ID: " + id + ")");
            System.out.println();

        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test2LeereTitel(WissenschaftlicheArbeitDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 2: Leerer Titel");
        System.out.println("───────────────────────────────────────");

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SELECT studierenden_id FROM STUDIERENDE WHERE matrikelnummer = 'TEST999'");
            int studierendenId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT studiengang_id FROM STUDIENGANG WHERE bezeichnung = 'Test-Studiengang'");
            int studiengangId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT po_id FROM PRUEFUNGSORDNUNG WHERE bezeichnung = 'Test-PO'");
            int poId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT semester_id FROM SEMESTER WHERE bezeichnung = 'Test 2024'");
            int semesterId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            WissenschaftlicheArbeit arbeit = new WissenschaftlicheArbeit();
            arbeit.setStudierendenId(studierendenId);
            arbeit.setStudiengangId(studiengangId);
            arbeit.setPruefungsordnungId(poId);
            arbeit.setSemesterId(semesterId);
            arbeit.setTitel("");
            arbeit.setTyp("Bachelor");
            arbeit.setStatus("Geplant");

            dao.create(arbeit);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Korrekt abgelehnt");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test3UngueltigerTyp(WissenschaftlicheArbeitDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 3: Ungültiger Typ");
        System.out.println("───────────────────────────────────────");

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SELECT studierenden_id FROM STUDIERENDE WHERE matrikelnummer = 'TEST999'");
            int studierendenId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT studiengang_id FROM STUDIENGANG WHERE bezeichnung = 'Test-Studiengang'");
            int studiengangId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT po_id FROM PRUEFUNGSORDNUNG WHERE bezeichnung = 'Test-PO'");
            int poId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            rs = stmt.executeQuery("SELECT semester_id FROM SEMESTER WHERE bezeichnung = 'Test 2024'");
            int semesterId = rs.next() ? rs.getInt(1) : 0;
            rs.close();

            WissenschaftlicheArbeit arbeit = new WissenschaftlicheArbeit();
            arbeit.setStudierendenId(studierendenId);
            arbeit.setStudiengangId(studiengangId);
            arbeit.setPruefungsordnungId(poId);
            arbeit.setSemesterId(semesterId);
            arbeit.setTitel("Test-Arbeit mit ungültigem Typ");
            arbeit.setTyp("Diplomarbeit");
            arbeit.setStatus("Geplant");

            dao.create(arbeit);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Korrekt abgelehnt");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test4MehrereFehler(WissenschaftlicheArbeitDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 4: Mehrere Fehler");
        System.out.println("───────────────────────────────────────");

        try {
            WissenschaftlicheArbeit arbeit = new WissenschaftlicheArbeit();
            arbeit.setStudierendenId(0);
            arbeit.setStudiengangId(-5);
            arbeit.setPruefungsordnungId(1);
            arbeit.setSemesterId(1);
            arbeit.setTitel("XYZ");
            arbeit.setTyp("Hausarbeit");
            arbeit.setStatus("Fertig");

            dao.create(arbeit);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Alle Fehler erkannt");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: " + e.getMessage());
            System.out.println();
        }
    }

    private static void cleanDB() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  ALLE TESTDATEN WERDEN GELÖSCHT        │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.println();

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // WICHTIG: Foreign Keys temporär deaktivieren für Cleanup
            stmt.execute("PRAGMA foreign_keys = OFF");

            /*// 1. Arbeit löschen
            if (!erstellteArbeitIds.isEmpty()) {
                for (Integer id : erstellteArbeitIds) {
                    stmt.executeUpdate("DELETE FROM WISSENSCHAFTLICHE_ARBEIT WHERE arbeit_id = " + id);
                    System.out.println("  ✓ Arbeit gelöscht (ID: " + id + ")");
                }
            }*/

            // 1. Arbeit löschen
            stmt.executeUpdate("DELETE FROM WISSENSCHAFTLICHE_ARBEIT WHERE titel = 'Test-Arbeit: Microservice-Architektur'");
            System.out.println("  ✓ Arbeit gelöscht");

            // 2. Prüfungsordnung löschen
            stmt.executeUpdate("DELETE FROM PRUEFUNGSORDNUNG WHERE bezeichnung = 'Test-PO'");
            System.out.println("  ✓ Test-Prüfungsordnung gelöscht");

            // 3. Semester löschen
            stmt.executeUpdate("DELETE FROM SEMESTER WHERE bezeichnung = 'Test 2024'");
            System.out.println("  ✓ Test-Semester gelöscht");

            // 4. Semesterzeit löschen
            stmt.executeUpdate("DELETE FROM SEMESTERZEIT WHERE bezeichnung = 'Test-Semester'");
            System.out.println("  ✓ Test-Semesterzeit gelöscht");

            // 5. Studiengang löschen
            stmt.executeUpdate("DELETE FROM STUDIENGANG WHERE bezeichnung = 'Test-Studiengang'");
            System.out.println("  ✓ Test-Studiengang gelöscht");

            // 6. Studierender löschen
            stmt.executeUpdate("DELETE FROM STUDIERENDE WHERE matrikelnummer = 'TEST999'");
            System.out.println("  ✓ Test-Studierender gelöscht");

            // Foreign Keys wieder aktivieren
            stmt.execute("PRAGMA foreign_keys = ON");

            System.out.println();
            System.out.println("✓ Alle Testdaten wurden gelöscht!");
            System.out.println("  Datenbank ist wieder im Ursprungszustand.");

        } catch (SQLException e) {
            System.err.println("  ✗ Fehler beim Löschen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}