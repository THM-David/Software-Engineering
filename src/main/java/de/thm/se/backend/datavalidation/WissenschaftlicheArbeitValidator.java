package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.WissenschaftlicheArbeit;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
/**
 * Validator für WissenschaftlicheArbeit-Objekte.
 * Prüft Vollständigkeit und Gültigkeit der Daten.
 */
public class WissenschaftlicheArbeitValidator {

    // Erlaubte Werte für Typ und Status
    private static final List<String> ERLAUBTE_TYPEN = Arrays.asList(
            "Bachelorarbeit", "Masterarbeit", "Projektarbeit"
    );

    private static final List<String> ERLAUBTE_STATUS = Arrays.asList(
            "Angemeldet", "Geplant", "In Bearbeitung", "Abgeschlossen", "Abgebrochen", "Bewertet"
    );

    // Validierungsregeln
    private static final int MIN_TITEL_LAENGE = 5;
    private static final int MAX_TITEL_LAENGE = 500;

    /**
     * Validiert ein WissenschaftlicheArbeit-Objekt vollständig
     * @param arbeit Das zu validierende Objekt
     * @return ValidationResult mit allen gefundenen Fehlern
     */
    public ValidationResult validate(WissenschaftlicheArbeit arbeit) {
        ValidationResult result = new ValidationResult();

        if (arbeit == null) {
            result.addError("Arbeit darf nicht null sein");
            return result;
        }

        // Pflichtfelder prüfen
        validateStudierendenId(arbeit.getStudierendenId(), result);
        validateStudiengangId(arbeit.getStudiengangId(), result);
        validatePruefungsordnungId(arbeit.getPruefungsordnungId(), result);
        validateSemesterId(arbeit.getSemesterId(), result);
        validateTitel(arbeit.getTitel(), result);
        validateTyp(arbeit.getTyp(), result);
        validateStatus(arbeit.getStatus(), result);

        return result;
    }

    /**
     * Validiert nur die ID eines Studierenden
     * Hier kann man weitere Überprüfungen durchführen, bspw. in StudierendenDB überprüfen, ob die ID existiert
     */
    private void validateStudierendenId(int studierendenId, ValidationResult result) {
        if (studierendenId <= 0) {
            result.addError("Studierenden-ID muss größer als 0 sein");
        }
    }

    /**
     * Validiert die Studiengang-ID
     * Wie bei StudierendenID kann man auch in StudiengangDB überprüfen, ob die ID existiert
     */
    private void validateStudiengangId(int studiengangId, ValidationResult result) {
        if (studiengangId <= 0) {
            result.addError("Studiengang-ID muss größer als 0 sein");
        }
    }

    /**
     * Validiert die Prüfungsordnungs-ID
     * Gleiches gilt hier
     */
    private void validatePruefungsordnungId(int pruefungsordnungId, ValidationResult result) {
        if (pruefungsordnungId <= 0) {
            result.addError("Prüfungsordnungs-ID muss größer als 0 sein");
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
     * Validiert den Titel der Arbeit
     */
    private void validateTitel(String titel, ValidationResult result) {
        if (titel == null || titel.trim().isEmpty()) {
            result.addError("Titel darf nicht leer sein");
            return;
        }

        String trimmedTitel = titel.trim();
        if (trimmedTitel.length() < MIN_TITEL_LAENGE) {
            result.addError("Titel muss mindestens " + MIN_TITEL_LAENGE + " Zeichen lang sein");
        }

        if (trimmedTitel.length() > MAX_TITEL_LAENGE) {
            result.addError("Titel darf maximal " + MAX_TITEL_LAENGE + " Zeichen lang sein");
        }
    }

    /**
     * Validiert den Typ der Arbeit
     */
    private void validateTyp(String typ, ValidationResult result) {
        if (typ == null || typ.trim().isEmpty()) {
            result.addError("Typ darf nicht leer sein");
            return;
        }

        if (!ERLAUBTE_TYPEN.contains(typ)) {
            result.addError("Ungültiger Typ '" + typ + "'. Erlaubt sind: " +
                    String.join(", ", ERLAUBTE_TYPEN));
        }
    }

    /**
     * Validiert den Status der Arbeit
     */
    private void validateStatus(String status, ValidationResult result) {
        if (status == null || status.trim().isEmpty()) {
            result.addError("Status darf nicht leer sein");
            return;
        }

        if (!ERLAUBTE_STATUS.contains(status)) {
            result.addError("Ungültiger Status '" + status + "'. Erlaubt sind: " +
                    String.join(", ", ERLAUBTE_STATUS));
        }
    }

    /**
     * Schnelle Validierung nur für UPDATE-Operationen
     * (prüft nur Felder, die aktualisiert werden können)
     */
    public ValidationResult validateForUpdate(WissenschaftlicheArbeit arbeit) {
        ValidationResult result = new ValidationResult();

        if (arbeit == null) {
            result.addError("Arbeit darf nicht null sein");
            return result;
        }

        if (arbeit.getArbeitId() <= 0) {
            result.addError("Arbeit-ID muss für Update gesetzt sein");
        }

        validateTitel(arbeit.getTitel(), result);
        validateTyp(arbeit.getTyp(), result);
        validateStatus(arbeit.getStatus(), result);

        return result;
    }
}