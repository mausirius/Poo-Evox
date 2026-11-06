package com.evoxspace.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection open() throws SQLException {
        String url = System.getenv().getOrDefault(
                "EVOX_DB_URL",
                "jdbc:h2:file:./data/evoxspace;AUTO_SERVER=TRUE"
        );
        String user = System.getenv().getOrDefault("EVOX_DB_USER", "sa");
        String password = System.getenv().getOrDefault("EVOX_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
}
