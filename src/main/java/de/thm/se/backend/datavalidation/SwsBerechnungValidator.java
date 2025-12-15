package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.SwsBerechnung;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Validator für SwsBerechnung-Objekte.
 * Prüft Vollständigkeit und Gültigkeit der Daten sowie Plausibilität der SWS-Werte.
 */
public class SwsBerechnungValidator {

    // Erlaubte Werte für Rolle
    private static final List<String> ERLAUBTE_ROLLEN = Arrays.asList(
            "Referent", "Korreferent", "Betreuer"
    );

    // Validierungsregeln für SWS-Werte
    private static final float MIN_SWS_WERT = 0.0f;
    private static final float MAX_SWS_WERT = 2.0f;

    /**
     * Validiert ein SwsBerechnung-Objekt vollständig
     * @param sws Das zu validierende Objekt
     * @return ValidationResult mit allen gefundenen Fehlern
     */
    public ValidationResult validate(SwsBerechnung sws) {
        ValidationResult result = new ValidationResult();

        if (sws == null) {
            result.addError("SWS-Berechnung darf nicht null sein");
            return result;
        }

        // Pflichtfelder prüfen
        validateArbeitId(sws.getArbeitId(), result);
        validateBetreuerId(sws.getBetreuerId(), result);
        validateSemesterId(sws.getSemesterId(), result);
        validatePruefungsordnungId(sws.getPruefungsordnungId(), result);
        validateSwsWert(sws.getSwsWert(), result);
        validateRolle(sws.getRolle(), result);
        validateBerechnetAm(sws.getBerechnetAm(), result);

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
     * Validiert die Betreuer-ID
     */
    private void validateBetreuerId(int betreuerId, ValidationResult result) {
        if (betreuerId <= 0) {
            result.addError("Betreuer-ID muss größer als 0 sein");
        }
    }

    /**
     * Validiert die Semester-ID
     */
    private void validateSemesterId(int semesterId, ValidationResult result) {
        if (semesterId <= 0) {
            result.addError("Semester-ID muss größer als 0 sein");
        }
    }

    /**
     * Validiert die Prüfungsordnungs-ID
     */
    private void validatePruefungsordnungId(int pruefungsordnungId, ValidationResult result) {
        if (pruefungsordnungId <= 0) {
            result.addError("Prüfungsordnungs-ID muss größer als 0 sein");
        }
    }

    /**
     * Validiert den SWS-Wert
     * SWS-Werte sollten realistisch sein (typisch zwischen 0.5 und 10)
     */
    private void validateSwsWert(float swsWert, ValidationResult result) {
        if (swsWert < MIN_SWS_WERT) {
            result.addError("SWS-Wert muss mindestens " + MIN_SWS_WERT + " SWS umfassen");
        }

        if (swsWert > MAX_SWS_WERT) {
            result.addError("SWS-Wert ist zu hoch (maximal " + MAX_SWS_WERT + " erlaubt)");
        }
    }

    /**
     * Validiert die Rolle des Betreuers
     */
    private void validateRolle(String rolle, ValidationResult result) {
        if (rolle == null || rolle.trim().isEmpty()) {
            result.addError("Rolle darf nicht leer sein");
            return;
        }

        if (!ERLAUBTE_ROLLEN.contains(rolle)) {
            result.addError("Ungültige Rolle '" + rolle + "'. Erlaubt sind: " +
                    String.join(", ", ERLAUBTE_ROLLEN));
        }
    }

    /**
     * Validiert das Berechnungsdatum
     */
    private void validateBerechnetAm(LocalDate berechnetAm, ValidationResult result) {
        if (berechnetAm == null) {
            result.addError("Berechnungsdatum darf nicht leer sein");
            return;
        }

        // Berechnungsdatum sollte nicht in der Zukunft liegen
        if (berechnetAm.isAfter(LocalDate.now())) {
            result.addError("Berechnungsdatum darf nicht in der Zukunft liegen");
        }

        // Berechnungsdatum sollte nicht zu weit in der Vergangenheit liegen (mehr als 10 Jahre)
        LocalDate minVergangenheit = LocalDate.now().minusYears(10);
        if (berechnetAm.isBefore(minVergangenheit)) {
            result.addError("Berechnungsdatum liegt zu weit in der Vergangenheit (maximal 10 Jahre)");
        }
    }

    /**
     * Schnelle Validierung nur für UPDATE-Operationen
     * (prüft nur Felder, die aktualisiert werden können)
     */
    public ValidationResult validateForUpdate(SwsBerechnung sws) {
        ValidationResult result = new ValidationResult();

        if (sws == null) {
            result.addError("SWS-Berechnung darf nicht null sein");
            return result;
        }

        if (sws.getSwsId() <= 0) {
            result.addError("SWS-ID muss für Update gesetzt sein");
        }

        // Alle Felder validieren wie bei Create
        validateArbeitId(sws.getArbeitId(), result);
        validateBetreuerId(sws.getBetreuerId(), result);
        validateSemesterId(sws.getSemesterId(), result);
        validatePruefungsordnungId(sws.getPruefungsordnungId(), result);
        validateSwsWert(sws.getSwsWert(), result);
        validateRolle(sws.getRolle(), result);
        validateBerechnetAm(sws.getBerechnetAm(), result);

        return result;
    }
}