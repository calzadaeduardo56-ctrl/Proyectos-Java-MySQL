
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class surtido extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;
    private double totalVentaActual = 0.0;
    private boolean formularioCargado = false;
    private boolean cambioPorCodigo = false;
    private String puesto;

    public surtido() {
        initComponents();

        agregarValidacionUsuario(jComboBox1, this::validarProveedorDisponible);
        agregarValidacionUsuario(jComboBox2, this::validarMetodoPagoDisponible);

        formularioCargado = true;

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("proveedores", "Nombre", jComboBox1);

        re.RellenarComboBox("metdodopago", "NombreMetodo", jComboBox2);

        //re.RellenarComboBox("productos", "Descripcion", jComboBox4);
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/iteracion.png"));
        setIconImage(logoIcon.getImage());

        jButton1.setIcon(getIconUrl("/imagen/diskette.png", 30, 30));

        jButton2.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        cargarTabla();

        recalcularTotalVenta();

        centrarColumnas();

        inicializarLogicaDeCarga();

        ocultarIdDetalle();

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        surtido.this,
                        "¿Estás seguro de que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    dispose(); // Cierra la ventana
                }
            }
        });

        jDateChooser1.getDateEditor().addPropertyChangeListener("date", e -> {
            if (modoCapturaManual && jDateChooser1.getDate() != null) {
                jComboBox3.showPopup();
            }
        });

        jComboBox3.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField3.requestFocus();
            }
        });

        jTable1.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt
            ) {
                modoCapturaManual = false;
                int filaSeleccionada = jTable1.rowAtPoint(evt.getPoint());

                if (evt.getClickCount() == 1 && filaSeleccionada != -1) {
                    actualizarFormularioDesdeTabla(filaSeleccionada);
                }
            }
        }
        );
    }

