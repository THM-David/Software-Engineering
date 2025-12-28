package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Notenbestandteil;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class NotenbestandteilValidator {

    private static final List<String> ERLAUBTE_ROLLEN = Arrays.asList(
            "Referent", "Korreferent"
    );

    private static final double MIN_NOTE = 1.0;
    private static final double MAX_NOTE = 5.0;
    private static final double MIN_GEWICHTUNG = 0.0;
    private static final double MAX_GEWICHTUNG = 1.0;

    public ValidationResult validate(Notenbestandteil note) {
        ValidationResult result = new ValidationResult();

        if (note == null) {
            result.addError("Notenbestandteil darf nicht null sein");
            return result;
        }

        validateNotenID(note.getNotenId(), result);
        validateArbeitID(note.getArbeitId(), result);
        validateBetreuerID(note.getBetreuerId(), result);
        validateRolle(note.getRolle(), result);
        validateNoteArbeit(note.getNoteArbeit(), result);
        validateNoteKollqium(note.getNoteKolloquium(), result);
        validateGewichtung(note.getGewichtung(), result);

        if (note.getArbeitId() <= 0) {
            result.addError("Arbeit-ID muss größer als 0 sein");
        }

        if (note.getBetreuerId() <= 0) {
            result.addError("Betreuer-ID muss größer als 0 sein");
        }

        // Rolle validieren
        if (note.getRolle() == null || note.getRolle().trim().isEmpty()) {
            result.addError("Rolle darf nicht leer sein");
        } else if (!ERLAUBTE_ROLLEN.contains(note.getRolle())) {
            result.addError("Ungültige Rolle. Erlaubt sind: " + String.join(", ", ERLAUBTE_ROLLEN));
        }

        // Note Arbeit validieren
        if (note.getNoteArbeit() < MIN_NOTE || note.getNoteArbeit() > MAX_NOTE) {
            result.addError("Note Arbeit muss zwischen " + MIN_NOTE + " und " + MAX_NOTE + " liegen");
        }

        // Note Kolloquium validieren
        if (note.getNoteKolloquium() < MIN_NOTE || note.getNoteKolloquium() > MAX_NOTE) {
            result.addError("Note Kolloquium muss zwischen " + MIN_NOTE + " und " + MAX_NOTE + " liegen");
        }

        // Gewichtung validieren
        if (note.getGewichtung() < MIN_GEWICHTUNG || note.getGewichtung() > MAX_GEWICHTUNG) {
            result.addError("Gewichtung muss zwischen " + MIN_GEWICHTUNG + " und " + MAX_GEWICHTUNG + " liegen");
        }

        return result;
    }

    private void validateNotenID(int notenId, ValidationResult result) {
        if (notenId <= 0) {
            result.addError("Noten-ID darf nicht negativ sein");
        }
    }

    private void validateArbeitID(int arbeitId, ValidationResult result) {
        if (arbeitId <= 0) {
            result.addError("Arbeit-ID darf nicht negativ sein");
        }
    }

    private void validateBetreuerID(int betreuerId, ValidationResult result) {
        if (betreuerId <= 0) {
            result.addError("Betreuer ID darf nicht negativ sein");
        }
    }

    private void validateRolle(String rolle, ValidationResult result) {
        if (rolle.trim().isEmpty()) {
            result.addError("Rolle darf nicht leer sein");
        }

        if (!ERLAUBTE_ROLLEN.contains(rolle)) {
            result.addError("Ungültiger Typ '" + rolle + "'. Erlaubt sind: " +
                    String.join(", ", ERLAUBTE_ROLLEN));
        }
    }

    private void validateNoteArbeit(double noteArbeit, ValidationResult result) {
        if (noteArbeit <= 0) {
            result.addError("Note der Arbeit darf nicht negativ sein");
        }

        if (noteArbeit < MIN_NOTE || noteArbeit > MAX_NOTE) {
            result.addError("Note der Arbeit muss zwischen " + MIN_NOTE + " und " + MAX_NOTE + " liegen");
        }
    }

    private void validateNoteKollqium(double noteKollqium, ValidationResult result) {
        if (noteKollqium <= 0) {
            result.addError("Note des Kolloqiums darf nicht negativ sein");
        }

        if (noteKollqium < MIN_NOTE || noteKollqium > MAX_NOTE) {
            result.addError("Note des Kolloqiums muss zwischen " + MIN_NOTE + " und " + MAX_NOTE + " liegen");
        }
    }

    private void validateGewichtung(double gewichtung, ValidationResult result) {
        if (gewichtung <= 0) {
            result.addError("Gewichtung darf nicht negativ sein");
        }

        if (gewichtung < MIN_GEWICHTUNG || gewichtung > MAX_GEWICHTUNG) {
            result.addError("Gewichtung der Note muss zwischen " + MIN_GEWICHTUNG + " und " + MAX_GEWICHTUNG + " liegen");
        }
    }

    public ValidationResult validateForUpdate(Notenbestandteil note) {
        ValidationResult result = validate(note);

        if (note.getNotenId() <= 0) {
            result.addError("Noten-ID muss für Update gesetzt sein");
        }

        return result;
    }
}