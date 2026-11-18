
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itextpdf.text.Font;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
public class VentasFechas extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;
    private String puesto;
    private final DecimalFormat df = new DecimalFormat("0.00");
    private double totalVentaActual = 0.0;
    public int idVenta;
    private boolean formularioCargado = false;

    public VentasFechas() {
        initComponents();

        agregarValidacionUsuario(jComboBox1, this::validarMetodoPagoDisponible);

        formularioCargado = true;

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/calendario.png"));
        setIconImage(logoIcon.getImage());

        jButton1.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        btnMenuSupervisor.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        centrarColumnas();

        inicializarLogicaDeCarga();

        try {

            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar JTable

            Connection cn = new conexion().conectar();

            String sql = "SELECT iddetalle, CodigoBarras, Producto, PrecioVenta, Cantidad, "
                    + "TipoVenta, FechaVenta, MetodoPago, PrecioTotal FROM nuevaventa";

            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // INSERT adaptado a los datos de nuevaventa
            String sqlInsert = "INSERT INTO ventasfecha "
                    + "(Folio, CodigoBarras, Producto, PrecioVenta, Cantidad, "
                    + "TipoVenta, FechaVenta, MetodoPago, PrecioTotal) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "CodigoBarras = VALUES(CodigoBarras), "
                    + "Producto = VALUES(Producto), "
                    + "PrecioVenta = VALUES(PrecioVenta), "
                    + "Cantidad = VALUES(Cantidad), "
                    + "TipoVenta = VALUES(TipoVenta), "
                    + "FechaVenta = VALUES(FechaVenta), "
                    + "MetodoPago = VALUES(MetodoPago), "
                    + "PrecioTotal = VALUES(PrecioTotal)";

            PreparedStatement psInsert = cn.prepareStatement(sqlInsert);

            while (rs.next()) {

                int Folio = rs.getInt("iddetalle");
                String CodigoBarras = rs.getString("CodigoBarras");
                String Producto = rs.getString("Producto");
                double PrecioVenta = rs.getDouble("PrecioVenta");
                int Cantidad = rs.getInt("Cantidad");
                String TipoVenta = rs.getString("TipoVenta");
                java.sql.Date FechaVenta = rs.getDate("FechaVenta");
                String MetodoPago = rs.getString("MetodoPago");
                double PrecioTotal = rs.getDouble("PrecioTotal");

                psInsert.setInt(1, Folio);
                psInsert.setString(2, CodigoBarras);
                psInsert.setString(3, Producto);
                psInsert.setDouble(4, PrecioVenta);
                psInsert.setInt(5, Cantidad);
                psInsert.setString(6, TipoVenta);
                psInsert.setDate(7, FechaVenta);
                psInsert.setString(8, MetodoPago);
                psInsert.setDouble(9, PrecioTotal);

                psInsert.executeUpdate();

                // Agregar al JTable
                modelo.addRow(new Object[]{
                    Folio,
                    CodigoBarras,
                    Producto,
                    PrecioVenta,
                    Cantidad,
                    TipoVenta,
                    FechaVenta,
                    MetodoPago,
                    PrecioTotal
                });
            }

            rs.close();
            ps.close();
            psInsert.close();
            cn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e
            ) {
                int opcion = JOptionPane.showConfirmDialog(
                        VentasFechas.this,
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
                jDateChooser2.getCalendarButton().doClick();
            }
        });

        jDateChooser2.getDateEditor()
                .addPropertyChangeListener("date", e -> {
                    if (modoCapturaManual && jDateChooser2.getDate() != null) {
                        jComboBox1.showPopup();
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

        String FechaInicio = obtenerValorDeTabla(fila, 11);
        if (!FechaInicio.isEmpty()) {
            try {
                java.util.Date fechaReal = java.sql.Date.valueOf(FechaInicio);
                jDateChooser1.setDate(fechaReal);
            } catch (IllegalArgumentException e) {
                jDateChooser1.setDate(null);
            }
        } else {
            jDateChooser1.setDate(null);
        }

        String FechaTermino = obtenerValorDeTabla(fila, 12);
        if (!FechaTermino.isEmpty()) {
            try {
                java.util.Date fechaReal = java.sql.Date.valueOf(FechaTermino);
                jDateChooser2.setDate(fechaReal);
            } catch (IllegalArgumentException e) {
                jDateChooser2.setDate(null);
            }
        } else {
            jDateChooser2.setDate(null);
        }
        jComboBox1.setSelectedItem(obtenerValorDeTabla(fila, 9));
        jTextField1.setText(obtenerValorDeTabla(fila, 10));
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

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuSupervisor);
    }

    private void centrarColumnas() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }
    }

    public void eliminarRegistro() {
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un registro para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String Id_ticket = jTable1.getValueAt(filaSeleccionada, 0).toString();

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                PreparedStatement pst = cn.prepareStatement("DELETE FROM ventasfecha WHERE Folio=?");
                pst.setString(1, Id_ticket);
                pst.executeUpdate();

                // Eliminar del modelo asociado a jTable2
                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                modelo.removeRow(filaSeleccionada);
            }
        } catch (SQLException ex) {
        }

    }

    public void actualizarRegistro() {
        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int Folio = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de actualizar este registro?",
                "Confirmar Actualización",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String Cliente, Telefono;

        java.sql.Date FechaInicio = new java.sql.Date(jDateChooser1.getDate().getTime());

        java.sql.Date FechaTermino = new java.sql.Date(jDateChooser2.getDate().getTime());

        Telefono = jTextField1.getText().trim();

        Cliente = jComboBox1.getSelectedItem().toString();

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String sql = "UPDATE ventasfecha SET Cliente = ?, Telefono = ?, FechaInicio = ?, FechaTermino = ?  WHERE Folio = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, Cliente);
            ps.setString(2, Telefono);
            ps.setDate(3, FechaInicio);
            ps.setDate(4, FechaTermino);
            ps.setInt(5, Folio);
            ps.executeUpdate();
            cargarTabla();
            centrarColumnas();

        } catch (SQLException ex) {
            System.out.println("Error al actualizar: " + ex.getMessage());
        }
    }

    public void cargarTabla() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar tabla

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String sql = "SELECT Folio, CodigoBarras, Producto, PrecioVenta, Cantidad, TipoVenta, FechaVenta, MetodoPago, PrecioTotal, Cliente, Telefono, FechaInicio, FechaTermino  FROM ventasfecha";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int Folio = rs.getInt("Folio");
                int CodigoBarras = rs.getInt("CodigoBarras");
                String Producto = rs.getString("Producto") != null ? rs.getString("Producto") : "";
                double precioVenta = rs.getObject("PrecioVenta") != null ? rs.getDouble("PrecioVenta") : 0;
                String precioVentaFormateado = df.format(precioVenta);
                int Cantidad = rs.getInt("Cantidad");
                String TipoVenta = rs.getString("TipoVenta") != null ? rs.getString("TipoVenta") : "";
                String FechaVenta = rs.getDate("FechaVenta") != null ? rs.getDate("FechaVenta").toString() : "";
                String MetodoPago = rs.getString("MetodoPago") != null ? rs.getString("MetodoPago") : "";
                double precioTotal = rs.getObject("PrecioTotal") != null ? rs.getDouble("PrecioTotal") : 0;
                String precioTotalFormateado = df.format(precioTotal);
                String Cliente = rs.getString("Cliente") != null ? rs.getString("Cliente") : "";
                String Telefono = rs.getString("Telefono") != null ? rs.getString("Telefono") : "";
                String FechaInicio = rs.getDate("FechaInicio") != null ? rs.getDate("FechaInicio").toString() : "";
                String FechaTermino = rs.getDate("FechaTermino") != null ? rs.getDate("FechaTermino").toString() : "";

                modelo.addRow(new Object[]{Folio, CodigoBarras, Producto, precioVentaFormateado, TipoVenta, Cantidad, FechaVenta, MetodoPago, precioTotalFormateado, Cliente, Telefono, FechaInicio, FechaTermino});
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException ex) {
            System.out.println("Error al cargar tabla: " + ex.getMessage());
        }
    }

    public double recalcularTotalVenta() {

        double total = 0.0;
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int columnaPrecioTotal = 8; // ✅ columna correcta

        for (int i = 0; i < model.getRowCount(); i++) {
            Object valor = model.getValueAt(i, columnaPrecioTotal);

            if (valor != null) {
                String texto = valor.toString().trim();

                if (!texto.isEmpty()) {
                    try {
                        total += Double.parseDouble(texto);
                    } catch (NumberFormatException e) {
                        System.out.println("Valor no numérico en fila " + i + ": " + texto);
                    }
                }
            }
        }

        total = Math.round(total * 100.0) / 100.0;
        totalVentaActual = total;

        jLabel8.setText("Total a Pagar = $" + String.format("%.2f", total) + " pesos");

        return total;
    }

    public void cargarDatosDeTicketPorCodigo(String Nombre) {

        String sql = "SELECT Telefono "
                + "FROM empleados WHERE Nombre = ?";

        try (Connection cn = new conexion().conectar(); PreparedStatement stmt = cn.prepareStatement(sql)) {

            stmt.setString(1, Nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                jTextField1.setText(rs.getString("Telefono"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void cargarTickets() {

        jComboBox1.removeAllItems(); // 🔹 limpiar antes

        String sql = "SELECT Nombre FROM empleados";

        try (Connection cn = new conexion().conectar(); PreparedStatement stmt = cn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                jComboBox1.addItem(rs.getString("Nombre"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void inicializarLogicaDeCarga() {
        cargarTickets();

        jComboBox1.addActionListener(e -> {
            String IdSurtido = (String) jComboBox1.getSelectedItem();
            if (IdSurtido != null && !IdSurtido.trim().isEmpty()) {
                cargarDatosDeTicketPorCodigo(IdSurtido);
            }
        });
    }

    private void validarMetodoPagoDisponible() {

        String proveedorSeleccionado = jComboBox1.getSelectedItem().toString();

        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM empleados WHERE Nombre = ?";

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        btnMenuSupervisor = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("VENTAS POR FECHA");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 10, -1, -1));

        jDateChooser2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 170, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Fecha de Termino");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 180, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Fecha de Inicio");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 60, 190, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Buscador");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 60, -1, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, 230, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Telefono");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 60, 230, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Nombre del Cliente");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, -1, -1));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Folio", "CodigoBarras", "Producto", "PrecioVenta", "Cantidad", "TipoVenta", "FechaVenta", "MetodoPago", "PrecioTotal", "Cliente", "Telefono", "FechaInicio", "FechaTermino"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 1150, 240));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Editar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 160, 40));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Eliminar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 160, 160, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 160, 240, 40));

        btnMenuSupervisor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSupervisor.setText("Menu del Cajero");
        btnMenuSupervisor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSupervisorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuSupervisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 110, 260, 40));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 160, 520, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 460));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnMenuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuModulosActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuModulosActionPerformed

    private void btnMenuSupervisorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSupervisorActionPerformed
        MenuCajero menu = new MenuCajero();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuSupervisorActionPerformed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        String textoFiltro = jTextField2.getText().trim();

        String sql = "SELECT Folio, CodigoBarras, Producto, PrecioVenta, Cantidad, TipoVenta, FechaVenta, MetodoPago, PrecioTotal, Cliente, Telefono, FechaInicio, FechaTermino "
                + "FROM ventasfecha "
                + "WHERE TipoVenta LIKE ? OR Producto LIKE ?";

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"Folio", "CodigoBarras", "Producto", "PrecioVenta", "Cantidad", "TipoVenta", "FechaVenta", "MetodoPago", "PrecioTotal", "Cliente", "Telefono", "FechaInicio", "FechaTermino"}, 0
        );

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            String filtro = "%" + textoFiltro + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("Folio"),
                        rs.getInt("CodigoBarras"),
                        rs.getString("Producto"),
                        df.format(rs.getDouble("PrecioVenta")),
                        rs.getInt("Cantidad"),
                        rs.getString("TipoVenta"),
                        rs.getDate("FechaVenta"),
                        rs.getString("MetodoPago"),
                        df.format(rs.getDouble("PrecioTotal")),
                        rs.getString("Cliente"),
                        rs.getString("Telefono"),
                        rs.getDate("FechaInicio"),
                        rs.getDate("FechaTermino")
                    });
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();
            recalcularTotalVenta();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jTextField2KeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        cargarTabla();
        recalcularTotalVenta();
    }//GEN-LAST:event_formWindowOpened

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
            java.util.logging.Logger.getLogger(VentasFechas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentasFechas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentasFechas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentasFechas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        try {
            FlatIntelliJLaf.setup();
            UIManager.put("Component.arc", 30);
            UIManager.put("TextComponent.arc", 30);
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.borderColor", new Color(80, 80, 80));
            UIManager.put("Button.borderColor", new Color(80, 80, 80));
            UIManager.put("defaultFont", new java.awt.Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentasFechas().setVisible(true);
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
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton btnMenuSupervisor;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
