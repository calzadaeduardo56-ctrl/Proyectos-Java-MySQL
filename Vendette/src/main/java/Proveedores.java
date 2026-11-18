
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
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
public class Proveedores extends javax.swing.JFrame {

    private ImageIcon imagen;
    private Icon icocno;
    private boolean modoCapturaManual = true;
    private String puesto;

    public Proveedores() {
        initComponents();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/partnership.png"));
        setIconImage(logoIcon.getImage());

        jButton1.setIcon(getIconUrl("/imagen/diskette.png", 30, 30));

        jButton2.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        btnMenuCapturista.setIcon(getIconUrl("/imagen/menu1.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        Proveedores.this,
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

        cargarTabla();

        centrarColumnas();

        ocultarIdDetalle();

        jTextField1.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField2.requestFocus();
            }
        });

        jTextField2.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField4.requestFocus();
            }
        });

        jTextField4.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField7.requestFocus();
            }
        });

        jTextField7.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField6.requestFocus();
            }
        });

        jTextField6.addActionListener(e -> {
            if (modoCapturaManual) {
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
        jTextField1.setText(obtenerValorDeTabla(fila, 1));
        jTextField2.setText(obtenerValorDeTabla(fila, 2));
        jTextField4.setText(obtenerValorDeTabla(fila, 3));
        jTextField7.setText(obtenerValorDeTabla(fila, 4));
        jTextField6.setText(obtenerValorDeTabla(fila, 5));
        jComboBox1.setSelectedItem(obtenerValorDeTabla(fila, 6));
        modoCapturaManual = true;
    }

    private String obtenerValorDeTabla(int fila, int columna) {
        Object valor = jTable1.getValueAt(fila, columna);
        return (valor != null) ? valor.toString() : "";
    }

    public void actualizarRegistro() {
        final int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            mostrarMensaje("Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos vacíos
        if (validarCamposVacios() || !validarTelefono()) {
            return;
        }

        final int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de actualizar este registro?",
                "Confirmar Actualización",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            final int IdCliente = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());
            final int clave = Integer.parseInt(jTextField1.getText().trim());
            final String nombre = jTextField2.getText().trim();
            final String telefono = jTextField4.getText().trim();
            final String correo = jTextField7.getText().trim();
            final String direccion = jTextField6.getText().trim();
            final String status = jComboBox1.getSelectedItem().toString();

            final String sql = "UPDATE proveedores SET Clave = ?, Nombre = ?, Telefono = ?, Correo = ?, Direccion = ?, Status = ? WHERE IdProveedores = ?";

            try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

                pst.setInt(1, clave);
                pst.setString(2, nombre);
                pst.setString(3, telefono);
                pst.setString(4, correo);
                pst.setString(5, direccion);
                pst.setString(6, status);
                pst.setInt(7, IdCliente);

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    cargarTabla();
                    centrarColumnas();
                } else {
                }
            }

        } catch (NumberFormatException ex) {
        } catch (SQLException ex) {
        }
    }

    public void cargarTabla() {
        final String sql = "SELECT IdProveedores, Clave, Nombre, Telefono, Correo, Direccion, Status FROM proveedores";
        final String[] columnas = {"IdProveedores", "Clave", "Nombre", "Telefono", "Correo", "Direccion", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        conexion conexion = new conexion(); // Asegúrate de que esta clase esté bien definida
        try (Connection conn = conexion.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String[] fila = new String[columnas.length];
                for (int i = 0; i < columnas.length; i++) {
                    fila[i] = rs.getString(columnas[i]);
                }
                modelo.addRow(fila);
            }

            jTable1.setModel(modelo);
            ocultarIdDetalle();

        } catch (SQLException ex) {
        }
    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    private int generarNuevoIdAsistencia() {
        int nuevoId = 1; // Por defecto empezamos en 1

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(IdProveedores) FROM proveedores"); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                nuevoId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
        }
        return nuevoId;
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

            String IdCliente = jTable1.getValueAt(filaSeleccionada, 0).toString();

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                PreparedStatement pst = cn.prepareStatement("DELETE FROM proveedores WHERE IdProveedores=?");
                pst.setString(1, IdCliente);
                pst.executeUpdate();
                limpiarCampos();

                // Eliminar del modelo asociado a jTable2
                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                modelo.removeRow(filaSeleccionada);
            }
        } catch (SQLException ex) {
        }

    }

    private void centrarColumnas() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centrado);
            jTable1.getColumnModel().getColumn(6).setCellRenderer(new ColorearStatus3());
        }
    }

    public void guardarRegistro() {
        if (validarCamposVacios() || !validarTelefono()) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Desea guardar este registro?",
                "Confirmar Registro",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection cn = new conexion().conectar()) {
            final int IdCliente = generarNuevoIdAsistencia();
            final int clave = Integer.parseInt(jTextField1.getText().trim());
            final String nombre = jTextField2.getText().trim();
            final String telefono = jTextField4.getText().trim();
            final String correo = jTextField7.getText().trim();
            final String direccion = jTextField6.getText().trim();
            final String status = jComboBox1.getSelectedItem().toString();

            final String sql = "INSERT INTO proveedores (IdProveedores, Clave, Nombre, Telefono, Correo, Direccion, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                pst.setInt(1, IdCliente);
                pst.setInt(2, clave);
                pst.setString(3, nombre);
                pst.setString(4, telefono);
                pst.setString(5, correo);
                pst.setString(6, direccion);
                pst.setString(7, status);

                if (pst.executeUpdate() > 0) {
                    cargarTabla();
                    centrarColumnas();
                    limpiarCampos();
                    seleccionarRegistroPorId(IdCliente);
                }
            }

        } catch (SQLException ex) {
        } catch (Exception ex) {
        }
    }

    private boolean validarCamposVacios() {
        JTextField[] campos = {jTextField1, jTextField2, jTextField4, jTextField6, jTextField7};
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
            new Timer(2000, evt -> {
                for (JTextField campo : campos) {
                    campo.putClientProperty("JComponent.outline", null);
                    campo.repaint();
                    campo.revalidate();
                }
            }).start();
        }

        return hayVacios;
    }

    private boolean validarTelefono() {
        // Eliminar todos los espacios del campo
        String telefono = jTextField4.getText().replaceAll("\\s+", "");

        // Actualizar el campo visualmente con el texto limpio
        jTextField4.setText(telefono);

        // Verificar que tenga exactamente 10 dígitos numéricos
        if (!telefono.matches("\\d{10}")) {
            mostrarMensaje("El número de teléfono debe contener exactamente 10 dígitos sin letras ni símbolos.", "Teléfono inválido", JOptionPane.ERROR_MESSAGE);
            jTextField4.requestFocus();
            jTextField4.selectAll();
            return false;
        }

        return true;
    }

    private void seleccionarRegistroPorId(int IdCliente) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Integer.parseInt(model.getValueAt(i, 0).toString()) == IdCliente) {
                jTable1.setRowSelectionInterval(i, i);
                jTable1.scrollRectToVisible(jTable1.getCellRect(i, 0, true));
                break;
            }
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipo);
    }

    private void limpiarCampos() {
        jTextField2.setText("");
        jTextField6.setText("");
        jTextField4.setText("");
        jTextField7.setText("");
        jTextField1.setText("");
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuCapturista);
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
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnMenuCapturista = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("CATALOGO-PROVEEDORES");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Clave");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 220, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Nombre");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 220, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Telefono");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 70, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 220, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Correo");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 70, -1));

        jTextField7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, 260, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Direccion");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 80, -1));

        jTextField6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 126, 260, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Status");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 190, 60, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Esta Disponible", "No esta Disponible" }));
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 190, 240, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Busqueda de Proveedores");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 130, -1, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 130, 150, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 180, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Editar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 250, 180, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 250, 170, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 250, 210, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "IdProveedor", "Clave", "Nombre", "Telefono", "Correo", "Direccion", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 1010, 290));

        btnMenuCapturista.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuCapturista.setText("Menu del Supervisor");
        btnMenuCapturista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuCapturistaActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuCapturista, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 190, 270, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1030, 610));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        String textoFiltro = jTextField3.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT IdProveedores, Clave, Nombre, Telefono, Correo, Direccion, Status FROM proveedores");
        boolean tieneTexto = !textoFiltro.isEmpty();

        if (tieneTexto) {
            sql.append(" WHERE Clave LIKE ? OR Nombre LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"IdProveedores", "Clave", "Nombre", "Telefono", "Correo", "Direccion", "Status"}, 0);

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
    }//GEN-LAST:event_jTextField3KeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        guardarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnMenuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuModulosActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuModulosActionPerformed

    private void btnMenuCapturistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuCapturistaActionPerformed
        MenuSupervisor menu = new MenuSupervisor();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuCapturistaActionPerformed

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
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Proveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

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
            public void run() {
                new Proveedores().setVisible(true);
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
    private javax.swing.JButton btnMenuCapturista;
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
