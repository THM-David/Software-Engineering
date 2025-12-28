package de.thm.se.backend.datavalidation;

import de.thm.se.backend.model.Studierende;
import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.stereotype.Repository;

@Repository
/**
 * Validator für Studierende-Objekte.
 * Prüft Vollständigkeit und Gültigkeit der Daten.
 */
public class StudierendeValidator {

    // Regex für eine einfache E-Mail-Validierung
    // Kann noch angepasst werden, falls man auf spezifische Uni-Mailadressen prüfen will (z.B. *@zdh.thm.de)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    //Regex für eine Adresse
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "^[A-Za-z-. ]+[A-Za-z0-9]$"
    );

    // Validierungsregeln
    private static final int MIN_NAME_LAENGE = 2;
    private static final int MAX_NAME_LAENGE = 50;
    private static final int MAX_MATRIKELNUMMER_LAENGE = 10;
    private static final int MIN_ALTER_STUDIERNDE = 16;

    /**
     * Validiert ein Studierende-Objekt vollständig (für CREATE-Operation)
     * @param studi Das zu validierende Objekt
     * @return ValidationResult mit allen gefundenen Fehlern
     */
    public ValidationResult validate(Studierende studi) {
        ValidationResult result = new ValidationResult();

        if (studi == null) {
            result.addError("Studierenden-Objekt darf nicht null sein");
            return result;
        }

        // Pflichtfelder und Format prüfen
        validateMatrikelnummer(studi.getMatrikelnummer(), result);
        validateVorname(studi.getVorname(), result);
        validateNachname(studi.getNachname(), result);
        validateEmail(studi.getEmail(), result);
        validateGeburtsdatum(studi.getGeburtsdatum(), result);
        validateAdresse(studi.getAdresse(), result);

        return result;
    }

    /**
     * Validiert die Matrikelnummer.
     * (Einfache Prüfung: nicht leer, max 10 Zeichen)
     */
    private void validateMatrikelnummer(String matrikelnummer, ValidationResult result) {
        if (matrikelnummer == null || matrikelnummer.trim().isEmpty()) {
            result.addError("Matrikelnummer darf nicht leer sein");
            return;
        }
        String trimmedMatrikelnummer = matrikelnummer.trim();
        if (trimmedMatrikelnummer.length() > MAX_MATRIKELNUMMER_LAENGE) {
            result.addError("Matrikelnummer darf maximal " + MAX_MATRIKELNUMMER_LAENGE + " Zeichen lang sein");
        }
        // Hier könnte man weitere spezifische Formatprüfungen (z.B. nur Ziffern) hinzufügen
    }

    /**
     * Validiert den Vornamen.
     */
    private void validateVorname(String vorname, ValidationResult result) {
        if (vorname == null || vorname.trim().isEmpty()) {
            result.addError("Vorname darf nicht leer sein");
            return;
        }
        String trimmedVorname = vorname.trim();
        if (trimmedVorname.length() < MIN_NAME_LAENGE || trimmedVorname.length() > MAX_NAME_LAENGE) {
            result.addError("Vorname muss zwischen " + MIN_NAME_LAENGE + " und " + MAX_NAME_LAENGE + " Zeichen lang sein");
        }
    }

    /**
     * Validiert den Nachnamen.
     */
    private void validateNachname(String nachname, ValidationResult result) {
        if (nachname == null || nachname.trim().isEmpty()) {
            result.addError("Nachname darf nicht leer sein");
            return;
        }
        String trimmedNachname = nachname.trim();
        if (trimmedNachname.length() < MIN_NAME_LAENGE || trimmedNachname.length() > MAX_NAME_LAENGE) {
            result.addError("Nachname muss zwischen " + MIN_NAME_LAENGE + " und " + MAX_NAME_LAENGE + " Zeichen lang sein");
        }
    }

    /**
     * Validiert die E-Mail-Adresse.
     */
    private void validateEmail(String email, ValidationResult result) {
        if (email == null || email.trim().isEmpty()) {
            result.addError("E-Mail darf nicht leer sein");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError("E-Mail-Adresse ist ungültig formatiert");
        }
    }

    /**
     * Validiert das Geburtsdatum.
     */
    private void validateGeburtsdatum(LocalDate geburtsdatum, ValidationResult result) {
        if (geburtsdatum == null) {
            result.addError("Geburtsdatum darf nicht leer sein");
            return;
        }

        // Prüfung, ob das Geburtsdatum in der Zukunft liegt
        if (geburtsdatum.isAfter(LocalDate.now())) {
            result.addError("Geburtsdatum darf nicht in der Zukunft liegen");
        }

        // Prüfung auf Mindestalter (optional, z.B. mindestens 16 Jahre)
        if (geburtsdatum.isAfter(LocalDate.now().minusYears(MIN_ALTER_STUDIERNDE))) {
            result.addError("Studierende müssen mindestens " + MIN_ALTER_STUDIERNDE + " Jahre alt sein");
        }
    }

    /**
     * Validiert die Adresse
     */
    private void validateAdresse(String adresse, ValidationResult result) {
        if (adresse == null || adresse.trim().isEmpty()) {
            result.addError("Adresse darf nicht leer sein");
        }
        if (!ADDRESS_PATTERN.matcher(adresse).matches()) {
            result.addError("Adresee entspricht nicht dem richtigen Format");
        }
    }

    /**
     * Schnelle Validierung nur für UPDATE-Operationen
     * (prüft nur Felder, die aktualisiert werden können, zzgl. ID-Prüfung)
     */
    public ValidationResult validateForUpdate(Studierende studi) {
        ValidationResult result = new ValidationResult();

        if (studi == null) {
            result.addError("Studierenden-Objekt darf nicht null sein");
            return result;
        }

        if (studi.getStudierendenId() <= 0) {
            result.addError("Studierenden-ID muss für Update gesetzt sein");
        }

        // Die meisten Felder können aktualisiert werden
        validateMatrikelnummer(studi.getMatrikelnummer(), result);
        validateVorname(studi.getVorname(), result);
        validateNachname(studi.getNachname(), result);
        validateEmail(studi.getEmail(), result);
        validateGeburtsdatum(studi.getGeburtsdatum(), result);
        validateAdresse(studi.getAdresse(), result);

        return result;
    }
}