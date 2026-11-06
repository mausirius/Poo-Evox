package com.evoxspace;

import com.evoxspace.repository.jdbc.DatabaseConnection;
import com.evoxspace.repository.jdbc.DatabaseInitializer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class DatosDemoGenerator {
    private static final String[] MODELOS = {"Evox S1", "Volt X", "Nexa E", "Orion EV", "Lumen GT"};
    private static final String[] FALLAS = {
            "Pérdida intermitente de potencia",
            "Advertencia del sistema de batería",
            "Ruido en el inversor",
            "Error de carga rápida",
            "Disminución de autonomía"
    };
    private static final String[] MECANICOS = {"Ana López", "Carlos Ruiz", "Sofía Méndez"};

    private DatosDemoGenerator() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection connection = DatabaseConnection.open()) {
            DatabaseInitializer.initialize(connection);
            connection.setAutoCommit(false);
            try {
                for (int index = 0; index < 5; index++) {
                    generarCaso(connection, index);
                }
                connection.commit();
                System.out.println("Se generaron 5 casos demo correctamente.");
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static void generarCaso(Connection connection, int index) throws SQLException {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String email = "cliente." + suffix + "@evoxspace.com";
        String nombre = "Cliente Demo " + (index + 1);
        String password = "demo" + (index + 1);
        String userId = UUID.randomUUID().toString();
        String vehicleId = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();
        String vin = generarVin();
        String modelo = MODELOS[index];
        String falla = FALLAS[index];
        String estado = new String[]{"EN_DIAGNOSTICO", "ESPERANDO_PIEZAS", "EN_REPARACION",
                "LISTO_PARA_ENTREGA"}[index % 4];
        BigDecimal labor = BigDecimal.valueOf(700L + index * 175L);
        BigDecimal parts = BigDecimal.valueOf(1200L + index * 260L);
        long quoteId;

        execute(connection, "INSERT INTO usuarios (id, nombre, email, password_hash, rol) "
                + "VALUES (?, ?, ?, ?, 'CLIENTE')", userId, nombre, email, password);
        execute(connection, "INSERT INTO clientes (id, nombre, email, password_hash) VALUES (?, ?, ?, ?)",
                userId, nombre, email, password);
        execute(connection, "INSERT INTO vehiculos (id, cliente_id, vin, marca, modelo, anio) "
                + "VALUES (?, ?, ?, 'Evox', ?, ?)", vehicleId, userId, vin, modelo,
                2022 + index);
        String mechanicId = obtenerMecanico(connection, index);
        execute(connection, "INSERT INTO ordenes_reparacion "
                + "(id, vehiculo_id, mecanico_id, estado, fecha_ingreso, fecha_entrega_estimada) "
                + "VALUES (?, ?, ?, ?, ?, ?)", orderId, vehicleId, mechanicId, estado,
                Date.valueOf(LocalDate.now().minusDays(index + 1)),
                Date.valueOf(LocalDate.now().plusDays(5 + index)));
        execute(connection, "INSERT INTO diagnosticos "
                + "(orden_id, codigo_dtc, tipo_falla, descripcion) VALUES (?, ?, 'OTRA', ?)",
                orderId, "DTC-" + (100 + index), falla);
        quoteId = insertarCotizacion(connection, orderId, labor, parts);
        execute(connection, "INSERT INTO cotizacion_detalles "
                + "(cotizacion_id, tipo, descripcion, costo, fecha_llegada_estimada) "
                + "VALUES (?, 'MANO_OBRA', ?, ?, ?)", quoteId, "Diagnóstico y reparación de " + falla,
                labor, Date.valueOf(LocalDate.now().plusDays(2)));
        execute(connection, "INSERT INTO cotizacion_detalles "
                + "(cotizacion_id, tipo, descripcion, costo, fecha_llegada_estimada) "
                + "VALUES (?, 'PIEZA', ?, ?, ?)", quoteId, "Refacción para " + modelo, parts,
                Date.valueOf(LocalDate.now().plusDays(4 + index)));
        execute(connection, "INSERT INTO piezas_refaccion "
                + "(orden_id, nombre, fecha_estimada_llegada) VALUES (?, ?, ?)", orderId,
                "Módulo compatible " + modelo, Date.valueOf(LocalDate.now().plusDays(4 + index)));
        long caseNumber = obtenerNumeroCaso(connection, orderId);
        System.out.println("Caso " + caseNumber + " | " + nombre + " | " + modelo + " | " + estado
                + " | acceso: " + email + " / " + password);
    }

    private static long obtenerNumeroCaso(Connection connection, String orderId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT numero_caso FROM ordenes_reparacion WHERE id = ?")) {
            statement.setString(1, orderId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new SQLException("No se pudo obtener el número del caso demo.");
                return result.getLong(1);
            }
        }
    }

    private static long insertarCotizacion(Connection connection, String orderId,
            BigDecimal labor, BigDecimal parts) throws SQLException {
        String sql = "INSERT INTO cotizaciones (orden_id, mano_de_obra, piezas) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql,
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, orderId);
            statement.setBigDecimal(2, labor);
            statement.setBigDecimal(3, parts);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No se pudo crear la cotización demo.");
                return keys.getLong(1);
            }
        }
    }

    private static String obtenerMecanico(Connection connection, int index) throws SQLException {
        String email = "mecanico.demo" + (index % MECANICOS.length) + "@evoxspace.com";
        String findSql = "SELECT id FROM mecanicos WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(findSql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return result.getString(1);
            }
        }
        String id = UUID.randomUUID().toString();
        execute(connection, "INSERT INTO mecanicos (id, nombre, email, password_hash, especialidad) "
                + "VALUES (?, ?, ?, 'demo', 'Vehículos eléctricos')",
                id, MECANICOS[index % MECANICOS.length], email);
        return id;
    }

    private static void execute(Connection connection, String sql, Object... values) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }
            statement.executeUpdate();
        }
    }

    private static String generarVin() {
        String characters = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
        StringBuilder vin = new StringBuilder(7);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (vin.length() < 7) {
            vin.append(characters.charAt(random.nextInt(characters.length())));
        }
        return vin.toString();
    }
}
