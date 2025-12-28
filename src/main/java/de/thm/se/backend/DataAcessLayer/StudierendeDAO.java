package de.thm.se.backend.DataAcessLayer;

import de.thm.se.backend.datavalidation.StudierendeValidator;
import de.thm.se.backend.datavalidation.ValidationResult;
import de.thm.se.backend.model.Studierende;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class StudierendeDAO {

    private final StudierendeValidator validator;

    public  StudierendeDAO() {
        validator = new StudierendeValidator();
    }

    // CREATE - Neuen Studierenden anlegen
    public int create(Studierende studi) throws SQLException {

        ValidationResult validationResult = validator.validate(studi);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        String sql = """
                INSERT INTO STUDIERENDE
                (matrikelnummer, vorname, nachname, email, geburtsdatum, adresse)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studi.getMatrikelnummer());
            pstmt.setString(2, studi.getVorname());
            pstmt.setString(3, studi.getNachname());
            pstmt.setString(4, studi.getEmail());
            pstmt.setDate(5, java.sql.Date.valueOf(studi.getGeburtsdatum()));
            pstmt.setString(6, studi.getAdresse());

            pstmt.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Erstellen fehlgeschlagen, keine ID erhalten (last_insert_rowid() fehlgeschlagen)");
            }
        }
    }

    // READ - Studierender nach ID
    public Optional<Studierende> findById(int studiId) throws SQLException {
        String sql = "SELECT * FROM STUDIERENDE WHERE studierenden_id = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studiId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Studierende studi = mapResultSet(rs);
                DatabaseConnection.closeResultSet(rs);
                return Optional.of(studi);
            }
            DatabaseConnection.closeResultSet(rs);
            return Optional.empty();
        }
    }

    // READ - Alle Studierende
    public List<Studierende> findAll() throws SQLException {
        String sql = "SELECT * FROM STUDIERENDE";
        List<Studierende> studi = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                studi.add(mapResultSet(rs));
            }
            DatabaseConnection.closeResultSet(rs);
        }
        return studi;
    }

    // READ - Studierender nach Matrikelnummer
    public Optional<Studierende> findByMatrikelnummer(String matrikelnummer) throws SQLException {
        String sql = "SELECT * FROM STUDIERENDE WHERE matrikelnummer = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matrikelnummer);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Studierende studi = mapResultSet(rs);
                DatabaseConnection.closeResultSet(rs);
                return Optional.of(studi);
            }
            DatabaseConnection.closeResultSet(rs);
            return Optional.empty();
        }
    }

    // UPDATE - Studierende aktualisieren
    public boolean update(Studierende studi) throws SQLException {

        ValidationResult validationResult = validator.validateForUpdate(studi);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        String sql = """
                UPDATE STUDIERENDE
                SET matrikelnummer = ?, vorname = ?, nachname = ?, email = ?, geburtsdatum = ?, adresse = ?
                WHERE studierenden_Id = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studi.getMatrikelnummer());
            pstmt.setString(2, studi.getVorname());
            pstmt.setString(3, studi.getNachname());
            pstmt.setString(4, studi.getEmail());
            pstmt.setDate(5, java.sql.Date.valueOf(studi.getGeburtsdatum()));
            pstmt.setString(6, studi.getAdresse());
            pstmt.setInt(6, studi.getStudierendenId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // DELETE - Studierenden löschen
    public boolean delete(int studierendenId) throws SQLException {
        String sql = "DELETE FROM STUDIERENDE WHERE studierenden_id = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studierendenId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Hilfsmethode: ResultSet in Objekt umwandeln
    private Studierende mapResultSet(ResultSet rs) throws SQLException {
        Studierende studi = new Studierende();
        studi.setStudierendenId(rs.getInt("studierenden_id"));
        studi.setMatrikelnummer(rs.getString("matrikelnummer"));
        studi.setVorname(rs.getString("vorname"));
        studi.setNachname(rs.getString("nachname"));
        studi.setEmail(rs.getString("email"));
        studi.setGeburtsdatum(rs.getDate("geburtsdatum").toLocalDate());
        studi.setAdresse(rs.getString("adresse"));
        return studi;
    }
}
