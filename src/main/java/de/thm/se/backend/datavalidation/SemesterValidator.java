package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Semester;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class SemesterValidator {

    private static final List<String> ERLAUBTE_TYPEN = Arrays.asList(
            "Wintersemester", "Sommersemester"
    );

    private static final int MIN_BEZEICHNUNG_LAENGE = 5;
    private static final int MAX_BEZEICHNUNG_LAENGE = 50;

    public ValidationResult validate(Semester sem) {
        ValidationResult result = new ValidationResult();

        if (sem == null) {
            result.addError("Semester darf nicht leer sein");
            return result;
        }

        validateSemesterID(sem.getSemesterId(), result);
        validateSemesterzeitID(sem.getSemesterzeitId(), result);
        validateBezeichnung(sem.getBezeichnung(), result);
        validateTyp(sem.getTyp(), result);
        validateJahr(sem.getJahr(), result);

        return result;
    }

    private void validateSemesterID(int id, ValidationResult result) {
        if (id <= 0) {
            result.addError("Semester-ID darf nicht kleiner als 0 sein");
        }
    }

    private void validateSemesterzeitID(int id,  ValidationResult result) {
        if (id <= 0) {
            result.addError("Semesterzeit-ID muss größer als 0 sein");
        }
    }

    private void validateBezeichnung(String bezeichnung,  ValidationResult result) {
        String bez = bezeichnung.trim();
        if (bezeichnung == null || bez.isEmpty()) {
            result.addError("Bezeichnung darf nicht leer sein");
        } else {
            if (bez.length() < MIN_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung muss mindestens " + MIN_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
            }
            if (bez.length() > MAX_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung darf maximal " + MAX_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
            }
        }
    }

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

    // Weitere Prüfmethoden möglich (z.B. Jahr darf nicht zu lange in Zukunft/Vergangenheit liegen)
    private void validateJahr(LocalDate jahr, ValidationResult result) {
        if (jahr == null) {
            result.addError("Jahr darf nicht leer sein");
        }
    }

    public ValidationResult validateForUpdate(Semester sem) {
        ValidationResult result = validate(sem);

        if (sem.getSemesterId() <= 0) {
            result.addError("Semester-ID muss für Update gesetzt sein");
        }

        return result;
    }
}