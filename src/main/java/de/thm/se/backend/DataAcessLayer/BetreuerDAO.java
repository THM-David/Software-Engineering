package de.thm.se.backend.DataAcessLayer;

import de.thm.se.backend.model.Betreuer;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class BetreuerDAO {

    //CREATE - Neuen Betreuer anlegen
    public int create(Betreuer betreuer) throws SQLException {
        String sql = """
                INSERT INTO BETREUER
                (vorname = ?, nachname = ?, titel = ?, email = ?, rolle = ?)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){ 

                pstmt.setString(1, betreuer.getVorname());
                pstmt.setString(2, betreuer.getNachname());
                pstmt.setString(3, betreuer.getTitel());
                pstmt.setString(4, betreuer.getEmail());
                pstmt.setString(5, betreuer.getRolle());

                pstmt.executeUpdate();

                try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
                    if (generatedKeys.next()){
                        return generatedKeys.getInt(1);
                    }
                    throw new SQLException("Erstellen fehlgeschlagen, keine ID erhalten");
                }
        }
    }

    //READ - Betreuer nach ID
    
    //UPDATE - Betreuer aktualisieren
    public boolean update(Betreuer betreuer) throws SQLException {
        String sql = """
                UPDATE BETREUER
                SET vorname = ?, nachname = ?, titel = ?, email = ?, rolle = ?
                WHERE betreuer_id = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

                pstmt.setString(1, betreuer.getVorname());
                pstmt.setString(2, betreuer.getNachname());
                pstmt.setString(3, betreuer.getTitel());
                pstmt.setString(4, betreuer.getEmail());
                pstmt.setString(5, betreuer.getRolle());
                pstmt.setInt(6, betreuer.getBetreuerId());

                return pstmt.executeUpdate() > 0;
            }
    }

    //DELETE - Betreuer löschen
    //public boolean delete() throws SQLException {}

    //Hilfsmethode: ResultSet in Objekt umwandeln
    private Betreuer mapResultSet(ResultSet rs) throws SQLException {
        Betreuer betreuer = new Betreuer();
        betreuer.setBetreuerId(rs.getInt("betreuer_id"));
        betreuer.setVorname(rs.getString("vorname"));
        betreuer.setNachname(rs.getString("nachname"));
        betreuer.setTitel(rs.getString("titel"));
        betreuer.setEmail(rs.getString("email"));
        betreuer.setRolle(rs.getString("rolle"));
        return betreuer;
    }
}
