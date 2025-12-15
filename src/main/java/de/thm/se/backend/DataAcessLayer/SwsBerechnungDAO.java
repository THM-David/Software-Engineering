package de.thm.se.backend.DataAcessLayer;

import de.thm.se.backend.datavalidation.SwsBerechnungValidator;
import de.thm.se.backend.datavalidation.ValidationResult;
import de.thm.se.backend.model.SwsBerechnung;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SwsBerechnungDAO {

    private final SwsBerechnungValidator validator;

    public SwsBerechnungDAO() {
        this.validator = new SwsBerechnungValidator();
    }

    // CREATE - Neue SWS Berechnung
    public int create(SwsBerechnung swsBerechnung) throws SQLException {

        ValidationResult validationResult = validator.validate(swsBerechnung);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        String sql = """
                INSERT INTO SWS_BERECHNUNG
                (arbeit_id, betreuer_id, semester_id, pruefungsordnung_id, sws_wert, rolle, berechnet_am)
                """;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, swsBerechnung.getArbeitId());
            pstmt.setInt(2, swsBerechnung.getBetreuerId());
            pstmt.setInt(3, swsBerechnung.getSemesterId());
            pstmt.setInt(4, swsBerechnung.getPruefungsordnungId());
            pstmt.setFloat(5, swsBerechnung.getSwsWert());
            pstmt.setString(6, swsBerechnung.getRolle());
            // Berechnungsdatum
            if (swsBerechnung.getBerechnetAm() != null) {
                pstmt.setDate(7, java.sql.Date.valueOf(swsBerechnung.getBerechnetAm()));
            } else {
                pstmt.setNull(7, Types.DATE);
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

    // READ - SWS Berechnung nach ID
    public Optional<SwsBerechnung> findById(int swsId) throws SQLException {
        String sql = "SELECT * FROM SWS_BERECHNUNG WHERE sws_id = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, swsId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                SwsBerechnung sws = mapResultSet(rs);
                DatabaseConnection.closeResultSet(rs);
                return Optional.of(sws);
            }
            DatabaseConnection.closeResultSet(rs);
            return Optional.empty();
        }
    }

    // READ - Alle SWS Berechnungen
    public List<SwsBerechnung> findAll() throws SQLException {
        String sql = "SELECT * FROM SWS_BERECHNUNG";
        List<SwsBerechnung> sws = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sws.add(mapResultSet(rs));
            }
            DatabaseConnection.closeResultSet(rs);
        }
        return sws;
    }

    // UPDATE - SWS Berechnung aktualisieren
    public boolean update(SwsBerechnung sws) throws SQLException {

        ValidationResult validationResult = validator.validateForUpdate(sws);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        String sql = """
                UPDATE SWS_BERECHNUNG
                SET arbeit_id = ?, betreuer_id = ?, semester_id = ?, pruefungsordnung_id = ?, sws_wert = ?, rolle = ?, berechnet_am = ?
                WHERE sws_id = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sws.getArbeitId());
            pstmt.setInt(2, sws.getBetreuerId());
            pstmt.setInt(3, sws.getSemesterId());
            pstmt.setInt(4, sws.getPruefungsordnungId());
            pstmt.setFloat(5, sws.getSwsWert());
            pstmt.setString(6, sws.getRolle());
            // Berechnungsdatum
            if (sws.getBerechnetAm() != null) {
                pstmt.setDate(7, java.sql.Date.valueOf(sws.getBerechnetAm()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setInt(8, sws.getSwsId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // DELETE - SWS Berechnung löschen
    public boolean delete(int swsId) throws SQLException {
        String sql = "DELETE FROM SWS_BERECHNUNG WHERE sws_id = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, swsId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Hilfsmethode: ResultSet in Objekt umwandeln
    private SwsBerechnung mapResultSet(ResultSet rs) throws SQLException {
        SwsBerechnung sws = new SwsBerechnung();
        sws.setSwsId(rs.getInt("sws_id"));
        sws.setArbeitId(rs.getInt("arbeit_id"));
        sws.setBetreuerId(rs.getInt("betreuer_id"));
        sws.setSemesterId(rs.getInt("semester_id"));
        sws.setPruefungsordnungId(rs.getInt("pruefungsordnung_id"));
        sws.setSwsWert(rs.getFloat("sws_wert"));
        sws.setRolle(rs.getString("rolle"));
        Date berechnetAm = rs.getDate("berechnet_am");
        if (berechnetAm != null) {
            sws.setBerechnetAm(berechnetAm.toLocalDate());
        }
        return sws;
    }
}
