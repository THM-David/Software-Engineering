package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Betreuer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator für Betreuer-Objekte
 */
public class BetreuerValidator {

    private static final List<String> ERLAUBTE_ROLLEN = Arrays.asList(
            "Professor", "Wissenschaftlicher Mitarbeiter", "Lehrbeauftragter", "Dozent"
    );

    private static final List<String> ERLAUBTE_TITEL = Arrays.asList(
            "Prof. Dr.", "Dr.", "Prof.", "Dr.-Ing.", "Prof. Dr.-Ing.", ""
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public ValidationResult validate(Betreuer betreuer) {
        ValidationResult result = new ValidationResult();

        if (betreuer == null) {
            result.addError("Betreuer darf nicht null sein");
            return result;
        }

        validateVorname(betreuer.getVorname(), result);
        validateNachname(betreuer.getNachname(), result);
        validateTitel(betreuer.getTitel(), result);
        validateEmail(betreuer.getEmail(), result);
        validateRolle(betreuer.getRolle(), result);

        return result;
    }

    private void validateVorname(String vorname, ValidationResult result) {
        if (vorname == null || vorname.trim().isEmpty()) {
            result.addError("Vorname darf nicht leer sein");
            return;
        }
        if (vorname.trim().length() < 2) {
            result.addError("Vorname muss mindestens 2 Zeichen lang sein");
        }
        if (vorname.trim().length() > 100) {
            result.addError("Vorname darf maximal 100 Zeichen lang sein");
        }
    }

    private void validateNachname(String nachname, ValidationResult result) {
        if (nachname == null || nachname.trim().isEmpty()) {
            result.addError("Nachname darf nicht leer sein");
            return;
        }
        if (nachname.trim().length() < 2) {
            result.addError("Nachname muss mindestens 2 Zeichen lang sein");
        }
        if (nachname.trim().length() > 100) {
            result.addError("Nachname darf maximal 100 Zeichen lang sein");
        }
    }

    private void validateTitel(String titel, ValidationResult result) {
        if (titel == null) {
            return; // Titel ist optional
        }

        if (!titel.isEmpty() && !ERLAUBTE_TITEL.contains(titel)) {
            result.addError("Ungültiger Titel '" + titel + "'. Erlaubt sind: " +
                    String.join(", ", ERLAUBTE_TITEL));
        }
    }

    private void validateEmail(String email, ValidationResult result) {
        if (email == null || email.trim().isEmpty()) {
            result.addError("E-Mail darf nicht leer sein");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            result.addError("Ungültige E-Mail-Adresse");
        }
    }

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

    public ValidationResult validateForUpdate(Betreuer betreuer) {
        ValidationResult result = new ValidationResult();

        if (betreuer == null) {
            result.addError("Betreuer darf nicht null sein");
            return result;
        }

        if (betreuer.getBetreuerId() <= 0) {
            result.addError("Betreuer-ID muss für Update gesetzt sein");
        }

        validateVorname(betreuer.getVorname(), result);
        validateNachname(betreuer.getNachname(), result);
        validateTitel(betreuer.getTitel(), result);
        validateEmail(betreuer.getEmail(), result);
        validateRolle(betreuer.getRolle(), result);

        return result;
    }
}