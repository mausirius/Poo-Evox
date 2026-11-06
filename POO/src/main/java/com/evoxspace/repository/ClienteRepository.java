package com.evoxspace.repository;

import com.evoxspace.exception.ClienteNoEncontradoException;
import com.evoxspace.model.Cliente;

import java.sql.SQLException;

public interface ClienteRepository {
    void guardar(Cliente cliente) throws SQLException;
    Cliente buscarPorEmail(String email) throws SQLException, ClienteNoEncontradoException;
}
