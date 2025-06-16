package com.example.demoexam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    String url = "jdbc:postgresql://localhost:5432/demoEXAM";
    String user = "postgres";
    String password = "0000";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
