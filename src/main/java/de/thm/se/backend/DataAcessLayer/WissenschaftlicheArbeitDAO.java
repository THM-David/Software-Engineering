package de.thm.se.backend.DataAcessLayer;

import java.lang.Thread.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public int create (WissenschaftlicheArbeit arbeit) throws SQLException {
        String sql = """
                INSERT INTO WISSENSCHAFTLICHE_ARBEIT
                (studierenden_id, studiengang_id, pruefungsordnung_id, semester_id, titel, typ, status)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS )    
        )
    }
}
