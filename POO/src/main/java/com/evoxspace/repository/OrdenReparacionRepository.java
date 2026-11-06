package com.evoxspace.repository;

import com.evoxspace.model.EstadoReparacion;

import java.sql.SQLException;
import java.util.UUID;

public interface OrdenReparacionRepository {
    EstadoReparacion consultarEstado(UUID ordenId) throws SQLException;
}
