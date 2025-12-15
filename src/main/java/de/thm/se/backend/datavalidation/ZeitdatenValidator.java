package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Zeitdaten;
import java.time.LocalDate;

/**
 * Validator für Zeitdaten-Objekte.
 * Prüft Vollständigkeit und Gültigkeit der Daten sowie logische Konsistenz der Datumsangaben.
 */
public class ZeitdatenValidator {

    /**
     * Validiert ein Zeitdaten-Objekt vollständig
     * @param zeit Das zu validierende Objekt
     * @return ValidationResult mit allen gefundenen Fehlern
     */
    public ValidationResult validate(Zeitdaten zeit) {
        ValidationResult result = new ValidationResult();

        if (zeit == null) {
            result.addError("Zeitdaten dürfen nicht leer sein");
            return result;
        }

        // Pflichtfelder prüfen
        validateArbeitId(zeit.getArbeitId(), result);
        validateAnfangsdatum(zeit.getAnfangsdatum(), result);
        validateAbgabedatum(zeit.getAbgabedatum(), result);
        validateKolloquiumsdatum(zeit.getKolloquiumsdatum(), result);

        // Logische Konsistenz prüfen (nur wenn einzelne Daten valide sind)
        if (result.isValid()) {
            validateDatumReihenfolge(zeit, result);
        }

        return result;
    }

    /**
     * Validiert die Arbeit-ID
     */
    private void validateArbeitId(int arbeitId, ValidationResult result) {
        if (arbeitId <= 0) {
            result.addError("Arbeit-ID muss größer als 0 sein");
        }
    }

    /**
     * Validiert das Anfangsdatum
     */
    private void validateAnfangsdatum(LocalDate anfangsdatum, ValidationResult result) {
        if (anfangsdatum == null) {
            result.addError("Anfangsdatum darf nicht leer sein");
            return;
        }

        // Anfangsdatum sollte nicht in ferner Zukunft liegen (mehr als 1 Jahr)
        LocalDate maxZukunft = LocalDate.now().plusYears(1);
        if (anfangsdatum.isAfter(maxZukunft)) {
            result.addError("Anfangsdatum liegt zu weit in der Zukunft (maximal 1 Jahr)");
        }

        // Anfangsdatum sollte nicht zu weit in der Vergangenheit liegen (mehr als 10 Jahre)
        LocalDate minVergangenheit = LocalDate.now().minusYears(10);
        if (anfangsdatum.isBefore(minVergangenheit)) {
            result.addError("Anfangsdatum liegt zu weit in der Vergangenheit (maximal 10 Jahre)");
        }
    }

    /**
     * Validiert das Abgabedatum
     */
    private void validateAbgabedatum(LocalDate abgabedatum, ValidationResult result) {
        if (abgabedatum == null) {
            result.addError("Abgabedatum darf nicht leer sein");
            return;
        }

        // Abgabedatum sollte nicht zu weit in der Zukunft liegen (mehr als 2 Jahre)
        LocalDate maxZukunft = LocalDate.now().plusYears(2);
        if (abgabedatum.isAfter(maxZukunft)) {
            result.addError("Abgabedatum liegt zu weit in der Zukunft (maximal 2 Jahre)");
        }
    }

    /**
     * Validiert das Kolloquiumsdatum
     * Kolloquiumsdatum ist optional, muss aber wenn gesetzt gültig sein
     */
    private void validateKolloquiumsdatum(LocalDate kolloquiumsdatum, ValidationResult result) {
        if (kolloquiumsdatum == null) {
            // Kolloquiumsdatum ist optional - kein Fehler
            return;
        }

        // Wenn gesetzt, sollte es nicht zu weit in der Zukunft liegen (mehr als 2 Jahre)
        LocalDate maxZukunft = LocalDate.now().plusYears(2);
        if (kolloquiumsdatum.isAfter(maxZukunft)) {
            result.addError("Kolloquiumsdatum liegt zu weit in der Zukunft (maximal 2 Jahre)");
        }
    }

    /**
     * Validiert die logische Reihenfolge der Daten
     * - Anfangsdatum muss vor Abgabedatum liegen
     * - Abgabedatum muss vor Kolloquiumsdatum liegen (falls gesetzt)
     * - Bearbeitungszeit sollte realistisch sein (mind. 1 Woche, max. 2 Jahre)
     */
    private void validateDatumReihenfolge(Zeitdaten zeit, ValidationResult result) {
        LocalDate anfang = zeit.getAnfangsdatum();
        LocalDate abgabe = zeit.getAbgabedatum();
        LocalDate kolloquium = zeit.getKolloquiumsdatum();

        // Anfangsdatum muss vor Abgabedatum liegen
        if (anfang != null && abgabe != null) {
            if (!anfang.isBefore(abgabe)) {
                result.addError("Anfangsdatum muss vor dem Abgabedatum liegen");
            }

            // Bearbeitungszeit prüfen
            long bearbeitungstage = java.time.temporal.ChronoUnit.DAYS.between(anfang, abgabe);

            if (bearbeitungstage < 7) {
                result.addError("Bearbeitungszeit zu kurz (mindestens 1 Woche erforderlich)");
            }

            if (bearbeitungstage > 730) { // 2 Jahre
                result.addError("Bearbeitungszeit zu lang (maximal 2 Jahre erlaubt)");
            }
        }

        // Kolloquium muss nach Abgabedatum liegen (falls gesetzt)
        if (abgabe != null && kolloquium != null) {
            if (!abgabe.isBefore(kolloquium) && !abgabe.isEqual(kolloquium)) {
                result.addError("Kolloquiumsdatum muss am oder nach dem Abgabedatum liegen");
            }

            // Kolloquium sollte nicht zu lange nach Abgabe sein (maximal 6 Monate)
            long tageNachAbgabe = java.time.temporal.ChronoUnit.DAYS.between(abgabe, kolloquium);
            if (tageNachAbgabe > 180) {
                result.addError("Kolloquiumsdatum liegt zu lange nach dem Abgabedatum (maximal 6 Monate)");
            }
        }
    }

    /**
     * Schnelle Validierung nur für UPDATE-Operationen
     * (prüft nur Felder, die aktualisiert werden können)
     */
    public ValidationResult validateForUpdate(Zeitdaten zeit) {
        ValidationResult result = new ValidationResult();

        if (zeit == null) {
            result.addError("Zeitdaten dürfen nicht null sein");
            return result;
        }

        if (zeit.getZeitId() <= 0) {
            result.addError("Zeit-ID muss für Update gesetzt sein");
        }

        // Alle Felder validieren wie bei Create
        validateArbeitId(zeit.getArbeitId(), result);
        validateAnfangsdatum(zeit.getAnfangsdatum(), result);
        validateAbgabedatum(zeit.getAbgabedatum(), result);
        validateKolloquiumsdatum(zeit.getKolloquiumsdatum(), result);

        if (result.isValid()) {
            validateDatumReihenfolge(zeit, result);
        }

        return result;
    }
}