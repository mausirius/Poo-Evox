package com.evoxspace.repository.jdbc;

import com.evoxspace.exception.ClienteNoEncontradoException;
import com.evoxspace.model.Cliente;
import com.evoxspace.repository.ClienteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class JdbcClienteRepository implements ClienteRepository {
    private final Connection connection;

    public JdbcClienteRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void guardar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (id, nombre, email, password_hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getId().toString());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getEmail());
            statement.setString(4, cliente.getPasswordHash());
            statement.executeUpdate();
        }
    }

    @Override
    public Cliente buscarPorEmail(String email) throws SQLException, ClienteNoEncontradoException {
        String sql = "SELECT nombre, email, password_hash FROM clientes WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.toLowerCase());
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new ClienteNoEncontradoException("No existe cliente con correo " + email);
                return new Cliente(result.getString("nombre"), result.getString("email"), result.getString("password_hash"));
            }
        }
    }
}
