package com.evoxspace;

import com.evoxspace.exception.ClienteNoEncontradoException;
import com.evoxspace.model.Cliente;
import com.evoxspace.model.EstadoReparacion;
import com.evoxspace.repository.jdbc.DatabaseConnection;
import com.evoxspace.repository.jdbc.DatabaseInitializer;
import com.evoxspace.repository.jdbc.JdbcClienteRepository;
import com.evoxspace.repository.jdbc.JdbcOrdenReparacionRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;

public final class EvoxSpaceApplication {
    private EvoxSpaceApplication() {
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("          EVOX SPACE - TALLER EV");
        System.out.println("========================================");

        try (Connection connection = DatabaseConnection.open();
             Scanner scanner = new Scanner(System.in)) {
            DatabaseInitializer.initialize(connection);
            System.out.println("Base de datos inicializada correctamente.");
            if (args.length > 0 && "--demo".equals(args[0])) {
                ejecutarDemo(connection);
            } else {
                ejecutarMenu(connection, scanner);
            }
        } catch (SQLException | IOException exception) {
            System.err.println("No fue posible iniciar EVOX SPACE: " + exception.getMessage());
            System.exit(1);
        }
    }

    private static void ejecutarDemo(Connection connection) throws SQLException {
        try (var statement = connection.createStatement();
             var result = statement.executeQuery("SELECT COUNT(*) AS total FROM clientes")) {
            result.next();
            System.out.println("Modo demo activo.");
            System.out.println("Clientes registrados: " + result.getInt("total"));
            System.out.println("EVOX SPACE inició correctamente.");
        }
    }

    private static void ejecutarMenu(Connection connection, Scanner scanner) throws SQLException {
        JdbcClienteRepository clientes = new JdbcClienteRepository(connection);
        JdbcOrdenReparacionRepository ordenes = new JdbcOrdenReparacionRepository(connection);

        boolean continuar = true;
        while (continuar) {
            System.out.println();
            System.out.println("1. Registrar cliente");
            System.out.println("2. Buscar cliente por correo");
            System.out.println("3. Consultar estado de orden");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> registrarCliente(clientes, scanner);
                case "2" -> buscarCliente(clientes, scanner);
                case "3" -> consultarEstado(ordenes, scanner);
                case "0" -> continuar = false;
                default -> System.out.println("Opción no válida.");
            }
        }
        System.out.println("EVOX SPACE finalizado.");
    }

    private static void registrarCliente(JdbcClienteRepository clientes, Scanner scanner) throws SQLException {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Correo: ");
        String email = scanner.nextLine();
        System.out.print("Contraseña (demo): ");
        String password = scanner.nextLine();

        Cliente cliente = new Cliente(nombre, email, password);
        clientes.guardar(cliente);
        System.out.println("Cliente registrado con ID: " + cliente.getId());
    }

    private static void buscarCliente(JdbcClienteRepository clientes, Scanner scanner) {
        System.out.print("Correo: ");
        try {
            Cliente cliente = clientes.buscarPorEmail(scanner.nextLine());
            System.out.println("Cliente encontrado: " + cliente.getNombre() + " (" + cliente.getEmail() + ")");
        } catch (SQLException | ClienteNoEncontradoException exception) {
            System.out.println("No se encontró el cliente: " + exception.getMessage());
        }
    }

    private static void consultarEstado(JdbcOrdenReparacionRepository ordenes, Scanner scanner)
            throws SQLException {
        System.out.print("ID de la orden: ");
        UUID ordenId;
        try {
            ordenId = UUID.fromString(scanner.nextLine().trim());
        } catch (IllegalArgumentException exception) {
            System.out.println("El ID debe ser un UUID válido.");
            return;
        }

        EstadoReparacion estado = ordenes.consultarEstado(ordenId);
        if (estado == null) {
            System.out.println("No existe una orden con ese ID.");
        } else {
            System.out.println("Estado de la orden: " + estado);
        }
    }
}
