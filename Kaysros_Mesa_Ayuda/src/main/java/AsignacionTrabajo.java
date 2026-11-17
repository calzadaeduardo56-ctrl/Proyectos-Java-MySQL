
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class AsignacionTrabajo extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private String nombreEmpleadoFiltrado = null;
    private ImageIcon imagen;
    private Icon icocno;
    private String puesto;
    private boolean formularioCargado = false;

    public AsignacionTrabajo() {
        initComponents();
        
        agregarValidacionUsuario(Empleado, this::validarClienteDisponible);

        formularioCargado = true;

        RellenarCombos re = new RellenarCombos();
        
        re.RellenarComboBox("empleados", "Nombre", Empleado);

        this.setLocationRelativeTo(null); // Centrar la ventana

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        AsignacionTrabajo.this,
                        "¿Estás seguro de que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    dispose(); // Cierra la ventana
                }
                // Si elige NO, no hace nada y se queda en la ventana
            }
        });

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/asignacion-de-recursos.png"));
        setIconImage(logoIcon.getImage());

        jButton1.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        btnMenuSupervisor.setIcon(getIconUrl("/imagen/menu1.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        btnMenuSoporte.setIcon(getIconUrl("/imagen/menu-abierto.png", 30, 30));

        Empleado.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox2.showPopup();
            }
        });

        pintarImagen(jLabel1, "Imagen3.png");

        try {
            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar filas existentes sin eliminar columnas

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String sql = "SELECT Id_Ticket, Nombre_Cliente FROM alta_ticket";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idTicket = rs.getInt("Id_Ticket");
                String cliente = rs.getString("Nombre_Cliente") != null ? rs.getString("Nombre_Cliente") : "";

                // Insertar en la tabla asignacion (Evitar duplicados con ON DUPLICATE KEY UPDATE)
                String sqlInsert = "INSERT INTO asignacion (Id_Asignacion, Nombre_Cliente) "
                        + "VALUES (?, ?) "
                        + "ON DUPLICATE KEY UPDATE Nombre_Cliente = VALUES(Nombre_Cliente)";

                PreparedStatement psInsert = cn.prepareStatement(sqlInsert);
                psInsert.setInt(1, idTicket);
                psInsert.setString(2, cliente);
                psInsert.executeUpdate();
                psInsert.close();

                // Agregar fila al JTable
                modelo.addRow(new Object[]{idTicket, cliente});
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException ex) {
        } catch (Exception e) {
        }

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

    private void actualizarFormularioDesdeTabla(int fila) {
        Empleado.setSelectedItem(obtenerValorDeTabla(fila, 2));
        jComboBox2.setSelectedItem(obtenerValorDeTabla(fila, 5));
        modoCapturaManual = true;
    }

