package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Fachbereich;

public class FachbereichValidator {

    private static final int MIN_BEZEICHNUNG_LAENGE = 3;
    private static final int MAX_BEZEICHNUNG_LAENGE = 200;
    private static final int MIN_FBNAME_LAENGE = 2;
    private static final int MAX_FBNAME_LAENGE = 50;

    public ValidationResult validate(Fachbereich fb) {
        ValidationResult result = new ValidationResult();

        if (fb == null) {
            result.addError("Fachbereich darf nicht null sein");
            return result;
        }

        validateFBID(fb.getFachbereichId(), result);
        validateBezeichnung(fb.getBezeichnung(), result);
        validateFachbereich(fb.getFbname(), result);

        // Bezeichnung validieren
        if (fb.getBezeichnung() == null || fb.getBezeichnung().trim().isEmpty()) {
            result.addError("Bezeichnung darf nicht leer sein");
        } else {
            String bez = fb.getBezeichnung().trim();
            if (bez.length() < MIN_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung muss mindestens " + MIN_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
            }
            if (bez.length() > MAX_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung darf maximal " + MAX_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
            }
        }

        // FB-Name validieren
        if (fb.getFbname() == null || fb.getFbname().trim().isEmpty()) {
            result.addError("FB-Name darf nicht leer sein");
        } else {
            String name = fb.getFbname().trim();
            if (name.length() < MIN_FBNAME_LAENGE) {
                result.addError("FB-Name muss mindestens " + MIN_FBNAME_LAENGE + " Zeichen lang sein");
            }
            if (name.length() > MAX_FBNAME_LAENGE) {
                result.addError("FB-Name darf maximal " + MAX_FBNAME_LAENGE + " Zeichen lang sein");
            }
        }

        return result;
    }

    public void validateFBID(int FBID, ValidationResult result) {
        if (FBID < 0) {
            result.addError("Fachbereich-ID darf nicht negativ sein");
        }
    }

    public void validateBezeichnung(String bezeichnung, ValidationResult result) {
        if (bezeichnung == null || bezeichnung.trim().isEmpty()) {
            result.addError("Bezeichnung darf nicht leer sein");
        }  else {
            if (bezeichnung.length() > MAX_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung darf nicht länger als " +  MAX_BEZEICHNUNG_LAENGE + " Zeichen sein");
            }
            if  (bezeichnung.length() < MIN_BEZEICHNUNG_LAENGE) {
                result.addError("Bezeichnung muss mindestens " + MIN_BEZEICHNUNG_LAENGE + " Zeichen lang sein");
            }
        }
    }

    public void validateFachbereich(String fb, ValidationResult result) {
        if  (fb == null || fb.trim().isEmpty()) {
            result.addError("Fachbereich darf nicht leer sein");
        } else {
            if (fb.length() < MIN_FBNAME_LAENGE) {
                result.addError("Fachbereich muss mindestens " + MIN_FBNAME_LAENGE + " Zeichen lang sein");
            }
            if (fb.length() > MAX_FBNAME_LAENGE) {
                result.addError("Fachbereich darf maximal " +  MAX_FBNAME_LAENGE + " Zeichen lang sein");
            }
        }
    }

    public ValidationResult validateForUpdate(Fachbereich fb) {
        ValidationResult result = validate(fb);

        if (fb.getFachbereichId() <= 0) {
            result.addError("Fachbereich-ID muss für Update gesetzt sein");
        }

        return result;
    }
}