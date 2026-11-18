
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
import java.sql.SQLIntegrityConstraintViolationException;
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
public class Gastos extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;
    private double totalVentaActual = 0.0;
    private boolean formularioCargado = false;
    private String puesto;

    public Gastos() {
        initComponents();

        agregarValidacionUsuario(jComboBox1, this::validarProveedorDisponible);
        agregarValidacionUsuario(jComboBox3, this::validarMetodoPagoDisponible);

        formularioCargado = true;

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("catgastos", "NombreGastos", jComboBox1);

        re.RellenarComboBox("metdodopago", "NombreMetodo", jComboBox3);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/gasto.png"));
        setIconImage(logoIcon.getImage());

        jButton2.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton1.setIcon(getIconUrl("/imagen/diskette.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        cargarTabla();

        centrarColumnas();
        
        ocultarIdDetalle();

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        Gastos.this,
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
                jTextField1.requestFocus();
            }
        });

        jTextField1.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox1.showPopup();
            }
        });

        jComboBox3.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField2.requestFocus();
            }
        });

        jTextField2.addActionListener(e -> {
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
        String FechaRegistro = obtenerValorDeTabla(fila, 1);
        if (!FechaRegistro.isEmpty()) {
            try {
                java.util.Date fechaReal = java.sql.Date.valueOf(FechaRegistro);
                jDateChooser1.setDate(fechaReal);
            } catch (IllegalArgumentException e) {
                jDateChooser1.setDate(null);
            }
        } else {
            jDateChooser1.setDate(null);
        }
        jTextField1.setText(obtenerValorDeTabla(fila, 2));
        jComboBox1.setSelectedItem(obtenerValorDeTabla(fila, 3));
        String FechaPago = obtenerValorDeTabla(fila, 4);
        if (!FechaPago.isEmpty()) {
            try {
                java.util.Date fechaReal = java.sql.Date.valueOf(FechaPago);
                jDateChooser2.setDate(fechaReal);
            } catch (IllegalArgumentException e) {
                jDateChooser2.setDate(null);
            }
        } else {
            jDateChooser2.setDate(null);
        }
        jComboBox3.setSelectedItem(obtenerValorDeTabla(fila, 5));
        jTextField2.setText(obtenerValorDeTabla(fila, 6));
        jTextField3.setText(obtenerValorDeTabla(fila, 7));
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
        }
    }

    public void guardarRegistro() {

        if (validarCamposVacios()) {
            return;
        }

        String Referencia, TipoGastos, MetodoPago, Comentarios;

        double PrecioGastos;

        int IdGastos = generarNuevoIdAsistencia();
        java.sql.Date FechaRegistro = new java.sql.Date(jDateChooser1.getDate().getTime());
        Referencia = jTextField1.getText().trim();
        TipoGastos = jComboBox1.getSelectedItem().toString();
        java.sql.Date FechaPago = new java.sql.Date(jDateChooser2.getDate().getTime());
        MetodoPago = jComboBox3.getSelectedItem().toString();
        Comentarios = jTextField2.getText().trim();
        String texto = jTextField3.getText().trim();
// Reemplaza coma por punto para que Double.parseDouble lo acepte
        texto = texto.replace(",", ".");
        PrecioGastos = Double.parseDouble(texto);

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Desea guardar este registro?",
                "Confirmar Registro",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "INSERT INTO gastos (IdGastos, FechaRegistro, Referencia, TipoGastos, FechaPago, MetodoPago, Comentarios, PrecioGastos) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

                pst.setInt(1, IdGastos);
                pst.setDate(2, FechaRegistro);
                pst.setString(3, Referencia);
                pst.setString(4, TipoGastos);
                pst.setDate(5, FechaPago);
                pst.setString(6, MetodoPago);
                pst.setString(7, Comentarios);
                pst.setDouble(8, PrecioGastos);

                int filasInsertadas = pst.executeUpdate();
                if (filasInsertadas > 0) {
                    cargarTabla();
                    centrarColumnas();
                    limpiarCampos();
                    // Buscar y seleccionar el nuevo registro en la tabla
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        int idTabla = Integer.parseInt(model.getValueAt(i, 0).toString());
                        if (idTabla == IdGastos) {
                            jTable1.setRowSelectionInterval(i, i);
                            jTable1.scrollRectToVisible(jTable1.getCellRect(i, 0, true));
                            break;
                        }
                    }
                }

            } catch (SQLIntegrityConstraintViolationException e) {

            } catch (SQLException e) {

            }

        }
    }

    private int generarNuevoIdAsistencia() {
        int nuevoId = 1; // Por defecto empezamos en 1

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(IdGastos) FROM gastos"); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                nuevoId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
        }
        return nuevoId;
    }

    private boolean validarCamposVacios() {
        JTextField[] campos = {jTextField1, jTextField2, jTextField3};
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
        jTextField1.setText("");
        jTextField3.setText("");
        jTextField2.setText("");
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
                "DELETE FROM gastos WHERE IdGastos = ?")) {

            pst.setInt(1, idnueva);
            pst.executeUpdate();

            // ❌ Eliminar fila visual
            modelo.removeRow(filaSeleccionada);

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

    public void actualizarRegistro() {

        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String Referencia, TipoGastos, MetodoPago, Comentarios;

            double PrecioGastos;

            java.sql.Date FechaRegistro = new java.sql.Date(jDateChooser1.getDate().getTime());
            Referencia = jTextField1.getText().trim();
            TipoGastos = jComboBox1.getSelectedItem().toString();
            java.sql.Date FechaPago = new java.sql.Date(jDateChooser2.getDate().getTime());
            MetodoPago = jComboBox3.getSelectedItem().toString();
            Comentarios = jTextField2.getText().trim();
            String texto = jTextField3.getText().trim();
// Reemplaza coma por punto para que Double.parseDouble lo acepte
            texto = texto.replace(",", ".");
            PrecioGastos = Double.parseDouble(texto);

            int IdGastos = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());

            int confirmacion = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de actualizar este registro?",
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                String sqlUpdate = "UPDATE gastos SET FechaRegistro = ?, Referencia = ?, TipoGastos = ?, FechaPago = ?, MetodoPago = ?, Comentarios = ?, PrecioGastos = ?  WHERE IdGastos = ?";
                try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sqlUpdate)) {
                    pst.setDate(1, FechaRegistro);
                    pst.setString(2, Referencia);
                    pst.setString(3, TipoGastos);
                    pst.setDate(4, FechaPago);
                    pst.setString(5, MetodoPago);
                    pst.setString(6, Comentarios);
                    pst.setDouble(7, PrecioGastos);
                    pst.setInt(8, IdGastos);

                    int filasAfectadas = pst.executeUpdate();
                    if (filasAfectadas > 0) {
                        cargarTabla();
                        centrarColumnas();
                    } else {
                    }
                }
            }
        } catch (NumberFormatException e) {

        } catch (SQLException e) {
        }
    }

    public void cargarTabla() {
        final String sql = "SELECT IdGastos, FechaRegistro, Referencia, TipoGastos, FechaPago, MetodoPago, Comentarios, PrecioGastos  FROM gastos";
        final String[] columnas = {"IdGastos", "FechaRegistro", "Referencia", "TipoGastos", "FechaPago", "MetodoPago", "Comentarios", "PrecioGastos"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        conexion conexion = new conexion();
        try (Connection conn = conexion.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String PrecioGasto = String.format("%.2f", rs.getDouble("PrecioGastos"));

                Object[] fila = {
                    rs.getInt("IdGastos"),
                    rs.getDate("FechaRegistro"),
                    rs.getString("Referencia"),
                    rs.getString("TipoGastos"),
                    rs.getDate("FechaPago"),
                    rs.getString("MetodoPago"),
                    rs.getString("Comentarios"),
                    PrecioGasto
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

        String sql = "SELECT Status FROM catgastos WHERE NombreGastos = ?";

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

        String proveedorSeleccionado = jComboBox3.getSelectedItem().toString();

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
                        jComboBox3.requestFocusInWindow();
                        jComboBox3.showPopup();
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
        ControlMenus.aplicarControlMenus(puesto, jButton4, btnMenuContador);
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
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnMenuContador = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("GASTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 6, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Fecha de Registro");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 200, 30));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 200, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Referencia");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 200, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Tipo de Gastos");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, -1));

        jDateChooser2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 230, 200, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Fecha de Pago");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, -1));

        jComboBox3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 50, 180, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Metodo de Pago");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 120, 270, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Comentarios");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 120, -1, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 170, 190, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Precio del Gasto");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, -1, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 230, 170, 30));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Buscador");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 230, -1, -1));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Menu de Modulos");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 290, 260, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 290, 210, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Editar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 290, 190, 40));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 190, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "IdGastos", "FechaRegistro", "Referencia", "TipoGastos", "FechaPago", "MetodoPago", "Comentarios", "PrecioGastos"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 980, 290));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Cajero");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 230, 260, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 650));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        guardarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        String textoFiltro = jTextField4.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT IdGastos, FechaRegistro, Referencia, TipoGastos, FechaPago, MetodoPago, Comentarios, PrecioGastos  FROM gastos");
        boolean tieneTexto = !textoFiltro.isEmpty();

        if (tieneTexto) {
            sql.append(" WHERE TipoGastos LIKE ? OR Referencia LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"IdGastos", "FechaRegistro", "Referencia", "TipoGastos", "FechaPago", "MetodoPago", "Comentarios", "PrecioGastos"}, 0);

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
            ocultarIdDetalle();

        } catch (SQLException ex) {
        }
    }//GEN-LAST:event_jTextField4KeyReleased

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
            java.util.logging.Logger.getLogger(Gastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Gastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Gastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Gastos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new Gastos().setVisible(true);
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
    private javax.swing.JComboBox<String> jComboBox3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