// También este método debe estar dentro de la clase
    private String obtenerValorDeTabla(int fila, int columna) {
        Object valor = jTable1.getValueAt(fila, columna);
        return (valor != null) ? valor.toString() : "";
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuSupervisor, btnMenuSoporte);
    }

    public void setEmpleadoFiltrado(String nombreEmpleado) {
        this.nombreEmpleadoFiltrado = nombreEmpleado;
    }

    public void cargarTicketsAsignados() {
        if (nombreEmpleadoFiltrado == null || nombreEmpleadoFiltrado.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se ha establecido un empleado para filtrar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT Id_Asignacion, Nombre_Cliente, Nombre_Empleado, Fecha_Asignacion, Hora_Asignacion, Prioridad "
                + "FROM asignacion WHERE Nombre_Empleado LIKE ?";

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"Id_Asignacion", "Nombre_Cliente", "Nombre_Empleado", "Fecha_Asignacion", "Hora_Asignacion", "Prioridad"}, 0
        );

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombreEmpleadoFiltrado.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsMd = rs.getMetaData();
                int columnas = rsMd.getColumnCount();

                while (rs.next()) {
                    Object[] fila = new Object[columnas];

                    for (int i = 0; i < columnas; i++) {
                        String columna = rsMd.getColumnLabel(i + 1);

                        if (columna.equalsIgnoreCase("Hora_Asignacion")) {
                            Time horaBD = rs.getTime(i + 1);
                            if (horaBD != null) {
                                LocalTime horaLocal = horaBD.toLocalTime();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                                fila[i] = horaLocal.format(formatter);
                            } else {
                                fila[i] = "";
                            }
                        } else {
                            fila[i] = rs.getObject(i + 1);
                        }
                    }
                    modelo.addRow(fila);
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar tickets: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                PreparedStatement pst = cn.prepareStatement("DELETE FROM asignacion WHERE Id_Asignacion=?");
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

        int idTicket = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());

        // 🔹 Confirmación antes de actualizar
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

        String empleado = (String) Empleado.getSelectedItem();
        String prioridad = (String) jComboBox2.getSelectedItem();

        // Obtener fecha y hora actuales
        java.sql.Date fechaActual = new java.sql.Date(System.currentTimeMillis());
        java.sql.Time horaActual = new java.sql.Time(System.currentTimeMillis());

        // Formatear hora a "hh:mm a"
        SimpleDateFormat formatoHora12 = new SimpleDateFormat("hh:mm a");
        String horaFormateada = formatoHora12.format(horaActual);

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String sqlUpdate = "UPDATE asignacion SET Nombre_Empleado = ?, Prioridad = ?, Fecha_Asignacion = ?, Hora_Asignacion = ? WHERE Id_Asignacion = ?";
            PreparedStatement ps = cn.prepareStatement(sqlUpdate);
            ps.setString(1, empleado);
            ps.setString(2, prioridad);
            ps.setDate(3, fechaActual);
            ps.setTime(4, horaActual);
            ps.setInt(5, idTicket);
            ps.executeUpdate();
            ps.close();
            cn.close();

            // Actualizar la fila en el JTable
            jTable1.setValueAt(empleado, filaSeleccionada, 2);
            jTable1.setValueAt(fechaActual.toString(), filaSeleccionada, 3);
            jTable1.setValueAt(horaFormateada, filaSeleccionada, 4);
            jTable1.setValueAt(prioridad, filaSeleccionada, 5);
            cargarTabla();
            centrarColumnas();

        } catch (SQLException ex) {
            System.out.println("Error SQL: " + ex.getMessage());
        }
    }

    public void cargarTabla() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar tabla

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String sql = "SELECT Id_Asignacion, Nombre_Cliente, Nombre_Empleado, Fecha_Asignacion, Hora_Asignacion, Prioridad FROM asignacion";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat formatoHora12 = new SimpleDateFormat("hh:mm a"); // Formato 12h con AM/PM

            while (rs.next()) {
                int idTicket = rs.getInt("Id_Asignacion");
                String cliente = rs.getString("Nombre_Cliente") != null ? rs.getString("Nombre_Cliente") : "";
                String empleado = rs.getString("Nombre_Empleado") != null ? rs.getString("Nombre_Empleado") : "";
                String prioridad = rs.getString("Prioridad") != null ? rs.getString("Prioridad") : "";
                String fecha = rs.getDate("Fecha_Asignacion") != null ? rs.getDate("Fecha_Asignacion").toString() : "";

                java.sql.Time horaSQL = rs.getTime("Hora_Asignacion");
                String hora = (horaSQL != null) ? formatoHora12.format(horaSQL) : "";

                modelo.addRow(new Object[]{idTicket, cliente, empleado, fecha, hora, prioridad});
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException ex) {
            System.out.println("Error al cargar tabla: " + ex.getMessage());
        }
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
            jTable1.getColumnModel().getColumn(5).setCellRenderer(new ColorearStatus());
        }
    }
    
    private void validarClienteDisponible() {

        String proveedorSeleccionado = Empleado.getSelectedItem().toString();

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
                            "Empleado no disponible",
                            JOptionPane.WARNING_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> {
                        Empleado.requestFocusInWindow();
                        Empleado.showPopup();
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
        jButton1 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Empleado = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnMenuSupervisor = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        btnMenuSoporte = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("ASIGNACION DE TRABAJO");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, -1, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Editar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, 160, 40));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Alta", "Media", "Baja" }));
        getContentPane().add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 110, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Prioridad");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Nombre del Empleado");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id_Asignacion", "Nombre_Cliente", "Nombre_Empleado", "Fecha_Asignacion", "Hora_Asignacion", "Prioridad"
            }
        ));
        jTable1.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 1130, 300));

        Empleado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(Empleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 210, -1));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Eliminar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 120, 160, 40));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 130, 110, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Buscador");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, -1, -1));

        btnMenuSupervisor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSupervisor.setText("Menu del Supervisor");
        btnMenuSupervisor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSupervisorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuSupervisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 70, 290, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 120, 290, 40));

        btnMenuSoporte.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSoporte.setText("Menu de Soporte Tecnico");
        btnMenuSoporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSoporteActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuSoporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 290, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1170, 480));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (nombreEmpleadoFiltrado != null) {
            cargarTicketsAsignados();
        } else {
            cargarTabla();
        }
        centrarColumnas();
    }//GEN-LAST:event_formWindowOpened

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String textoFiltro = jTextField1.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT Id_Asignacion, Nombre_Cliente, Nombre_Empleado, Fecha_Asignacion, Hora_Asignacion, Prioridad FROM asignacion");

        boolean tieneTexto = !textoFiltro.isEmpty();
        if (tieneTexto) {
            sql.append(" WHERE Nombre_Cliente LIKE ? OR Nombre_Empleado LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"Id_Asignacion", "Nombre_Cliente", "Nombre_Empleado", "Fecha_Asignacion", "Hora_Asignacion", "Prioridad"}, 0
        );

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            if (tieneTexto) {
                String filtro = "%" + textoFiltro + "%";
                ps.setString(1, filtro);
                ps.setString(2, filtro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsMd = rs.getMetaData();
                int columnas = rsMd.getColumnCount();

                while (rs.next()) {
                    Object[] fila = new Object[columnas];

                    for (int i = 0; i < columnas; i++) {
                        if (rsMd.getColumnLabel(i + 1).equalsIgnoreCase("Hora_Asignacion")) {
                            Time horaBD = rs.getTime(i + 1); // tipo java.sql.Time
                            if (horaBD != null) {
                                LocalTime horaLocal = horaBD.toLocalTime();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                                fila[i] = horaLocal.format(formatter);
                            } else {
                                fila[i] = "";
                            }
                        } else {
                            fila[i] = rs.getObject(i + 1);
                        }
                    }

                    modelo.addRow(fila);
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();
        } catch (SQLException ex) {
            ex.printStackTrace(); // Para depuración
        }

    }//GEN-LAST:event_jTextField1KeyReleased

    private void btnMenuSupervisorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSupervisorActionPerformed
        MenuSupervisor menu = new MenuSupervisor();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuSupervisorActionPerformed

    private void btnMenuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuModulosActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuModulosActionPerformed

    private void btnMenuSoporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSoporteActionPerformed
        MenuSoporte menu = new MenuSoporte();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuSoporteActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            FlatIntelliJLaf.setup();
            UIManager.put("TextComponent.arc", 30);
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.arc", 30);
            UIManager.put("Component.borderColor", new Color(80, 80, 80));
            UIManager.put("Button.borderColor", new Color(80, 80, 80));
            UIManager.put("defaultFont", new Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AsignacionTrabajo().setVisible(true);
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
    private javax.swing.JComboBox<String> Empleado;
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton btnMenuSoporte;
    private javax.swing.JButton btnMenuSupervisor;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
