package de.thm.se.backend.model;

import java.time.LocalDate;

/**
 * Model-Klasse für SemesterZeit
 * Entspricht der Tabelle SEMESTERZEIT
 */

public class Semesterzeit {
    private int semesterzeitId;
    private LocalDate beginn;
    private LocalDate ende;
    private String bezeichnung;

    // Konstruktoren
    public Semesterzeit() {
    }

    public Semesterzeit(LocalDate beginn, LocalDate ende, String bezeichnung) {
        this.beginn = beginn;
        this.ende = ende;
        this.bezeichnung = bezeichnung;
    }

    // Getter und Setter
    public int getSemesterzeitId() {
        return semesterzeitId;
    }

    public void setSemesterzeitId(int semesterZeitId) {
        this.semesterzeitId = semesterZeitId;
    }

    public LocalDate getBeginn() {
        return beginn;
    }

    public void setBeginn(LocalDate beginn) {
        this.beginn = beginn;
    }

    public LocalDate getEnde() {
        return ende;
    }

    public void setEnde(LocalDate ende) {
        this.ende = ende;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    // Hilfsmethoden
    @Override
    public String toString() {
        return String.format("Semesterzeit[ID=%d, Bezeichnung=&s", semesterzeitId, bezeichnung);
    }
}