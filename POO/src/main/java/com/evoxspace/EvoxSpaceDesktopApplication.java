package com.evoxspace;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.awt.Desktop;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class EvoxSpaceDesktopApplication {
    private static final Color DARK = new Color(18, 30, 45);
    private static final Color ACCENT = new Color(24, 164, 143);
    private static final Color BACKGROUND = new Color(242, 247, 249);

    private EvoxSpaceDesktopApplication() {
    }

    public static void main(String[] args) {
        configurarApariencia();
        SwingUtilities.invokeLater(EvoxSpaceDesktopApplication::mostrarVentana);
    }

    private static void configurarApariencia() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("TabbedPane.background", Color.WHITE);
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
        UIManager.put("Table.gridColor", new Color(225, 233, 236));
        UIManager.put("Table.selectionBackground", new Color(204, 239, 232));
        UIManager.put("Table.selectionForeground", DARK);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }

    private static void mostrarVentana() {
        JFrame frame = new JFrame("EVOX SPACE - Taller de Vehículos Eléctricos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);

        CardLayout cards = new CardLayout();
        JPanel content = new JPanel(cards);
        try {
            Connection connection = com.evoxspace.repository.jdbc.DatabaseConnection.open();
            com.evoxspace.repository.jdbc.DatabaseInitializer.initialize(connection);
            content.add(crearLogin(connection, content, cards), "login");
            content.add(crearDashboard(connection, content, cards), "dashboard");
            content.add(crearAdminDashboard(connection, content, cards), "admin-dashboard");
            content.add(crearMecanicoDashboard(connection, content, cards), "mechanic-dashboard");
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent event) {
                    try {
                        connection.close();
                    } catch (SQLException exception) {
                        System.err.println("No se pudo cerrar la conexión: " + exception.getMessage());
                    }
                }
            });
        } catch (SQLException | java.io.IOException exception) {
            JOptionPane.showMessageDialog(frame, "No se pudo abrir la base de datos: " + exception.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }
        frame.add(content);
        frame.setVisible(true);
    }

    private static JPanel crearLogin(Connection connection, JPanel content, CardLayout cards) {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(DARK);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(62, 207, 183), 1),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel logo = new JLabel("EVOX SPACE", SwingConstants.CENTER);
        logo.setForeground(ACCENT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 30));
        c.gridy = 0;
        card.add(logo, c);

        JLabel subtitle = new JLabel("Gestión inteligente para tu vehículo eléctrico", SwingConstants.CENTER);
        subtitle.setForeground(DARK);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        c.gridy++;
        card.add(subtitle, c);

        JTextField email = new JTextField("demo@evoxspace.com", 22);
        JPasswordField password = new JPasswordField("demo", 22);
        c.gridy++;
        card.add(new JLabel("Correo electrónico"), c);
        c.gridy++;
        card.add(email, c);
        c.gridy++;
        card.add(new JLabel("Contraseña"), c);
        c.gridy++;
        card.add(password, c);

        JLabel message = new JLabel(" ", SwingConstants.CENTER);
        message.setForeground(new Color(190, 50, 50));
        c.gridy++;
        card.add(message, c);

        JButton login = new JButton("Iniciar sesión");
        estilizarBotonPrincipal(login);
        login.addActionListener(event -> {
            String enteredEmail = email.getText().trim().toLowerCase();
            String enteredPassword = new String(password.getPassword());
            if (enteredEmail.isBlank() || enteredPassword.isBlank()) {
                message.setText("Ingresa correo y contraseña.");
            } else {
                String role = autenticar(connection, enteredEmail, enteredPassword);
                if ("ADMINISTRADOR".equals(role)) {
                    cards.show(content, "admin-dashboard");
                } else if ("MECANICO".equals(role)) {
                    cards.show(content, "mechanic-dashboard");
                } else if ("CLIENTE".equals(role)) {
                    content.remove(1);
                    content.add(crearDashboard(connection, content, cards, enteredEmail), "dashboard");
                    content.revalidate();
                    content.repaint();
                    cards.show(content, "dashboard");
                } else {
                    message.setText("Correo o contraseña incorrectos.");
                }
            }
        });
        c.gridy++;
        card.add(login, c);

        JButton register = new JButton("Crear nueva cuenta");
        estilizarBotonSecundario(register);
        register.addActionListener(event -> registrarCuenta(connection));
        c.gridy++;
        card.add(register, c);

        JLabel hint = new JLabel(
                "<html><center>Cliente: demo@evoxspace.com / demo<br>Administrador: admin@evoxspace.com / admin</center></html>",
                SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        c.gridy++;
        card.add(hint, c);

        page.add(card);
        return page;
    }

    private static JPanel crearDashboard(Connection connection, JPanel content, CardLayout cards) {
        return crearDashboard(connection, content, cards, "demo@evoxspace.com");
    }

    private static JPanel crearDashboard(Connection connection, JPanel content, CardLayout cards,
            String clientEmail) {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Dashboard del cliente");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DARK);
        header.add(title, BorderLayout.WEST);

        JButton logout = new JButton("Cerrar sesión");
        estilizarBotonSecundario(logout);
        logout.addActionListener(event -> cards.show(content, "login"));
        header.add(logout, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);
        JLabel welcome = new JLabel("Hola, Cliente Demo | Última actualización: hoy");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 16));
        body.add(welcome, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 16, 16));
        cardsPanel.setOpaque(false);
        cardsPanel.add(infoCard("VEHÍCULO", "Evox S1 · 2025", "VIN: 1HGBH41JXMN109186"));
        cardsPanel.add(infoCard("ESTADO ACTUAL", "En reparación", "Diagnóstico completado"));
        cardsPanel.add(infoCard("COTIZACIÓN", "$2,500.00 MXN", "Mano de obra: $1,000 · Piezas: $1,500"));
        cardsPanel.add(infoCard("TÉCNICO ASIGNADO", "Ana López", "Especialidad: sistemas de alto voltaje"));
        body.add(cardsPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.add(new JLabel("Refacción: Inversor EV · Llegada estimada: 12/11/2026"));
        body.add(footer, BorderLayout.SOUTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.addTab("Resumen", body);
        tabs.addTab("Mensajes", crearMensajesCliente(connection, clientEmail));
        tabs.addTab("Cotizaciones", crearCotizacionesClienteTab(connection, clientEmail));
        tabs.addTab("Documentos", crearDocumentosClienteTab(connection, clientEmail));
        page.add(tabs, BorderLayout.CENTER);

        return page;
    }

    private static JPanel crearAdminDashboard(Connection connection, JPanel content, CardLayout cards) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BACKGROUND);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(DARK);
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 18, 16));
        sidebar.setPreferredSize(new Dimension(235, 0));
        JLabel logo = new JLabel("<html><b>EVOX</b> SPACE<br><small>CONTROL CENTER</small></html>");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        sidebar.add(logo, BorderLayout.NORTH);

        JPanel navigation = new JPanel(new GridLayout(8, 1, 0, 7));
        navigation.setOpaque(false);
        JPanel views = new JPanel(new CardLayout());
        views.setBackground(BACKGROUND);
        String[] names = {"Resumen", "Clientes", "Cuentas y roles", "Solicitudes nuevas",
                "Órdenes de reparación", "Cotizaciones", "Documentos", "Mensajes"};
        JPanel[] panels = {
                crearResumenAdmin(connection, views),
                crearClientesTab(connection),
                crearUsuariosTab(connection),
                crearSolicitudesTab(connection),
                crearOrdenesTab(connection),
                crearCotizacionesAdminTab(connection),
                crearDocumentosAdminTab(connection),
                crearMensajesAdmin(connection)
        };
        CardLayout viewLayout = (CardLayout) views.getLayout();
        for (int index = 0; index < names.length; index++) {
            String name = names[index];
            views.add(panels[index], name);
            JButton item = crearBotonSidebar(name);
            item.addActionListener(event -> viewLayout.show(views, name));
            navigation.add(item);
        }
        sidebar.add(navigation, BorderLayout.CENTER);
        JButton logout = new JButton("Cerrar sesión");
        estilizarBotonSidebar(logout);
        logout.addActionListener(event -> cards.show(content, "login"));
        sidebar.add(logout, BorderLayout.SOUTH);

        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setBackground(BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(24, 30, 28, 30));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Panel de administración");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DARK);
        header.add(title, BorderLayout.WEST);
        JLabel status = new JLabel("EVOX SPACE · Gestión operativa");
        status.setForeground(Color.GRAY);
        header.add(status, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);
        main.add(views, BorderLayout.CENTER);
        page.add(sidebar, BorderLayout.WEST);
        page.add(main, BorderLayout.CENTER);
        return page;
    }

    private static JPanel crearMecanicoDashboard(Connection connection, JPanel content, CardLayout cards) {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(25, 35, 30, 35));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Panel del mecánico");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DARK);
        header.add(title, BorderLayout.WEST);
        JButton logout = new JButton("Cerrar sesión");
        estilizarBotonSecundario(logout);
        logout.addActionListener(event -> cards.show(content, "login"));
        header.add(logout, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        JLabel welcome = new JLabel("Órdenes asignadas | Consulta el vehículo y actualiza el avance del trabajo");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 16));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "VIN", "Cliente", "Estado", "Ingreso", "Entrega estimada"}, 0);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        estilizarTabla(table);
        cargarOrdenes(connection, model);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton refresh = new JButton("Actualizar órdenes");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarOrdenes(connection, model));
        JButton update = new JButton("Actualizar estado");
        estilizarBotonPrincipal(update);
        update.addActionListener(event -> actualizarEstadoOrden(connection, table, model));
        actions.add(refresh);
        actions.add(update);
        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);
        controls.add(welcome, BorderLayout.NORTH);
        controls.add(actions, BorderLayout.SOUTH);
        body.add(controls, BorderLayout.NORTH);
        body.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel help = new JLabel("Estados disponibles: EN_DIAGNOSTICO, ESPERANDO_PIEZAS, EN_REPARACION, LISTO_PARA_ENTREGA");
        help.setForeground(Color.DARK_GRAY);
        body.add(help, BorderLayout.SOUTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.addTab("Órdenes", body);
        tabs.addTab("Mensajes de clientes", crearMensajesMecanico(connection,
                "mecanico.demo0@evoxspace.com"));
        page.add(tabs, BorderLayout.CENTER);
        return page;
    }

    private static void actualizarEstadoOrden(Connection connection, JTable table, DefaultTableModel model) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(null, "Selecciona una orden primero.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        String caseNumber = model.getValueAt(row, 0).toString();
        String currentState = model.getValueAt(row, 3).toString();
        String[] states = {"EN_DIAGNOSTICO", "ESPERANDO_PIEZAS", "EN_REPARACION", "LISTO_PARA_ENTREGA"};
        String state = (String) JOptionPane.showInputDialog(null, "Selecciona el nuevo estado:",
                "Actualizar estado", JOptionPane.QUESTION_MESSAGE, null, states, currentState);
        if (state == null) {
            return;
        }
        String sql = "UPDATE ordenes_reparacion SET estado = ? WHERE numero_caso = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, state);
            statement.setLong(2, Long.parseLong(caseNumber));
            statement.executeUpdate();
            cargarOrdenes(connection, model);
        } catch (SQLException | NumberFormatException exception) {
            mostrarError(exception);
        }
    }

    private static JPanel crearResumenAdmin(Connection connection, JPanel views) {
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setBackground(BACKGROUND);
        JLabel welcome = new JLabel("Hola, Administrador | Selecciona una métrica para abrir su desglose");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 16));
        body.add(welcome, BorderLayout.NORTH);
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 16, 16));
        cardsPanel.setOpaque(false);
        cardsPanel.add(kpiCard("CLIENTES", contar(connection, "SELECT COUNT(*) FROM clientes"),
                "Registrados · Ver detalle", views, "Clientes"));
        cardsPanel.add(kpiCard("ÓRDENES ACTIVAS",
                contar(connection, "SELECT COUNT(*) FROM ordenes_reparacion "
                        + "WHERE estado <> 'LISTO_PARA_ENTREGA'"),
                "En proceso · Ver órdenes", views, "Órdenes de reparación"));
        cardsPanel.add(kpiCard("PERSONAL / ASIGNACIONES",
                contar(connection, "SELECT COUNT(*) FROM mecanicos"),
                "Mecánicos activos · Ver asignaciones", views, "Cuentas y roles"));
        cardsPanel.add(kpiCard("BASE DE DATOS", "CONECTADA",
                "H2 local · Sincronizada al abrir", views, "Resumen"));
        body.add(cardsPanel, BorderLayout.CENTER);
        return body;
    }

    private static String contar(Connection connection, String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getString(1);
        } catch (SQLException exception) {
            mostrarError(exception);
            return "—";
        }
    }

    private static JPanel kpiCard(String heading, String value, String detail,
            JPanel views, String destination) {
        JPanel card = infoCard(heading, value, detail);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                card.setBackground(new Color(232, 248, 244));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                card.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                ((CardLayout) views.getLayout()).show(views, destination);
            }
        });
        return card;
    }

    private static JPanel crearCotizacionesAdminTab(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "Orden", "Tipo", "Descripción", "Costo", "Llegada estimada"
        }, 0);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        estilizarTabla(table);
        cargarDetallesCotizacion(connection, model);

        JTextField orderId = new JTextField(36);
        JTextField description = new JTextField();
        JTextField cost = new JTextField();
        JTextField arrival = new JTextField("2026-12-01");
        JComboBox<String> type = new JComboBox<>(new String[]{"MANO_OBRA", "PIEZA"});
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        addCompactField(form, "Número de caso", orderId, 0);
        addCompactField(form, "Tipo", type, 1);
        addCompactField(form, "Descripción", description, 2);
        addCompactField(form, "Costo", cost, 3);
        addCompactField(form, "Llegada (AAAA-MM-DD)", arrival, 4);

        JButton add = new JButton("Agregar concepto");
        estilizarBotonPrincipal(add);
        add.addActionListener(event -> agregarDetalleCotizacion(connection, orderId, type, description,
                cost, arrival, model));
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarDetallesCotizacion(connection, model));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(refresh);
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(form, BorderLayout.CENTER);
        north.add(actions, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel crearCotizacionesClienteTab(Connection connection, String email) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "Orden", "Tipo", "Descripción", "Costo", "Llegada", "Estado"
        }, 0);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        estilizarTabla(table);
        cargarCotizacionesCliente(connection, email, model);
        JButton refresh = new JButton("Actualizar cotizaciones");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarCotizacionesCliente(connection, email, model));
        JButton approve = new JButton("Aprobar cotización seleccionada");
        estilizarBotonPrincipal(approve);
        approve.addActionListener(event -> aprobarCotizacion(connection, table, model, email));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(approve);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static void addCompactField(JPanel panel, String label, java.awt.Component field, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(3, 5, 3, 5);
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }

    private static void cargarDetallesCotizacion(Connection connection, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT o.numero_caso, d.tipo, d.descripcion, d.costo, d.fecha_llegada_estimada "
                + "FROM cotizacion_detalles d JOIN cotizaciones c ON c.id = d.cotizacion_id "
                + "JOIN ordenes_reparacion o ON o.id = c.orden_id ORDER BY o.fecha_ingreso DESC, d.id";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                model.addRow(new Object[]{result.getString(1), result.getString(2), result.getString(3),
                        result.getBigDecimal(4), result.getDate(5)});
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void agregarDetalleCotizacion(Connection connection, JTextField orderId,
            JComboBox<String> type, JTextField description, JTextField cost, JTextField arrival,
            DefaultTableModel model) {
        try {
            BigDecimal amount = new BigDecimal(cost.getText().trim());
            if (amount.signum() < 0 || description.getText().isBlank()) {
                throw new IllegalArgumentException("Descripción y costo válido son obligatorios.");
            }
            String find = "SELECT c.id FROM cotizaciones c JOIN ordenes_reparacion o "
                    + "ON o.id = c.orden_id WHERE o.numero_caso = ?";
            String create = "INSERT INTO cotizaciones (orden_id, mano_de_obra, piezas) "
                    + "VALUES ((SELECT id FROM ordenes_reparacion WHERE numero_caso = ?), 0, 0)";
            String detail = "INSERT INTO cotizacion_detalles "
                    + "(cotizacion_id, tipo, descripcion, costo, fecha_llegada_estimada) VALUES (?, ?, ?, ?, ?)";
            connection.setAutoCommit(false);
            try (PreparedStatement findStatement = connection.prepareStatement(find)) {
                findStatement.setLong(1, Long.parseLong(orderId.getText().trim()));
                long quoteId;
                try (ResultSet result = findStatement.executeQuery()) {
                    if (result.next()) {
                        quoteId = result.getLong(1);
                    } else {
                        try (PreparedStatement createStatement = connection.prepareStatement(create,
                                Statement.RETURN_GENERATED_KEYS)) {
                            createStatement.setLong(1, Long.parseLong(orderId.getText().trim()));
                            createStatement.executeUpdate();
                            try (ResultSet keys = createStatement.getGeneratedKeys()) {
                                if (!keys.next()) throw new SQLException("No se pudo crear la cotización.");
                                quoteId = keys.getLong(1);
                            }
                        }
                    }
                }
                try (PreparedStatement detailStatement = connection.prepareStatement(detail)) {
                    detailStatement.setLong(1, quoteId);
                    detailStatement.setString(2, type.getSelectedItem().toString());
                    detailStatement.setString(3, description.getText().trim());
                    detailStatement.setBigDecimal(4, amount);
                    detailStatement.setDate(5, java.sql.Date.valueOf(arrival.getText().trim()));
                    detailStatement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException | IllegalArgumentException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
            cargarDetallesCotizacion(connection, model);
            description.setText("");
            cost.setText("");
        } catch (SQLException | IllegalArgumentException exception) {
            mostrarError(exception);
        }
    }

    private static void cargarCotizacionesCliente(Connection connection, String email, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT o.numero_caso, d.tipo, d.descripcion, d.costo, d.fecha_llegada_estimada, "
                + "CASE WHEN c.aprobada THEN 'APROBADA' ELSE 'PENDIENTE' END "
                + "FROM cotizacion_detalles d JOIN cotizaciones c ON c.id = d.cotizacion_id "
                + "JOIN ordenes_reparacion o ON o.id = c.orden_id JOIN vehiculos v ON v.id = o.vehiculo_id "
                + "JOIN clientes cl ON cl.id = v.cliente_id WHERE cl.email = ? ORDER BY d.id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    model.addRow(new Object[]{result.getString(1), result.getString(2), result.getString(3),
                            result.getBigDecimal(4), result.getDate(5), result.getString(6)});
                }
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void aprobarCotizacion(Connection connection, JTable table, DefaultTableModel model, String email) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(null, "Selecciona un concepto de cotización.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        String caseNumber = model.getValueAt(row, 0).toString();
        int answer = JOptionPane.showConfirmDialog(null,
                "¿Aprobar la cotización completa de la orden seleccionada?",
                "Aprobar cotización", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        String sql = "UPDATE cotizaciones SET aprobada = TRUE "
                + "WHERE orden_id = (SELECT id FROM ordenes_reparacion WHERE numero_caso = ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, Long.parseLong(caseNumber));
            statement.executeUpdate();
            cargarCotizacionesCliente(connection, email, model);
        } catch (SQLException | NumberFormatException exception) {
            mostrarError(exception);
        }
    }

    private static JPanel crearDocumentosAdminTab(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Orden", "Documento", "Ubicación", "Fecha"}, 0);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        estilizarTabla(table);
        cargarDocumentos(connection, model, null);

        JTextField orderId = new JTextField(36);
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setOpaque(false);
        form.add(new JLabel("Número de caso:"));
        form.add(orderId);
        JButton upload = new JButton("Subir documento");
        estilizarBotonPrincipal(upload);
        upload.addActionListener(event -> subirDocumentoCotizacion(connection, orderId, model));
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarDocumentos(connection, model, null));
        JButton open = new JButton("Abrir seleccionado");
        estilizarBotonSecundario(open);
        open.addActionListener(event -> abrirDocumento(table, model));
        form.add(upload);
        form.add(refresh);
        form.add(open);
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel crearDocumentosClienteTab(Connection connection, String email) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Orden", "Documento", "Fecha", "Ruta interna"}, 0);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        estilizarTabla(table);
        cargarDocumentos(connection, model, email);
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarDocumentos(connection, model, email));
        JButton open = new JButton("Abrir documento seleccionado");
        estilizarBotonPrincipal(open);
        open.addActionListener(event -> abrirDocumento(table, model));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(open);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static void cargarDocumentos(Connection connection, DefaultTableModel model, String email) {
        model.setRowCount(0);
        String sql = "SELECT o.numero_caso, d.nombre, d.ruta, d.subido_en FROM cotizacion_documentos d "
                + "JOIN cotizaciones c ON c.id = d.cotizacion_id "
                + "JOIN ordenes_reparacion o ON o.id = c.orden_id";
        if (email != null) {
            sql += " JOIN vehiculos v ON v.id = o.vehiculo_id JOIN clientes cl ON cl.id = v.cliente_id "
                    + "WHERE cl.email = ?";
        }
        sql += " ORDER BY d.subido_en DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (email != null) statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    if (email == null) {
                        model.addRow(new Object[]{result.getString(1), result.getString(2),
                                result.getString(3), result.getTimestamp(4)});
                    } else {
                        model.addRow(new Object[]{result.getString(1), result.getString(2),
                                result.getTimestamp(4), result.getString(3)});
                    }
                }
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void subirDocumentoCotizacion(Connection connection, JTextField orderId,
            DefaultTableModel model) {
        if (orderId.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Escribe el número de caso.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
        Path source = chooser.getSelectedFile().toPath();
        String find = "SELECT c.id FROM cotizaciones c JOIN ordenes_reparacion o "
                + "ON o.id = c.orden_id WHERE o.numero_caso = ?";
        try {
            long quoteId;
            try (PreparedStatement statement = connection.prepareStatement(find)) {
                statement.setLong(1, Long.parseLong(orderId.getText().trim()));
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        JOptionPane.showMessageDialog(null, "La orden todavía no tiene cotización.");
                        return;
                    }
                    quoteId = result.getLong(1);
                }
            }
            Path directory = Path.of("data", "cotizaciones");
            Files.createDirectories(directory);
            String safeName = System.currentTimeMillis() + "_" + source.getFileName().toString()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = directory.resolve(safeName).toAbsolutePath().normalize();
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            String insert = "INSERT INTO cotizacion_documentos (cotizacion_id, nombre, ruta) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setLong(1, quoteId);
                statement.setString(2, source.getFileName().toString());
                statement.setString(3, target.toString());
                statement.executeUpdate();
            }
            cargarDocumentos(connection, model, null);
        } catch (SQLException | java.io.IOException | NumberFormatException exception) {
            mostrarError(exception);
        }
    }

    private static void abrirDocumento(JTable table, DefaultTableModel model) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(null, "Selecciona un documento.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        int pathColumn = model.findColumn("Ubicación");
        if (pathColumn < 0) pathColumn = model.findColumn("Ruta interna");
        Object pathValue = model.getValueAt(row, pathColumn);
        try {
            Path path = Path.of(pathValue.toString()).toAbsolutePath().normalize();
            if (!Files.isRegularFile(path)) throw new java.io.IOException("El archivo ya no existe.");
            if (!Desktop.isDesktopSupported()) throw new java.io.IOException("El sistema no admite abrir archivos.");
            Desktop.getDesktop().open(path.toFile());
        } catch (java.io.IOException exception) {
            mostrarError(exception);
        }
    }

    private static JPanel crearClientesTab(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Correo", "Creado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        cargarClientes(connection, model);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarClientes(connection, model));
        JButton edit = new JButton("Editar seleccionado");
        estilizarBotonSecundario(edit);
        edit.addActionListener(event -> editarCliente(connection, table, model));
        JButton add = new JButton("Registrar cliente");
        estilizarBotonPrincipal(add);
        add.addActionListener(event -> registrarCliente(connection, model));
        actions.add(refresh);
        actions.add(edit);
        actions.add(add);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static void cargarClientes(Connection connection, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT id, nombre, email, creado_en FROM clientes ORDER BY creado_en DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                model.addRow(new Object[]{result.getString("id"), result.getString("nombre"),
                        result.getString("email"), result.getTimestamp("creado_en")});
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static JPanel crearUsuariosTab(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Correo", "Rol", "Mecánico asignado", "Creado"}, 0);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        estilizarTabla(table);
        cargarUsuarios(connection, model);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarUsuarios(connection, model));
        JButton role = new JButton("Cambiar rol");
        estilizarBotonPrincipal(role);
        role.addActionListener(event -> cambiarRol(connection, table, model));
        actions.add(refresh);
        actions.add(role);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static void cargarUsuarios(Connection connection, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT u.id, u.nombre, u.email, u.rol, "
                + "COALESCE((SELECT STRINGAGG(DISTINCT m.nombre, ', ') "
                + "FROM clientes c JOIN vehiculos v ON v.cliente_id = c.id "
                + "JOIN ordenes_reparacion o ON o.vehiculo_id = v.id "
                + "JOIN mecanicos m ON m.id = o.mecanico_id WHERE c.email = u.email), "
                + "'Sin asignar'), u.creado_en FROM usuarios u ORDER BY u.creado_en DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                model.addRow(new Object[]{result.getString(1), result.getString(2),
                        result.getString(3), result.getString(4), result.getString(5),
                        result.getTimestamp(6)});
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void registrarCuenta(Connection connection) {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JTextField carModel = new JTextField();
        JTextField failure = new JTextField();
        JTextArea context = new JTextArea(5, 25);
        context.setLineWrap(true);
        context.setWrapStyleWord(true);
        context.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 207, 212)),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)));
        JPasswordField password = new JPasswordField();
        JDialog dialog = new JDialog((JFrame) null, "Crear cuenta y solicitar atención", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 0, 5, 0);

        JLabel intro = new JLabel("<html><b>Bienvenido a EVOX SPACE</b><br>"
                + "Crea tu cuenta y cuéntanos qué le ocurre a tu vehículo.</html>");
        intro.setForeground(DARK);
        intro.setFont(new Font("SansSerif", Font.PLAIN, 15));
        form.add(intro, c);

        c.gridy++;
        form.add(sectionLabel("DATOS DE LA CUENTA"), c);
        addFormRow(form, c, "Nombre completo", name);
        addFormRow(form, c, "Correo electrónico", email);
        addFormRow(form, c, "Contraseña", password);

        c.gridy++;
        form.add(sectionLabel("DATOS DE CONTACTO"), c);
        addFormRow(form, c, "Número de teléfono", phone);

        c.gridy++;
        form.add(sectionLabel("INFORMACIÓN DEL VEHÍCULO"), c);
        addFormRow(form, c, "Modelo del auto", carModel);
        addFormRow(form, c, "Falla presentada", failure);
        addFormRow(form, c, "Contexto de la falla", new JScrollPane(context));

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scroll, BorderLayout.CENTER);

        JLabel status = new JLabel("Completa todos los campos para enviar tu solicitud.");
        status.setForeground(Color.DARK_GRAY);
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 12, 20));
        footer.setBackground(new Color(248, 251, 252));
        footer.add(status, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton cancel = new JButton("Cancelar");
        estilizarBotonSecundario(cancel);
        cancel.addActionListener(event -> dialog.dispose());
        JButton save = new JButton("Crear cuenta y enviar");
        estilizarBotonPrincipal(save);
        save.addActionListener(event -> {
            if (name.getText().isBlank() || email.getText().isBlank() || phone.getText().isBlank()
                    || carModel.getText().isBlank() || failure.getText().isBlank()
                    || context.getText().isBlank() || password.getPassword().length == 0) {
                status.setText("Falta completar uno o más campos.");
                status.setForeground(new Color(190, 50, 50));
                return;
            }
            if (guardarCuentaYSolicitud(connection, name, email, password, phone, carModel, failure, context)) {
                dialog.dispose();
                JOptionPane.showMessageDialog(null,
                        "Cuenta creada como CLIENTE.\nTu solicitud fue enviada al administrador.",
                        "Registro completado", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttons.add(cancel);
        buttons.add(save);
        footer.add(buttons, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setSize(650, 680);
        dialog.setMinimumSize(new Dimension(560, 500));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ACCENT);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(12, 0, 2, 0));
        return label;
    }

    private static void addFormRow(JPanel form, GridBagConstraints constraints, String label, java.awt.Component field) {
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        form.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, constraints);
        constraints.gridx = 0;
        constraints.gridwidth = 2;
    }

    private static boolean guardarCuentaYSolicitud(Connection connection, JTextField name, JTextField email,
            JPasswordField password, JTextField phone, JTextField carModel, JTextField failure, JTextArea context) {
        String userSql = "INSERT INTO usuarios (id, nombre, email, password_hash, rol) "
                + "VALUES (RANDOM_UUID(), ?, ?, ?, 'CLIENTE')";
        String requestSql = "INSERT INTO solicitudes_cliente "
                + "(cliente_email, telefono, modelo_auto, falla, contexto) VALUES (?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(userSql);
                 PreparedStatement request = connection.prepareStatement(requestSql)) {
                statement.setString(1, name.getText().trim());
                statement.setString(2, email.getText().trim().toLowerCase());
                statement.setString(3, new String(password.getPassword()));
                statement.executeUpdate();
                request.setString(1, email.getText().trim().toLowerCase());
                request.setString(2, phone.getText().trim());
                request.setString(3, carModel.getText().trim());
                request.setString(4, failure.getText().trim());
                request.setString(5, context.getText().trim());
                request.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException exception) {
                connection.rollback();
                mostrarError(exception);
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            mostrarError(exception);
            return false;
        }
    }

    private static JPanel crearSolicitudesTab(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "ID", "Correo", "Teléfono", "Auto", "Falla", "Contexto", "Estado", "Creado"
        }, 0);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        estilizarTabla(table);
        cargarSolicitudes(connection, model);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarSolicitudes(connection, model));
        JButton createCase = new JButton("Generar caso");
        estilizarBotonPrincipal(createCase);
        createCase.addActionListener(event -> generarCaso(connection, table, model));
        actions.add(refresh);
        actions.add(createCase);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static void cargarSolicitudes(Connection connection, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT id, cliente_email, telefono, modelo_auto, falla, contexto, estado, creado_en "
                + "FROM solicitudes_cliente ORDER BY creado_en DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                model.addRow(new Object[]{result.getLong(1), result.getString(2), result.getString(3),
                        result.getString(4), result.getString(5), result.getString(6),
                        result.getString(7), result.getTimestamp(8)});
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void generarCaso(Connection connection, JTable table, DefaultTableModel model) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(null, "Selecciona una solicitud primero.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        if ("ATENDIDA".equals(model.getValueAt(row, 6).toString())) {
            JOptionPane.showMessageDialog(null, "Esta solicitud ya tiene un caso generado.");
            return;
        }
        long requestId = Long.parseLong(model.getValueAt(row, 0).toString());
        String email = model.getValueAt(row, 1).toString();
        String carModel = model.getValueAt(row, 3).toString();
        String failure = model.getValueAt(row, 4).toString();
        String context = model.getValueAt(row, 5).toString();
        String findClient = "SELECT id, nombre, password_hash FROM usuarios WHERE email = ?";
        String findRequest = "SELECT telefono FROM solicitudes_cliente WHERE id = ?";
        String insertClient = "MERGE INTO clientes (id, nombre, email, password_hash) "
                + "KEY (email) VALUES (RANDOM_UUID(), ?, ?, ?)";
        String findClientId = "SELECT id FROM clientes WHERE email = ?";
        String insertVehicle = "INSERT INTO vehiculos (id, cliente_id, vin, marca, modelo, anio) "
                + "VALUES (RANDOM_UUID(), ?, SUBSTRING(REPLACE(RANDOM_UUID(), '-', ''), 1, 7), 'Pendiente', ?, YEAR(CURRENT_DATE))";
        String findVehicle = "SELECT id FROM vehiculos WHERE cliente_id = ? ORDER BY id DESC LIMIT 1";
        String insertOrder = "INSERT INTO ordenes_reparacion (id, vehiculo_id, estado, fecha_ingreso) "
                + "VALUES (RANDOM_UUID(), ?, 'EN_DIAGNOSTICO', CURRENT_DATE)";
        String updateRequest = "UPDATE solicitudes_cliente SET estado = 'ATENDIDA' WHERE id = ?";
        try {
            connection.setAutoCommit(false);
            String clientId;
            try (PreparedStatement statement = connection.prepareStatement(findClient)) {
                statement.setString(1, email);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("No existe la cuenta del cliente.");
                    try (PreparedStatement insert = connection.prepareStatement(insertClient)) {
                        insert.setString(1, result.getString("nombre"));
                        insert.setString(2, email);
                        insert.setString(3, result.getString("password_hash"));
                        insert.executeUpdate();
                    }
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(findClientId)) {
                statement.setString(1, email);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("No se pudo obtener el cliente.");
                    clientId = result.getString(1);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(insertVehicle)) {
                statement.setString(1, clientId);
                statement.setString(2, carModel);
                statement.executeUpdate();
            }
            String vehicleId;
            try (PreparedStatement statement = connection.prepareStatement(findVehicle)) {
                statement.setString(1, clientId);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("No se pudo crear el vehículo.");
                    vehicleId = result.getString(1);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(insertOrder)) {
                statement.setString(1, vehicleId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(updateRequest)) {
                statement.setLong(1, requestId);
                statement.executeUpdate();
            }
            connection.commit();
            cargarSolicitudes(connection, model);
            JOptionPane.showMessageDialog(null, "Caso generado con orden en diagnóstico.\nFalla: "
                    + failure + "\nContexto: " + context);
        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            mostrarError(exception);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException exception) {
                mostrarError(exception);
            }
        }
    }

    private static String autenticar(Connection connection, String email, String password) {
        String sql = "SELECT rol FROM usuarios WHERE email = ? AND password_hash = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString("rol") : null;
            }
        } catch (SQLException exception) {
            mostrarError(exception);
            return null;
        }
    }

    private static void cambiarRol(Connection connection, JTable table, DefaultTableModel model) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(null, "Selecciona una cuenta primero.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        String id = model.getValueAt(row, 0).toString();
        String currentRole = model.getValueAt(row, 3).toString();
        String[] roles = {"CLIENTE", "MECANICO", "ADMINISTRADOR"};
        String role = (String) JOptionPane.showInputDialog(null, "Selecciona el nuevo rol:",
                "Cambiar rol", JOptionPane.QUESTION_MESSAGE, null, roles, currentRole);
        if (role == null) {
            return;
        }
        String sql = "UPDATE usuarios SET rol = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, role);
            statement.setString(2, id);
            statement.executeUpdate();
            cargarUsuarios(connection, model);
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void registrarCliente(Connection connection, DefaultTableModel model) {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JPasswordField password = new JPasswordField();
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Nombre"));
        form.add(name);
        form.add(new JLabel("Correo"));
        form.add(email);
        form.add(new JLabel("Contraseña demo"));
        form.add(password);
        if (JOptionPane.showConfirmDialog(null, form, "Registrar cliente",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        String sql = "INSERT INTO clientes (id, nombre, email, password_hash) VALUES (RANDOM_UUID(), ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.getText().trim());
            statement.setString(2, email.getText().trim().toLowerCase());
            statement.setString(3, new String(password.getPassword()));
            statement.executeUpdate();
            cargarClientes(connection, model);
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void editarCliente(Connection connection, JTable table, DefaultTableModel model) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(null, "Selecciona un cliente primero.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        String id = model.getValueAt(row, 0).toString();
        JTextField name = new JTextField(model.getValueAt(row, 1).toString());
        JTextField email = new JTextField(model.getValueAt(row, 2).toString());
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Nombre"));
        form.add(name);
        form.add(new JLabel("Correo"));
        form.add(email);
        if (JOptionPane.showConfirmDialog(null, form, "Editar cliente",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        String sql = "UPDATE clientes SET nombre = ?, email = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.getText().trim());
            statement.setString(2, email.getText().trim().toLowerCase());
            statement.setString(3, id);
            statement.executeUpdate();
            cargarClientes(connection, model);
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static JPanel crearOrdenesTab(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Caso", "VIN", "Cliente", "Estado", "Ingreso", "Entrega estimada"}, 0);
        JTable table = new JTable(model);
        estilizarTabla(table);
        table.setAutoCreateRowSorter(true);
        cargarOrdenes(connection, model);
        JButton refresh = new JButton("Actualizar órdenes");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarOrdenes(connection, model));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        actions.add(refresh);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel crearMensajesCliente(Connection connection, String clientEmail) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = crearModeloMensajes();
        JTable table = new JTable(model);
        estilizarTabla(table);
        table.setAutoCreateRowSorter(true);
        cargarMensajes(connection, model, clientEmail);

        JTextArea text = new JTextArea(3, 40);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        String mechanicTarget = obtenerMecanicoCliente(connection, clientEmail);
        String[] mechanicParts = mechanicTarget.split("\\|", -1);
        JComboBox<String> recipient = new JComboBox<>(new String[]{
                "Administrador",
                mechanicParts[2]
        });
        JPanel recipientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recipientPanel.setOpaque(false);
        recipientPanel.add(new JLabel("Enviar a:"));
        recipientPanel.add(recipient);
        JButton send = new JButton("Enviar mensaje");
        estilizarBotonPrincipal(send);
        send.addActionListener(event -> {
            if (recipient.getSelectedIndex() == 1 && mechanicParts[1].isBlank()) {
                JOptionPane.showMessageDialog(null, "El cliente aún no tiene mecánico asignado.");
                return;
            }
            String role = recipient.getSelectedIndex() == 0 ? "ADMIN" : "MECANICO";
            String email = recipient.getSelectedIndex() == 0
                    ? "admin@evoxspace.com" : mechanicParts[1];
            enviarMensaje(connection, "CLIENTE", clientEmail, role,
                    email, text, model, clientEmail);
        });
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarMensajes(connection, model, clientEmail));
        JPanel composer = new JPanel(new BorderLayout(8, 8));
        composer.setOpaque(false);
        composer.add(recipientPanel, BorderLayout.NORTH);
        composer.add(new JLabel("Escribe un mensaje:"), BorderLayout.WEST);
        composer.add(new JScrollPane(text), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        actions.add(send);
        actions.add(refresh);
        composer.add(actions, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(composer, BorderLayout.SOUTH);
        return panel;
    }

    private static String obtenerMecanicoCliente(Connection connection, String email) {
        String sql = "SELECT m.email, m.nombre FROM clientes c "
                + "JOIN vehiculos v ON v.cliente_id = c.id "
                + "JOIN ordenes_reparacion o ON o.vehiculo_id = v.id "
                + "JOIN mecanicos m ON m.id = o.mecanico_id "
                + "WHERE c.email = ? ORDER BY o.fecha_ingreso DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return "MECANICO|" + result.getString(1) + "|Mecánico: "
                        + result.getString(2);
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
        return "MECANICO||Mecánico no asignado";
    }

    private static JPanel crearMensajesAdmin(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = crearModeloMensajes();
        JTable table = new JTable(model);
        estilizarTabla(table);
        table.setAutoCreateRowSorter(true);
        cargarTodosMensajes(connection, model);

        JTextField recipient = new JTextField("demo@evoxspace.com", 25);
        JTextArea text = new JTextArea(3, 40);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        JButton send = new JButton("Enviar respuesta");
        estilizarBotonPrincipal(send);
        send.addActionListener(event -> enviarMensaje(connection, "ADMIN", "admin@evoxspace.com",
                "CLIENTE", recipient.getText().trim().toLowerCase(), text, model, null));
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarTodosMensajes(connection, model));
        JPanel composer = new JPanel(new BorderLayout(8, 8));
        composer.setOpaque(false);
        JPanel recipientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recipientPanel.setOpaque(false);
        recipientPanel.add(new JLabel("Correo del cliente:"));
        recipientPanel.add(recipient);
        composer.add(recipientPanel, BorderLayout.NORTH);
        composer.add(new JScrollPane(text), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        actions.add(send);
        actions.add(refresh);
        composer.add(actions, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(composer, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel crearMensajesMecanico(Connection connection, String mechanicEmail) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND);
        DefaultTableModel model = crearModeloMensajes();
        JTable table = new JTable(model);
        estilizarTabla(table);
        cargarMensajes(connection, model, mechanicEmail);

        JTextField recipient = new JTextField(25);
        JTextArea text = new JTextArea(3, 40);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        JButton send = new JButton("Responder al cliente");
        estilizarBotonPrincipal(send);
        send.addActionListener(event -> enviarMensaje(connection, "MECANICO", mechanicEmail,
                "CLIENTE", recipient.getText().trim().toLowerCase(), text, model, mechanicEmail));
        JButton refresh = new JButton("Actualizar");
        estilizarBotonSecundario(refresh);
        refresh.addActionListener(event -> cargarMensajes(connection, model, mechanicEmail));

        JPanel composer = new JPanel(new BorderLayout(8, 8));
        composer.setOpaque(false);
        JPanel recipientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recipientPanel.setOpaque(false);
        recipientPanel.add(new JLabel("Correo del cliente:"));
        recipientPanel.add(recipient);
        composer.add(recipientPanel, BorderLayout.NORTH);
        composer.add(new JScrollPane(text), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);
        actions.add(send);
        actions.add(refresh);
        composer.add(actions, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(composer, BorderLayout.SOUTH);
        return panel;
    }

    private static DefaultTableModel crearModeloMensajes() {
        return new DefaultTableModel(new String[]{"Fecha", "Remitente", "Destinatario", "Mensaje"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private static void cargarMensajes(Connection connection, DefaultTableModel model, String email) {
        model.setRowCount(0);
        String sql = "SELECT enviado_en, remitente_email, destinatario_email, contenido FROM mensajes "
                + "WHERE remitente_email = ? OR destinatario_email = ? ORDER BY enviado_en";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, email);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    model.addRow(new Object[]{result.getTimestamp(1), result.getString(2),
                            result.getString(3), result.getString(4)});
                }
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void cargarTodosMensajes(Connection connection, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT enviado_en, remitente_email, destinatario_email, contenido "
                + "FROM mensajes ORDER BY enviado_en";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                model.addRow(new Object[]{result.getTimestamp(1), result.getString(2),
                        result.getString(3), result.getString(4)});
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void enviarMensaje(Connection connection, String senderRole, String senderEmail,
            String recipientRole, String recipientEmail, JTextArea text, DefaultTableModel model,
            String clientEmail) {
        String content = text.getText().trim();
        if (recipientEmail.isBlank() || content.isBlank()) {
            JOptionPane.showMessageDialog(null, "El destinatario y el mensaje son obligatorios.");
            return;
        }
        String sql = "INSERT INTO mensajes (remitente_rol, remitente_email, destinatario_rol, "
                + "destinatario_email, contenido) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, senderRole);
            statement.setString(2, senderEmail);
            statement.setString(3, recipientRole);
            statement.setString(4, recipientEmail);
            statement.setString(5, content);
            statement.executeUpdate();
            text.setText("");
            if (clientEmail == null) {
                cargarTodosMensajes(connection, model);
            } else {
                cargarMensajes(connection, model, clientEmail);
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void cargarOrdenes(Connection connection, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT o.numero_caso, v.vin, c.nombre, o.estado, o.fecha_ingreso, o.fecha_entrega_estimada "
                + "FROM ordenes_reparacion o JOIN vehiculos v ON v.id = o.vehiculo_id "
                + "JOIN clientes c ON c.id = v.cliente_id ORDER BY o.fecha_ingreso DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                model.addRow(new Object[]{result.getString(1), result.getString(2), result.getString(3),
                        result.getString(4), result.getDate(5), result.getDate(6)});
            }
        } catch (SQLException exception) {
            mostrarError(exception);
        }
    }

    private static void mostrarError(SQLException exception) {
        JOptionPane.showMessageDialog(null, exception.getMessage(), "Error de base de datos",
                JOptionPane.ERROR_MESSAGE);
    }

    private static void mostrarError(Exception exception) {
        JOptionPane.showMessageDialog(null, exception.getMessage(), "Datos inválidos",
                JOptionPane.ERROR_MESSAGE);
    }

    private static void estilizarBotonPrincipal(JButton button) {
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setOpaque(true);
    }

    private static void estilizarBotonSecundario(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(DARK);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 207, 212)),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        button.setOpaque(true);
    }

    private static JButton crearBotonSidebar(String text) {
        JButton button = new JButton(text);
        estilizarBotonSidebar(button);
        return button;
    }

    private static void estilizarBotonSidebar(JButton button) {
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(new Color(224, 240, 239));
        button.setBackground(DARK);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 12, 11, 8));
        button.setOpaque(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(new Color(34, 57, 75));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(DARK);
            }
        });
    }

    private static void estilizarTabla(JTable table) {
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
    }

    private static JPanel infoCard(String heading, String value, String detail) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 232)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(3, 0, 3, 0);
        JLabel title = new JLabel(heading);
        title.setForeground(ACCENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        card.add(title, c);
        c.gridy = 1;
        JLabel main = new JLabel(value);
        main.setForeground(DARK);
        main.setFont(new Font("SansSerif", Font.BOLD, 21));
        card.add(main, c);
        c.gridy = 2;
        JLabel description = new JLabel(detail);
        description.setForeground(Color.DARK_GRAY);
        card.add(description, c);
        return card;
    }
}
