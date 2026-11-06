package com.evoxspace.repository.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {
    @Test
    void abreConexionH2Local() throws Exception {
        try (Connection connection = DatabaseConnection.open()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void aplicaEsquemaDeBaseDeDatos() throws Exception {
        try (Connection connection = java.sql.DriverManager.getConnection("jdbc:h2:mem:evox_schema")) {
            DatabaseInitializer.initialize(connection);
            try (ResultSet result = connection.getMetaData().getTables(null, null, "CLIENTES", null)) {
                assertTrue(result.next());
            }
        }
    }
}
