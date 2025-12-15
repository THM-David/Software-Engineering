package de.thm.se;

import de.thm.se.backend.DataAcessLayer.StudierendeDAO;
import de.thm.se.backend.model.Studierende;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Testklasse für die Validierung von Studierende-Objekten
 * Funktioniert korrekt mit SQLite (setzt Tabelle STUDIERENDE voraus)
 */
public class TestValidationStudierende {

    private static int erstellterStudierendenId = 0;
    private static final String TEST_MATRIKELNUMMER = "TEST9999";

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║    TEST VALIDIERUNG STUDIERENDE        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        try {
            StudierendeDAO dao = new StudierendeDAO();

            System.out.println("═══════════════════════════════════════");
            System.out.println("   VALIDIERUNGSTESTS STARTEN");
            System.out.println("═══════════════════════════════════════");
            System.out.println();

            // Tests durchführen
            test1GueltigeDatenCreate(dao);
            test2LeerePflichtfelder(dao);
            test3UngueltigeEmail(dao);
            test4UngueltigesGeburtsdatum(dao);
            test5GueltigeDatenUpdate(dao);
            test6FehlendeIdUpdate(dao);
            test7MehrereFehler(dao);


            System.out.println();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║    ALLE TESTS ABGESCHLOSSEN            ║");
            System.out.println("╚════════════════════════════════════════╝");

        } finally {
            // Aufräumen
            System.out.println();
            cleanDB();
        }
    }

    private static void test1GueltigeDatenCreate(StudierendeDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 1: Gültige Daten (CREATE)");
        System.out.println("───────────────────────────────────────");

        try {
            Studierende studi = new Studierende();
            studi.setMatrikelnummer(TEST_MATRIKELNUMMER);
            studi.setVorname("Valid");
            studi.setNachname("Tester");
            studi.setEmail("valid.tester@example.com");
            studi.setGeburtsdatum(LocalDate.of(1995, 10, 20));
            studi.setAdresse("Teststr. 1, 12345 Teststadt");

            erstellterStudierendenId = dao.create(studi);

            // Verifizieren
            Optional<Studierende> savedStudi = dao.findById(erstellterStudierendenId);
            if (savedStudi.isPresent() && savedStudi.get().getMatrikelnummer().equals(TEST_MATRIKELNUMMER)) {
                System.out.println("✓ ERFOLG: Studierender gespeichert (ID: " + erstellterStudierendenId + ")");
            } else {
                System.out.println("✗ FEHLER: Speichern erfolgreich, aber Daten nicht gefunden/korrekt.");
            }
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✗ FEHLER: Gültige Daten wurden abgelehnt: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: Unerwartete Exception: " + e.getMessage());
        }
    }

    private static void test2LeerePflichtfelder(StudierendeDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 2: Leere Pflichtfelder");
        System.out.println("───────────────────────────────────────");

        try {
            Studierende studi = new Studierende();
            studi.setMatrikelnummer(""); // Fehler
            studi.setVorname(null);      // Fehler
            studi.setNachname("Test");
            studi.setEmail("test@test.de");
            studi.setGeburtsdatum(LocalDate.of(2000, 1, 1));
            studi.setAdresse("Adresse");

            dao.create(studi);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Korrekt abgelehnt");
            System.out.println("  Fehlermeldungen:");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: Unerwartete Exception: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test3UngueltigeEmail(StudierendeDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 3: Ungültige E-Mail");
        System.out.println("───────────────────────────────────────");

        try {
            Studierende studi = new Studierende();
            studi.setMatrikelnummer("12345");
            studi.setVorname("Hans");
            studi.setNachname("Muster");
            studi.setEmail("hans.muster_at_uni.de"); // Fehler
            studi.setGeburtsdatum(LocalDate.of(2000, 1, 1));
            studi.setAdresse("Adresse");

            dao.create(studi);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Korrekt abgelehnt");
            System.out.println("  Fehlermeldung: " + e.getMessage().trim());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: Unerwartete Exception: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test4UngueltigesGeburtsdatum(StudierendeDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 4: Ungültiges Geburtsdatum (Zukunft)");
        System.out.println("───────────────────────────────────────");

        try {
            Studierende studi = new Studierende();
            studi.setMatrikelnummer("12345");
            studi.setVorname("Future");
            studi.setNachname("Kid");
            studi.setEmail("future@example.com");
            studi.setGeburtsdatum(LocalDate.now().plusDays(1)); // Fehler: Zukunft
            studi.setAdresse("Adresse");

            dao.create(studi);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Korrekt abgelehnt");
            System.out.println("  Fehlermeldung: " + e.getMessage().trim());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: Unerwartete Exception: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test5GueltigeDatenUpdate(StudierendeDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 5: Gültige Daten (UPDATE)");
        System.out.println("───────────────────────────────────────");

        if (erstellterStudierendenId == 0) {
            System.out.println("  Übersprungen: Test 1 ist fehlgeschlagen oder wurde nicht ausgeführt.");
            return;
        }

        try {
            // Bestehenden Studierenden laden und aktualisieren
            Optional<Studierende> optStudi = dao.findById(erstellterStudierendenId);
            if (optStudi.isEmpty()) {
                System.out.println("✗ FEHLER: Studierender zum Update nicht gefunden.");
                return;
            }

            Studierende studi = optStudi.get();
            studi.setNachname("Geändert");
            studi.setEmail("neu.test@example.com");

            boolean success = dao.update(studi);

            if (success) {
                // Verifizieren
                Optional<Studierende> updatedStudi = dao.findById(erstellterStudierendenId);
                if (updatedStudi.isPresent() && updatedStudi.get().getNachname().equals("Geändert")) {
                    System.out.println("✓ ERFOLG: Studierender erfolgreich aktualisiert.");
                } else {
                    System.out.println("✗ FEHLER: Update erfolgreich, aber Daten nicht korrekt gespeichert.");
                }
            } else {
                System.out.println("✗ FEHLER: Update fehlgeschlagen (DAO meldete keinen Erfolg).");
            }
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✗ FEHLER: Gültige Update-Daten wurden abgelehnt: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: Unerwartete Exception: " + e.getMessage());
        }
    }

    private static void test6FehlendeIdUpdate(StudierendeDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 6: Fehlende ID (UPDATE)");
        System.out.println("───────────────────────────────────────");

        try {
            Studierende studi = new Studierende();
            studi.setStudierendenId(0); // Fehler
            studi.setMatrikelnummer("12345");
            studi.setVorname("Update");
            studi.setNachname("Fehler");
            studi.setEmail("update@fehler.de");
            studi.setGeburtsdatum(LocalDate.of(2000, 1, 1));
            studi.setAdresse("Adresse");

            dao.update(studi);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Korrekt abgelehnt");
            System.out.println("  Fehlermeldung: " + e.getMessage().trim());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: Unerwartete Exception: " + e.getMessage());
            System.out.println();
        }
    }

    private static void test7MehrereFehler(StudierendeDAO dao) {
        System.out.println("───────────────────────────────────────");
        System.out.println("TEST 7: Mehrere Fehler (CREATE)");
        System.out.println("───────────────────────────────────────");

        try {
            Studierende studi = new Studierende();
            studi.setMatrikelnummer(null);
            studi.setVorname("A"); // Zu kurz
            studi.setNachname(""); // Leer
            studi.setEmail("falsch.de"); // Ungültiges Format
            studi.setGeburtsdatum(LocalDate.of(2025, 1, 1)); // Zukunft
            studi.setAdresse("Adresse");

            dao.create(studi);
            System.out.println("✗ FEHLER: Wurde nicht abgelehnt!");
            System.out.println();

        } catch (IllegalArgumentException e) {
            System.out.println("✓ ERFOLG: Alle Fehler erkannt");
            System.out.println("  Fehlermeldungen:");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ FEHLER: Unerwartete Exception: " + e.getMessage());
            System.out.println();
        }
    }

    private static void cleanDB() {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│  TESTDATEN WERDEN GELÖSCHT             │");
        System.out.println("└────────────────────────────────────────┘");

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Löscht den in Test 1 erstellten Datensatz
            int deletedRows = stmt.executeUpdate("DELETE FROM STUDIERENDE WHERE matrikelnummer = '" + TEST_MATRIKELNUMMER + "'");

            if (deletedRows > 0) {
                System.out.println("  ✓ Test-Studierender (" + TEST_MATRIKELNUMMER + ") gelöscht.");
            } else if (erstellterStudierendenId > 0) {
                System.out.println("  ✓ Test-Studierender (ID: " + erstellterStudierendenId + ") gelöscht.");
            } else {
                System.out.println("  ~ Kein Studierender zum Löschen gefunden.");
            }

            System.out.println();
            System.out.println("✓ Datenbank ist wieder im Ursprungszustand.");

        } catch (SQLException e) {
            System.err.println("  ✗ Fehler beim Löschen: " + e.getMessage());
        }
    }
}