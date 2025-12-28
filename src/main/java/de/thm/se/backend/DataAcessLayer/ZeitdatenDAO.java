package de.thm.se.backend.DataAcessLayer;

import de.thm.se.backend.datavalidation.ValidationResult;
import de.thm.se.backend.datavalidation.ZeitdatenValidator;
import de.thm.se.backend.model.Zeitdaten;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class ZeitdatenDAO {

    private final ZeitdatenValidator validator;

    public ZeitdatenDAO() {
        this.validator = new ZeitdatenValidator();
    }

    // CREATE - Neue Zeitdaten anlegen
    public int create(Zeitdaten zeit) throws SQLException {
        ValidationResult validationResult = validator.validate(zeit);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        String sql = """
                INSERT INTO ZEITDATEN
                (arbeit_id, anfangsdatum, abgabedatum, kolloquiumsdatum)
                VALUES(?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zeit.getArbeitId());

            // Anfangsdatum
            if (zeit.getAnfangsdatum() != null) {
                pstmt.setDate(2, java.sql.Date.valueOf(zeit.getAnfangsdatum()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }

            // Abgabedatum
            if (zeit.getAbgabedatum() != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(zeit.getAbgabedatum()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            // Kolloquiumsdatum ist optional
            if (zeit.getKolloquiumsdatum() != null) {
                pstmt.setDate(4, java.sql.Date.valueOf(zeit.getKolloquiumsdatum()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Erstellen fehlgeschlagen, keine ID erhalten.");
            }
        }
    }

    // READ - Zeitdaten nach ID
    public Optional<Zeitdaten> findById(int zeitId) throws SQLException {
        String sql = "SELECT * FROM ZEITDATEN WHERE zeit_id = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zeitId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Zeitdaten zeit = mapResultSet(rs);
                DatabaseConnection.closeResultSet(rs);
                return Optional.of(zeit);
            }
            DatabaseConnection.closeResultSet(rs);
            return Optional.empty();
        }
    }

    // READ - Alle Zeitdaten
    public List<Zeitdaten> findAll() throws SQLException {
        String sql = "SELECT * FROM ZEITDATEN";
        List<Zeitdaten> zeit = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                zeit.add(mapResultSet(rs));
            }
            DatabaseConnection.closeResultSet(rs);
        }
        return zeit;
    }

    // UPDATE - Zeitdaten aktualisieren
    public boolean update(Zeitdaten zeit) throws SQLException {
        ValidationResult validationResult = validator.validateForUpdate(zeit);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        String sql = """
                UPDATE ZEITDATEN
                SET arbeit_id = ?, anfangsdatum = ?, abgabedatum = ?, kolloquiumsdatum = ?
                WHERE zeit_id = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zeit.getArbeitId());

            // Anfangsdatum
            if (zeit.getAnfangsdatum() != null) {
                pstmt.setDate(2, java.sql.Date.valueOf(zeit.getAnfangsdatum()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }

            // Abgabedatum
            if (zeit.getAbgabedatum() != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(zeit.getAbgabedatum()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            // Kolloquiumsdatum ist optional
            if (zeit.getKolloquiumsdatum() != null) {
                pstmt.setDate(4, java.sql.Date.valueOf(zeit.getKolloquiumsdatum()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setInt(5, zeit.getZeitId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // DELETE - Zeitdaten löschen
    public boolean delete(int zeitId) throws SQLException {
        String sql = "DELETE FROM ZEITDATEN WHERE zeit_id = ?";
        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zeitId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Hilfsmethode: ResultSet in Objekt umwandeln
    private Zeitdaten mapResultSet(ResultSet rs) throws SQLException {
        Zeitdaten zeit = new Zeitdaten();
        zeit.setZeitId(rs.getInt("zeit_id"));
        zeit.setArbeitId(rs.getInt("arbeit_id"));
        // Anfangsdatum
        Date anfangsdatum = rs.getDate("anfangsdatum");
        if (anfangsdatum != null) {
            zeit.setAnfangsdatum(anfangsdatum.toLocalDate());
        }

        // Abgabedatum
        Date abgabedatum = rs.getDate("abgabedatum");
        if (abgabedatum != null) {
            zeit.setAbgabedatum(abgabedatum.toLocalDate());
        }

        // Kolloquiumsdatum (optional)
        Date kolloquiumsdatum = rs.getDate("kolloquiumsdatum");
        if (kolloquiumsdatum != null) {
            zeit.setKolloquiumsdatum(kolloquiumsdatum.toLocalDate());
        }
        return zeit;
    }
}
