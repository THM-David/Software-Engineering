package de.thm.se.backend.DataAcessLayer;

import de.thm.se.backend.model.Notenbestandteil;
import de.thm.se.backend.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotenbestandteilDAO {
    //CREATE - Neuen Notenbestandteil

    public int create(Notenbestandteil note) throws SQLException {
        String sql = """
                INSERT INTO NOTENBESTANDTEIL
                (arbeit_id, betreuer_id, rolle, note_arbeit, note_kolloquium, gewichtung)
                VALUES(?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
                
                pstmt.setInt(1, note.getArbeitId());
                pstmt.setInt(2, note.getBetreuerId());
                pstmt.setString(3, note.getRolle());
                pstmt.setDouble(4, note.getNoteArbeit());
                pstmt.setDouble(5, note.getNoteKolloquium());
                pstmt.setDouble(6, note.getGewichtung());

                pstmt.executeUpdate();

                try (ResultSet generateKeys = pstmt.getGeneratedKeys()) {
                    if(generateKeys.next()) {
                        return generateKeys.getInt(1);
                    }
                    throw new SQLException("Erstellen fehlgeschlagen, keine ID erhalten");
                }
            }
    }

    
}
