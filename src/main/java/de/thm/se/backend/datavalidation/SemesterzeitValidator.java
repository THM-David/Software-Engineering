package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Semesterzeit;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Repository;

@Repository
public class SemesterzeitValidator {

    private static final int MIN_BEZEICHNUNG_LAENGE = 5;
    private static final int MAX_BEZEICHNUNG_LAENGE = 100;
    private static final long MIN_DAUER_TAGE = 90; // 3 Monate
    private static final long MAX_DAUER_TAGE = 210; // 7 Monate

    public ValidationResult validate(Semesterzeit semZ) {
        ValidationResult result = new ValidationResult();

        if (semZ == null) {
            result.addError("Semesterzeit darf nicht null sein");
            return result;
        }

        validateBeginn(semZ.getBeginn(), result);
        validateEnde(semZ.getEnde(), result);
        validateBezeichnung(semZ.getBezeichnung(), result);

        // Logische Validierung der Daten, nachdem alle Felder gültig sind
        if (result.isValid()) {
            validateLogik(semZ, result);
        }

        return result;
    }

    private void validateBeginn(LocalDate beginn, ValidationResult result) {
        // Beginn validieren - Darf nicht leer sein
        if (beginn == null) {
            result.addError("Beginn darf nicht leer sein");
        }
    }

    private void validateEnde(LocalDate ende, ValidationResult result) {
        if (ende == null) {
            result.addError("Ende darf nicht leer sein");
        }
    }

    private void validateBezeichnung(String bezeichnung, ValidationResult result) {
        if (bezeichnung == null || bezeichnung.trim().isEmpty()) {
            result.addError("Bezeichnung darf nicht leer sein");
        } else {
            String bez = bezeichnung.trim();
            if (bez.length() < MIN_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung muss mindestens " + MIN_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
            }
            if (bez.length() > MAX_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung darf maximal " + MAX_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
            }
        }
    }

    private void validateLogik(Semesterzeit semZ, ValidationResult result) {
        LocalDate beginn = semZ.getBeginn();
        LocalDate ende = semZ.getEnde();
        if (beginn != null && ende != null) {
            if (!beginn.isBefore(ende)) {
                result.addError("Beginn muss vor Ende liegen");
            }

            long dauer = ChronoUnit.DAYS.between(beginn, ende);
            if (dauer < MIN_DAUER_TAGE) {
                result.addError("Semesterzeit zu kurz (mindestens " + MIN_DAUER_TAGE + " Tage)");
            }
            if (dauer > MAX_DAUER_TAGE) {
                result.addError("Semesterzeit zu lang (maximal " + MAX_DAUER_TAGE + " Tage)");
            }
        }
    }

    public ValidationResult validateForUpdate(Semesterzeit semZ) {
        ValidationResult result = validate(semZ);

        if (semZ.getSemesterzeitId() <= 0) {
            result.addError("Semesterzeit-ID muss für Update gesetzt sein");
        }

        return result;
    }
}