package de.thm.se.backend.DataAcessLayer;

import java.lang.Thread.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class WissenschaftlicheArbeitDAO {
    private static final String DB_URL = " "; //URL einfügen
    
    //Datenbankverbindung
    private Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        stmt.execute("PRAGMA foreign_keys = ON");
        return conn;
    }

    //CREATE - Neue Arbeit anlegen
    public int create(WissenschaftlicheArbeit arbeit) throws SQLException {
        String sql = """
            INSERT INTO WISSENSCHAFTLICHE_ARBEIT 
            (studierenden_id, studiengang_id, pruefungsordnung_id, semester_id, titel, typ, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
            pstmt.setInt(1, arbeit.getStudierendenId());
            pstmt.setInt(2, arbeit.getStudiengangId());
            pstmt.setInt(3, arbeit.getPruefungsordnungId());
            pstmt.setInt(4, arbeit.getSemesterId());
            pstmt.setString(5, arbeit.getTitel());
            pstmt.setString(6, arbeit.getTyp());
            pstmt.setString(7, arbeit.getStatus());
            
            pstmt.executeUpdate();
        
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            throw new SQLException("Erstellen fehlgeschlagen, keine ID erhalten.");
            }
        }
    }

    //READ -Arbeit nach ID Suchen 
    public Optional<WissenschaftlicheArbeit> findById(int id) throws SQLException{
        String sql = "SELECT * FROM WISSENSCHAFTLICHE_ARBEIT WHERE arbeit_id = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();

                if (re.next()) {
                    return Optional.of(mapResultset(rs));
                }
                return Optional.empty();
        }
    }

    //READ - Alle Arbeiten eines Studierenden
    public List<WissenschaftlicheArbeit> findByStudierendenId(String status) throws SQLException {
        String sql = "SELECT * FROM WISSENSCHAFTLICHE_ARBEIT WHERE studierenden_id = ?";
        List<WissenschaftlicheArbeit> arbeiten = new ArrayList<>();

        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, studierenden_id);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    arbeiten.add(mapResultset(rs));
                }
        }
        return arbeiten;
    }
}