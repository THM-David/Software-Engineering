package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Pruefungsordnung;

import java.time.LocalDate;

public class PruefungsordnungValidator {

    private static final int MIN_BEZEICHNUNG_LAENGE = 3;
    private static final int MAX_BEZEICHNUNG_LAENGE = 100;
    private static final float MIN_SWS = 0.0f;
    private static final float MAX_SWS = 2.0f;

    public ValidationResult validate(Pruefungsordnung po) {
        ValidationResult result = new ValidationResult();

        if (po == null) {
            result.addError("Prüfungsordnung darf nicht leer sein");
            return result;
        }

        validatePOID(po.getPruefungsordnungId(), result);
        validateStudID(po.getStudiengangId(), result);
        validateBezeichnung(po.getBezeichnung(), result);
        validateDates(po, result);
        validateSWSReferent(po.getSwsReferent(), result);
        validateSWSKorreferent(po.getSwsKoreferent(), result);

        return result;
    }

    private void validatePOID(int id,  ValidationResult result) {
        if (id <= 0) {
            result.addError("Prüfungsordnung-ID muss größer als 0 sein");
        }
    }

    private void validateStudID(int id,  ValidationResult result) {
        if (id <= 0) {
            result.addError("Studiengang-ID muss größer als 0 sein");
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

    private void validateDates(Pruefungsordnung po, ValidationResult result) {
        LocalDate gueltigAb = po.getGueltigAb();
        LocalDate gueltigBis = po.getGueltigBis();
        if (gueltigAb == null) {
            result.addError("Gueltig-Ab darf nicht null sein");
        }
        if (gueltigBis == null) {
            result.addError("Gueltig-Bis darf nicht null sein");
        }
        assert gueltigAb != null;
        if (gueltigAb.isAfter(gueltigBis)) {
            result.addError("Gueltig-Ab muss zeitlich vor Gueltig-Bis liegen");
        }
    }

    private void validateSWSReferent(float swsref, ValidationResult result) {
        if (swsref < MIN_SWS) {
            result.addError("SWS-Wert des Referenten muss mindestens " + MIN_SWS + " SWS umfassen");
        }

        if (swsref > MAX_SWS) {
            result.addError("SWS-Wert des Referenten ist zu hoch (maximal " + MAX_SWS + " erlaubt)");
        }
    }

    private void validateSWSKorreferent(float swskorref, ValidationResult result) {
        if (swskorref < MIN_SWS) {
            result.addError("SWS-Wert des Korreferenten muss mindestens " + MIN_SWS + " SWS umfassen");
        }

        if (swskorref > MAX_SWS) {
            result.addError("SWS-Wert des Korreferenten ist zu hoch (maximal " + MAX_SWS + " erlaubt)");
        }
    }

    public ValidationResult validateForUpdate(Pruefungsordnung po) {
        ValidationResult result = validate(po);

        if (po.getPruefungsordnungId() <= 0) {
            result.addError("Prüfungsordnung-ID muss für Update gesetzt sein");
        }

        return result;
    }
}