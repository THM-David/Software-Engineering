package de.thm.se.backend.datavalidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zum Sammeln von Validierungsfehlern.
 * Ermöglicht das Hinzufügen mehrerer Fehler und die Prüfung,
 * ob die Validierung erfolgreich war.
 */
public class ValidationResult {
    private final List<String> errors;

    public ValidationResult() {
        this.errors = new ArrayList<>();
    }

    /**
     * Fügt einen Validierungsfehler hinzu
     * @param error Die Fehlermeldung
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * Prüft, ob die Validierung erfolgreich war (keine Fehler)
     * @return true wenn keine Fehler vorhanden sind
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Gibt alle gesammelten Fehler zurück
     * @return Liste der Fehlermeldungen
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Gibt alle Fehler als formatierten String zurück
     * @return Formatierte Fehlerliste
     */
    public String getErrorMessage() {
        if (isValid()) {
            return "Keine Fehler";
        }
        StringBuilder sb = new StringBuilder("Validierungsfehler:\n");
        for (int i = 0; i < errors.size(); i++) {
            sb.append((i + 1)).append(". ").append(errors.get(i)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getErrorMessage();
    }
}