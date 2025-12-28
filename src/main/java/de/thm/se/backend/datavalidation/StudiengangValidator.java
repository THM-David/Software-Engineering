package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Studiengang;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
/**
 * Validator für Studiengang-Objekte.
 * Prüft Vollständigkeit und Gültigkeit der Daten.
 */
public class StudiengangValidator {

    // Erlaubte Abschlüsse
    private static final List<String> ERLAUBTE_ABSCHLUESSE = Arrays.asList(
            "B.Sc.", "B.A.", "B.Eng.", "M.Sc.", "M.A.", "M.Eng."
    );

    // Validierungsregeln
    private static final int MIN_BEZEICHNUNG_LAENGE = 3;
    private static final int MAX_BEZEICHNUNG_LAENGE = 200;
    private static final int MIN_KUERZEL_LAENGE = 2;
    private static final int MAX_KUERZEL_LAENGE = 10;
    private static final int MIN_ABSCHLUSSTITEL_LAENGE = 5;
    private static final int MAX_ABSCHLUSSTITEL_LAENGE = 300;

    /**
     * Validiert ein Studiengang-Objekt vollständig
     * @param studiengang Das zu validierende Objekt
     * @return ValidationResult mit allen gefundenen Fehlern
     */
    public ValidationResult validate(Studiengang studiengang) {
        ValidationResult result = new ValidationResult();

        if (studiengang == null) {
            result.addError("Studiengang darf nicht null sein");
            return result;
        }

        // Pflichtfelder prüfen
        validateFachbereichId(studiengang.getFachbereichId(), result);
        validateBezeichnung(studiengang.getBezeichnung(), result);
        validateKuerzel(studiengang.getKuerzel(), result);
        validateAbschlusstitel(studiengang.getAbschlusstitel(), result);
        validateAbschluss(studiengang.getAbschluss(), result);
        // aktiv ist boolean - keine Validierung nötig

        return result;
    }

    /**
     * Validiert die Fachbereich-ID
     */
    private void validateFachbereichId(int fachbereichId, ValidationResult result) {
        if (fachbereichId <= 0) {
            result.addError("Fachbereich-ID muss größer als 0 sein");
        }
    }

    /**
     * Validiert die Bezeichnung des Studiengangs
     */
    private void validateBezeichnung(String bezeichnung, ValidationResult result) {
        if (bezeichnung == null || bezeichnung.trim().isEmpty()) {
            result.addError("Bezeichnung darf nicht leer sein");
            return;
        }

        String trimmedBezeichnung = bezeichnung.trim();

        if (trimmedBezeichnung.length() < MIN_BEZEICHNUNG_LAENGE) {
            result.addError("Bezeichnung muss mindestens " + MIN_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
        }

        if (trimmedBezeichnung.length() > MAX_BEZEICHNUNG_LAENGE) {
            result.addError("Bezeichnung darf maximal " + MAX_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
        }

        // Bezeichnung sollte mit Großbuchstaben beginnen
        if (!Character.isUpperCase(trimmedBezeichnung.charAt(0))) {
            result.addError("Bezeichnung sollte mit einem Großbuchstaben beginnen");
        }
    }

    /**
     * Validiert das Kürzel des Studiengangs
     */
    private void validateKuerzel(String kuerzel, ValidationResult result) {
        if (kuerzel == null || kuerzel.trim().isEmpty()) {
            result.addError("Kürzel darf nicht leer sein");
            return;
        }

        String trimmedKuerzel = kuerzel.trim();

        if (trimmedKuerzel.length() < MIN_KUERZEL_LAENGE) {
            result.addError("Kürzel muss mindestens " + MIN_KUERZEL_LAENGE + " Zeichen lang sein");
        }

        if (trimmedKuerzel.length() > MAX_KUERZEL_LAENGE) {
            result.addError("Kürzel darf maximal " + MAX_KUERZEL_LAENGE + " Zeichen lang sein");
        }

        // Kürzel sollte nur aus Buchstaben, Zahlen und Bindestrichen bestehen
        if (!trimmedKuerzel.matches("^[A-Za-z0-9-]+$")) {
            result.addError("Kürzel darf nur Buchstaben, Zahlen und Bindestriche enthalten");
        }
    }

    /**
     * Validiert den Abschlusstitel
     */
    private void validateAbschlusstitel(String abschlusstitel, ValidationResult result) {
        if (abschlusstitel == null || abschlusstitel.trim().isEmpty()) {
            result.addError("Abschlusstitel darf nicht leer sein");
            return;
        }

        String trimmedTitel = abschlusstitel.trim();

        if (trimmedTitel.length() < MIN_ABSCHLUSSTITEL_LAENGE) {
            result.addError("Abschlusstitel muss mindestens " + MIN_ABSCHLUSSTITEL_LAENGE + " Zeichen lang sein");
        }

        if (trimmedTitel.length() > MAX_ABSCHLUSSTITEL_LAENGE) {
            result.addError("Abschlusstitel darf maximal " + MAX_ABSCHLUSSTITEL_LAENGE + " Zeichen lang sein");
        }
    }

    /**
     * Validiert den Abschluss (akademischer Grad)
     */
    private void validateAbschluss(String abschluss, ValidationResult result) {
        if (abschluss == null || abschluss.trim().isEmpty()) {
            result.addError("Abschluss darf nicht leer sein");
            return;
        }

        String trimmedAbschluss = abschluss.trim();

        // Prüfe ob der Abschluss in der Liste der erlaubten Abschlüsse ist
        boolean gefunden = false;
        for (String erlaubterAbschluss : ERLAUBTE_ABSCHLUESSE) {
            if (trimmedAbschluss.equalsIgnoreCase(erlaubterAbschluss)) {
                gefunden = true;
                break;
            }
        }

        if (!gefunden) {
            result.addError("Ungültiger Abschluss '" + trimmedAbschluss + "'. Erlaubt sind: " +
                    String.join(", ", ERLAUBTE_ABSCHLUESSE));
        }
    }

    /**
     * Schnelle Validierung nur für UPDATE-Operationen
     * (prüft nur Felder, die aktualisiert werden können)
     */
    public ValidationResult validateForUpdate(Studiengang studiengang) {
        ValidationResult result = new ValidationResult();

        if (studiengang == null) {
            result.addError("Studiengang darf nicht null sein");
            return result;
        }

        if (studiengang.getStudiengangId() <= 0) {
            result.addError("Studiengang-ID muss für Update gesetzt sein");
        }

        // Alle Felder validieren wie bei Create
        validateFachbereichId(studiengang.getFachbereichId(), result);
        validateBezeichnung(studiengang.getBezeichnung(), result);
        validateKuerzel(studiengang.getKuerzel(), result);
        validateAbschlusstitel(studiengang.getAbschlusstitel(), result);
        validateAbschluss(studiengang.getAbschluss(), result);

        return result;
    }
}