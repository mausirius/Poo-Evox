package com.evoxspace.repository.jdbc;

import com.evoxspace.model.EstadoReparacion;
import com.evoxspace.repository.OrdenReparacionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class JdbcOrdenReparacionRepository implements OrdenReparacionRepository {
    private final Connection connection;

    public JdbcOrdenReparacionRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public EstadoReparacion consultarEstado(UUID ordenId) throws SQLException {
        String sql = "SELECT estado FROM ordenes_reparacion WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ordenId.toString());
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;
                return EstadoReparacion.valueOf(result.getString("estado"));
            }
        }
    }
}