// Este método DEBE estar dentro de la MISMA CLASE que el anterior
    private void actualizarFormularioDesdeTabla(int fila) {
        jComboBox1.setSelectedItem(obtenerValorDeTabla(fila, 1));
        jComboBox2.setSelectedItem(obtenerValorDeTabla(fila, 2));
        String fechaCompra = obtenerValorDeTabla(fila, 3);
        if (!fechaCompra.isEmpty()) {
            try {
                java.util.Date fecha = java.sql.Date.valueOf(fechaCompra);
                jDateChooser1.setDate(fecha);
            } catch (IllegalArgumentException e) {
                jDateChooser1.setDate(null);
            }
        } else {
            jDateChooser1.setDate(null);
        }
        jComboBox3.setSelectedItem(obtenerValorDeTabla(fila, 5));
        jTextField3.setText(obtenerValorDeTabla(fila, 6));
        jTextField2.setText(obtenerValorDeTabla(fila, 7));
        jTextField4.setText(obtenerValorDeTabla(fila, 8));
        jTextField1.setText(obtenerValorDeTabla(fila, 9));
        modoCapturaManual = true;
    }

    private String obtenerValorDeTabla(int fila, int columna) {
        Object valor = jTable1.getValueAt(fila, columna);
        return (valor != null) ? valor.toString() : "";
    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    private void centrarColumnas() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centrado);
            jTable1.getColumnModel().getColumn(11).setCellRenderer(new TipoPrecio());
        }
    }

    public void cargarDatosDeTicketPorCodigo(String codigoBarras) {

        String sql = "SELECT Descripcion, Stock, PrecioInicial "
                + "FROM productos WHERE CodigoBarras = ?";

        try (Connection cn = new conexion().conectar(); PreparedStatement stmt = cn.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                // 💰 Precio
                jTextField2.setText(String.format("%.2f", rs.getDouble("PrecioInicial")));

                // 📦 Stock
                jTextField4.setText(String.valueOf(rs.getInt("Stock")));

                // 🧾 Descripción
                jTextField1.setText(rs.getString("Descripcion"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void cargarTickets() {

        jComboBox3.removeAllItems(); // 🔹 limpiar antes

        String sql = "SELECT CodigoBarras FROM productos";

        try (Connection cn = new conexion().conectar(); PreparedStatement stmt = cn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                jComboBox3.addItem(rs.getString("CodigoBarras"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void inicializarLogicaDeCarga() {
        cargarTickets();

        jComboBox3.addActionListener(e -> {
            String IdSurtido = (String) jComboBox3.getSelectedItem();
            if (IdSurtido != null && !IdSurtido.trim().isEmpty()) {
                cargarDatosDeTicketPorCodigo(IdSurtido);
            }
        });
    }

    private boolean validarCamposVacios() {
        JTextField[] campos = {jTextField2, jTextField3, jTextField4, jTextField1};
        boolean hayVacios = false;

        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) {
                campo.putClientProperty("JComponent.outline", "error");
                campo.repaint();
                campo.revalidate();
                hayVacios = true;
            }
        }

        if (hayVacios) {
            mostrarMensaje("Para poder guardar, no debe dejar campos en blanco.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            new Timer(4000, evt -> {
                for (JTextField campo : campos) {
                    campo.putClientProperty("JComponent.outline", null);
                    campo.repaint();
                    campo.revalidate();
                }
            }).start();
        }

        return hayVacios;
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipo);
    }

    private void limpiarCampos() {
        jTextField3.setText("");
        jTextField2.setText("");
        jTextField4.setText("");
        jTextField1.setText("");
    }

    public double recalcularTotalVenta() {

        double total = 0.0;
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int columnaPrecioTotal = 10; // ajusta si cambia

        for (int i = 0; i < model.getRowCount(); i++) {
            Object valor = model.getValueAt(i, columnaPrecioTotal);
            if (valor != null) {
                total += Double.parseDouble(valor.toString());
            }
        }

        total = Math.round(total * 100.0) / 100.0;
        totalVentaActual = total; // 🔥 guardar total REAL

        jLabel12.setText("Total a Pagar = $" + String.format("%.2f", total) + " pesos");

        return total;
    }

    public void guardarRegistro() {

        if (validarCamposVacios()) {
            return;
        }

        String Descripcion, Proveedor, metPago, TipoPrecio = "Sin Cambios";
        int Foliocompra, Codigo, Cantidad, Stock;
        int idsurtido = generarNuevoIdAsistencia();
        double PrecioCompra, PrecioTotal;

        Proveedor = jComboBox1.getSelectedItem().toString();
        metPago = jComboBox2.getSelectedItem().toString();
        java.sql.Date FechaCompra = new java.sql.Date(jDateChooser1.getDate().getTime());

        Codigo = Integer.parseInt(jComboBox3.getSelectedItem().toString());
        Cantidad = Integer.parseInt(jTextField3.getText().trim());

        String texto = jTextField2.getText().trim().replace(",", ".");
        PrecioCompra = Double.parseDouble(texto);

        Stock = Integer.parseInt(jTextField4.getText().trim());
        Descripcion = jTextField1.getText().trim();

        PrecioTotal = Math.round((Cantidad * PrecioCompra) * 100.0) / 100.0;

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Desea guardar este registro?",
                "Confirmar Registro",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String sqlGetFolio
                = "SELECT CASE "
                + "WHEN COUNT(*) = 0 THEN 1000 "
                + "ELSE MIN(FolioCompra) - 1 "
                + "END AS NuevoFolio "
                + "FROM surtido";

        String sqlInsertSurtido
                = "INSERT INTO surtido (IdSurtido, Proveedor, MetodoPago, FechaCompra, FolioCompra, Codigo, Cantidad, PrecioCompra, Stock, Descripcion, PrecioTotal, TipoPrecio) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlGetProducto
                = "SELECT Stock, PrecioInicial FROM productos WHERE Descripcion = ?";

        String sqlUpdateProducto
                = "UPDATE productos SET Stock = ?, PrecioInicial = ? WHERE Descripcion = ?";

        try (Connection cn = new conexion().conectar()) {

            cn.setAutoCommit(false); // 🔴 TRANSACCIÓN

            // 1️⃣ OBTENER FOLIO AUTOMÁTICO
            try (PreparedStatement pstFolio = cn.prepareStatement(sqlGetFolio); ResultSet rsFolio = pstFolio.executeQuery()) {

                rsFolio.next();
                Foliocompra = rsFolio.getInt("NuevoFolio");
            }

            int stockProducto = 0;
            double precioAnterior = 0;

            // 2️⃣ OBTENER STOCK Y PRECIO ACTUAL
            try (PreparedStatement pstGet = cn.prepareStatement(sqlGetProducto)) {
                pstGet.setString(1, Descripcion);
                ResultSet rs = pstGet.executeQuery();
                if (rs.next()) {
                    stockProducto = rs.getInt("Stock");
                    precioAnterior = rs.getDouble("PrecioInicial");
                }
            }

            // 3️⃣ COMPARAR PRECIOS
            if (PrecioCompra > precioAnterior) {
                TipoPrecio = "Precio Alto";
            } else if (PrecioCompra < precioAnterior) {
                TipoPrecio = "Precio Bajo";
            } else {
                TipoPrecio = "Sin Cambios";
            }

            // 4️⃣ INSERTAR SURTIDO
            try (PreparedStatement pst = cn.prepareStatement(sqlInsertSurtido)) {

                pst.setInt(1, idsurtido);
                pst.setString(2, Proveedor);
                pst.setString(3, metPago);
                pst.setDate(4, FechaCompra);
                pst.setInt(5, Foliocompra); // 🔥 AUTOMÁTICO
                pst.setInt(6, Codigo);
                pst.setInt(7, Cantidad);
                pst.setDouble(8, PrecioCompra);
                pst.setInt(9, Stock);
                pst.setString(10, Descripcion);
                pst.setDouble(11, PrecioTotal);
                pst.setString(12, TipoPrecio);

                pst.executeUpdate();
            }

            // 5️⃣ ACTUALIZAR STOCK
            int nuevoStockProducto = stockProducto + Cantidad;

            try (PreparedStatement pstUpd = cn.prepareStatement(sqlUpdateProducto)) {
                pstUpd.setInt(1, nuevoStockProducto);
                pstUpd.setDouble(2, PrecioCompra);
                pstUpd.setString(3, Descripcion);
                pstUpd.executeUpdate();
            }

            cn.commit(); // ✅ TODO OK

            cargarTabla();
            centrarColumnas();
            limpiarCampos();
            recalcularTotalVenta();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al guardar el registro:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void actualizarRegistro() {

        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe seleccionar un registro para actualizar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            String Descripcion, Proveedor, metPago, TipoPrecio = "Sin Cambios";
            int Foliocompra, Codigo, Cantidad, Stock;
            double PrecioCompra, PrecioTotal;

            Proveedor = jComboBox1.getSelectedItem().toString();
            metPago = jComboBox2.getSelectedItem().toString();
            java.sql.Date FechaCompra = new java.sql.Date(jDateChooser1.getDate().getTime());
            Foliocompra = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 4).toString());
            Codigo = Integer.parseInt(jComboBox3.getSelectedItem().toString());
            Cantidad = Integer.parseInt(jTextField3.getText().trim());

            String texto = jTextField2.getText().trim().replace(",", ".");
            PrecioCompra = Double.parseDouble(texto);

            Stock = Integer.parseInt(jTextField4.getText().trim()); // informativo
            Descripcion = jTextField1.getText().trim();

            PrecioTotal = Math.round((Cantidad * PrecioCompra) * 100.0) / 100.0;

            int idsurtido = Integer.parseInt(
                    jTable1.getValueAt(filaSeleccionada, 0).toString()
            );

            int confirmacion = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de actualizar este registro?",
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            String sqlUpdateSurtido
                    = "UPDATE surtido SET Proveedor=?, MetodoPago=?, FechaCompra=?, "
                    + "FolioCompra=?, Codigo=?, Cantidad=?, PrecioCompra=?, Stock=?, "
                    + "Descripcion=?, PrecioTotal=?, TipoPrecio=? WHERE IdSurtido=?";

            String sqlGetCantidadAnterior
                    = "SELECT Cantidad FROM surtido WHERE IdSurtido=?";

            String sqlGetProducto
                    = "SELECT Stock, PrecioInicial FROM productos WHERE Descripcion=?";

            String sqlUpdateProducto
                    = "UPDATE productos SET Stock=?, PrecioInicial=? WHERE Descripcion=?";

            try (Connection cn = new conexion().conectar()) {

                cn.setAutoCommit(false); // 🔴 TRANSACCIÓN

                // 1️⃣ Cantidad anterior del surtido
                int cantidadAnterior = 0;
                try (PreparedStatement pst = cn.prepareStatement(sqlGetCantidadAnterior)) {
                    pst.setInt(1, idsurtido);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        cantidadAnterior = rs.getInt("Cantidad");
                    }
                }

                // 2️⃣ Stock y precio actual del producto
                int stockProducto = 0;
                double precioAnterior = 0;

                try (PreparedStatement pst = cn.prepareStatement(sqlGetProducto)) {
                    pst.setString(1, Descripcion);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        stockProducto = rs.getInt("Stock");
                        precioAnterior = rs.getDouble("PrecioInicial");
                    }
                }

                // 3️⃣ Determinar TipoPrecio
                if (PrecioCompra > precioAnterior) {
                    TipoPrecio = "Precio Alto";
                } else if (PrecioCompra < precioAnterior) {
                    TipoPrecio = "Precio Bajo";
                } else {
                    TipoPrecio = "Sin Cambios";
                }

                // 4️⃣ Ajustar stock
                int diferencia = Cantidad - cantidadAnterior;
                int nuevoStockProducto = stockProducto + diferencia;

                // 5️⃣ Actualizar surtido
                try (PreparedStatement pst = cn.prepareStatement(sqlUpdateSurtido)) {

                    pst.setString(1, Proveedor);
                    pst.setString(2, metPago);
                    pst.setDate(3, FechaCompra);
                    pst.setInt(4, Foliocompra);
                    pst.setInt(5, Codigo);
                    pst.setInt(6, Cantidad);
                    pst.setDouble(7, PrecioCompra);
                    pst.setInt(8, Stock);
                    pst.setString(9, Descripcion);
                    pst.setDouble(10, PrecioTotal);
                    pst.setString(11, TipoPrecio); // 🔥 automático
                    pst.setInt(12, idsurtido);

                    pst.executeUpdate();
                }

                // 6️⃣ Actualizar productos (STOCK + PRECIO)
                try (PreparedStatement pst = cn.prepareStatement(sqlUpdateProducto)) {
                    pst.setInt(1, nuevoStockProducto);
                    pst.setDouble(2, PrecioCompra);
                    pst.setString(3, Descripcion);
                    pst.executeUpdate();
                }

                cn.commit(); // ✅ TODO OK

                cargarTabla();
                centrarColumnas();
                recalcularTotalVenta();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al actualizar:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int generarNuevoIdAsistencia() {
        int nuevoId = 1; // Por defecto empezamos en 1

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(idsurtido) FROM surtido"); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                nuevoId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
        }
        return nuevoId;
    }

    public void eliminarRegistro() {

        int filaSeleccionada = jTable1.getSelectedRow();

        // 🟡 Si no hay fila seleccionada
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe seleccionar un registro para eliminar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();

        // Obtener el ID del registro
        Object idObj = modelo.getValueAt(filaSeleccionada, 0);
        int idnueva = Integer.parseInt(idObj.toString());

        // 🔴 Confirmación mejorada
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de eliminar este registro?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(
                "DELETE FROM  surtido WHERE IdSurtido = ?")) {

            pst.setInt(1, idnueva);
            pst.executeUpdate();

            // ❌ Eliminar fila visual
            modelo.removeRow(filaSeleccionada);

            // 🔄 Recalcular total de la venta actual
            recalcularTotalVenta();

            // 🧹 Limpiar campos
            limpiarCampos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al eliminar el registro:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void cargarTabla() {
        final String sql = "SELECT IdSurtido, Proveedor, MetodoPago, FechaCompra, FolioCompra, Codigo, Cantidad, PrecioCompra, Stock, Descripcion, PrecioTotal, TipoPrecio  FROM surtido";
        final String[] columnas = {"IdSurtido", "Proveedor", "MetodoPago", "FechaCompra", "FolioCompra", "Codigo", "Cantidad", "PrecioCompra", "Stock", "Descripcion", "PrecioTotal", "TipoPrecio"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        conexion conexion = new conexion();
        try (Connection conn = conexion.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String PrecioCompra = String.format("%.2f", rs.getDouble("PrecioCompra"));
                String PrecioTotal = String.format("%.2f", rs.getDouble("PrecioTotal"));

                Object[] fila = {
                    rs.getInt("IdSurtido"),
                    rs.getString("Proveedor"),
                    rs.getString("MetodoPago"),
                    rs.getDate("FechaCompra"),
                    rs.getInt("FolioCompra"),
                    rs.getInt("Codigo"),
                    rs.getInt("Cantidad"),
                    PrecioCompra,
                    rs.getInt("Stock"),
                    rs.getString("Descripcion"),
                    PrecioTotal,
                    rs.getString("TipoPrecio")
                };

                modelo.addRow(fila);
            }

            jTable1.setModel(modelo);
            ocultarIdDetalle();

        } catch (SQLException ex) {
        }
    }

    private void validarProveedorDisponible() {

        String proveedorSeleccionado = jComboBox1.getSelectedItem().toString();

        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM proveedores WHERE Nombre = ?";

        try {
            Connection cn = new conexion().conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, proveedorSeleccionado);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("Status");

                // 🚨 SI NO ESTA DISPONIBLE
                if (status.equalsIgnoreCase("No esta Disponible")) {

                    JOptionPane.showMessageDialog(
                            null,
                            proveedorSeleccionado + " no está disponible",
                            "Proveedor no disponible",
                            JOptionPane.WARNING_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> {
                        jComboBox1.requestFocusInWindow();
                        jComboBox1.showPopup();
                    });
                }
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar proveedor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validarMetodoPagoDisponible() {

        String proveedorSeleccionado = jComboBox2.getSelectedItem().toString();

        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM metdodopago WHERE NombreMetodo = ?";

        try {
            Connection cn = new conexion().conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, proveedorSeleccionado);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("Status");

                // 🚨 SI NO ESTA DISPONIBLE
                if (status.equalsIgnoreCase("No esta Disponible")) {

                    JOptionPane.showMessageDialog(
                            null,
                            proveedorSeleccionado + " no está disponible",
                            "Proveedor no disponible",
                            JOptionPane.WARNING_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> {
                        jComboBox2.requestFocusInWindow();
                        jComboBox2.showPopup();
                    });
                }
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar proveedor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void agregarValidacionUsuario(JComboBox<?> combo, Runnable validacion) {

        combo.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (formularioCargado) {
                    validacion.run();
                }
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, jButton3, btnMenuContador);
    }

    private void ocultarIdDetalle() {
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jComboBox3 = new javax.swing.JComboBox<>();
        jTextField4 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        btnMenuContador = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("SURTIDO DE MERCANCIAS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 190, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Proveedor");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, 190, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Metodo de Pago");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 180, 190, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Fecha de Compra");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Codigo");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, -1, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 240, 150, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Cantidad");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 240, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Precio de Compra");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Stock");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 180, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Descripcion");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, -1, -1));

        jTextField7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 180, 170, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Buscador");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 180, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 110, 360, 40));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 200, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Eliminar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 300, 200, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Menu de Modulos");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 300, 260, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "IdSurtido", "Proveedor", "MetodoPago", "FechaCompra", "FolioCompra", "Codigo", "Cantidad", "PrecioCompra", "Stock", "Descripcion", "PrecioTotal", "TipoPrecio"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 1120, 300));

        jComboBox3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 240, 190, 30));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 180, 150, 30));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 120, 140, 30));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Editar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 300, 160, 40));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 60, 140, 30));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Cajero");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 240, 260, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1140, 660));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        guardarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        String textoFiltro = jTextField7.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT  IdSurtido, Proveedor, MetodoPago, FechaCompra, FolioCompra, Codigo, Cantidad, PrecioCompra, Stock, Descripcion, PrecioTotal, TipoPrecio  FROM surtido");
        boolean tieneTexto = !textoFiltro.isEmpty();

        if (tieneTexto) {
            sql.append(" WHERE FolioCompra LIKE ? OR Descripcion LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"IdSurtido", "Proveedor", "MetodoPago", "FechaCompra", "FolioCompra", "Codigo", "Cantidad", "PrecioCompra", "Stock", "Descripcion", "PrecioTotal", "TipoPrecio"}, 0);

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            if (tieneTexto) {
                String filtro = "%" + textoFiltro + "%";
                ps.setString(1, filtro); // Empresa
                ps.setString(2, filtro); // Empleado
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsMd = rs.getMetaData();
                int columnas = rsMd.getColumnCount();

                while (rs.next()) {
                    Object[] fila = new Object[columnas];
                    for (int i = 0; i < columnas; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    modelo.addRow(fila);
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();
            recalcularTotalVenta();
            ocultarIdDetalle();

        } catch (SQLException ex) {
        }
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void btnMenuContadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuContadorActionPerformed
        MenuCajero menu = new MenuCajero();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuContadorActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(surtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(surtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(surtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(surtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            FlatIntelliJLaf.setup();
            UIManager.put("Component.arc", 30);
            UIManager.put("TextComponent.arc", 30);
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.borderColor", new Color(80, 80, 80));
            UIManager.put("Button.borderColor", new Color(80, 80, 80));
            UIManager.put("defaultFont", new Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new surtido().setVisible(true);
            }
        });
    }

    private void pintarImagen(JLabel lbl, String ruta) {
        try {
            // Carga de imagen como recurso (dentro del .jar)
            java.net.URL imgURL = getClass().getResource("/imagen/Imagen3.png");
            if (imgURL != null) {
                this.imagen = new ImageIcon(imgURL);
                this.icocno = new ImageIcon(
                        this.imagen.getImage().getScaledInstance(
                                lbl.getWidth(), lbl.getHeight(), Image.SCALE_SMOOTH));
                lbl.setIcon(this.icocno);
            } else {
            }
        } catch (Exception e) {
        }
        this.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMenuContador;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
