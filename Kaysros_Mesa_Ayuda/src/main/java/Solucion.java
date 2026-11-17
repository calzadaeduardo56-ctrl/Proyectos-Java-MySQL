
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
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
import javax.swing.SwingConstants;
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
public class Solucion extends javax.swing.JFrame {

    private ImageIcon imagen;
    private Icon icocno;
    private String puesto;

    public Solucion() {
        initComponents();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/solucion.png"));
        setIconImage(logoIcon.getImage());

        jButton2.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        btnMenuSoporte.setIcon(getIconUrl("/imagen/menu-abierto.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                verificarStatusSeleccion();
            }
        });

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        Solucion.this,
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

        try {
            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar filas existentes sin eliminar columnas  

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            // Consulta combinada: traer Id_ticket y Descripcion de alta_ticket y Status de tomar
            String sql = "SELECT a.Id_Ticket, t.Nombre_Empleado, a.Nombre_Problematica, t.Status "
                    + "FROM alta_ticket a "
                    + "LEFT JOIN tomar t ON a.Id_Ticket = t.Id_Monitoreo";

            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idTicket = rs.getInt("Id_ticket");
                String empleado = rs.getString("Nombre_Empleado") != null ? rs.getString("Nombre_Empleado") : "";
                String problematica = rs.getString("Nombre_Problematica") != null ? rs.getString("Nombre_Problematica") : "";
                String status = rs.getString("Status") != null ? rs.getString("Status") : "";

                // Insertar o actualizar en la tabla solucion incluyendo Status
                String sqlInsert = "INSERT INTO solucion (Id_Solucion, Nombre_Empleado, Nombre_Problematica, Status) "
                        + "VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "Nombre_Empleado = VALUES(Nombre_Empleado), "
                        + "Nombre_Problematica = VALUES(Nombre_Problematica), "
                        + "Status = VALUES(Status)";

                PreparedStatement psInsert = cn.prepareStatement(sqlInsert);
                psInsert.setInt(1, idTicket);
                psInsert.setString(2, empleado);
                psInsert.setString(3, problematica);
                psInsert.setString(4, status);
                psInsert.executeUpdate();
                psInsert.close();

                // Agregar fila al JTable: [Id_ticket, Descripcion, "", Status]
                modelo.addRow(new Object[]{idTicket, empleado, problematica, "", status});
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException ex) {
        } catch (Exception e) {
        }
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuSoporte);
    }

    public void actualizarRegistro() {
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String solucion = jTextField2.getText().trim();
            int Id_ticket = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());

            int confirmacion = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de actualizar este registro?",
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                String sql = "UPDATE solucion SET Descripcion_Solucion = ? WHERE Id_Solucion = ?";

                try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
                    pst.setString(1, solucion);
                    pst.setInt(2, Id_ticket);

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
        final String sql = "SELECT Id_Solucion, Nombre_Empleado, Nombre_Problematica, Descripcion_Solucion, Status FROM solucion";
        final String[] columnas = {"Id_Solucion", "Nombre_Empleado", "Nombre_Problematica", "Descripcion_Solucion", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        conexion conexion = new conexion();
        try (Connection conn = conexion.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String[] fila = new String[columnas.length];
                for (int i = 0; i < columnas.length; i++) {
                    fila[i] = rs.getString(columnas[i]);
                }
                modelo.addRow(fila);
            }

            jTable1.setModel(modelo);

        } catch (SQLException ex) {
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
            jTable1.getColumnModel().getColumn(4).setCellRenderer(new Colorear_Status());
        }
    }

    private void verificarStatusSeleccion() {
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada != -1) {
            Object solucionObj = jTable1.getValueAt(filaSeleccionada, 3); // Columna 2 = Solucion
            Object statusObj = jTable1.getValueAt(filaSeleccionada, 4);   // Columna 3 = Status

            String solucion = (solucionObj != null) ? solucionObj.toString() : "";
            String status = (statusObj != null) ? statusObj.toString() : "";

            jTextField2.setText(solucion);

            if ("En Proceso".equalsIgnoreCase(status)) {
                jTextField2.setEditable(false);
                jTextField2.setBackground(Color.LIGHT_GRAY);
                JOptionPane.showMessageDialog(null,
                        "No puedes editar la solución porque el Status es 'En Proceso'.",
                        "Campo bloqueado",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                jTextField2.setEditable(true);
                jTextField2.setBackground(Color.WHITE);
            }
        }
    }

    public void eliminarRegistro() {
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String idSolucion = jTable1.getValueAt(filaSeleccionada, 0).toString();

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                PreparedStatement pst = cn.prepareStatement("DELETE FROM solucion WHERE Id_Solucion=?");
                pst.setString(1, idSolucion);
                pst.executeUpdate();
                limpiarCampos();

                // Eliminar del modelo asociado a jTable2
                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                modelo.removeRow(filaSeleccionada);
            }
        } catch (SQLException ex) {
        }
    }
    
    private void limpiarCampos() {
        jTextField2.setText("");
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
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnMenuSoporte = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("GESTION DE SOLUCIONES");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Descripcion de la Solucion");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 190, 30));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 430, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id_Solucion", "Nombre_Empleado", "Nombre_Problematica", "Descripcion_Solucion", "Status"
            }
        ));
        jTable1.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 970, 300));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Editar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 220, 40));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Eliminar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 210, 220, 40));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 180, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Buscador");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));

        btnMenuSoporte.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSoporte.setText("Menu de Soporte Tecnico");
        btnMenuSoporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSoporteActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuSoporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 140, 320, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 210, 240, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        cargarTabla();
        centrarColumnas();
    }//GEN-LAST:event_formWindowOpened

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String Cliente = jTextField1.getText().trim();
        final String BASE_SQL = "SELECT Id_Solucion, Nombre_Empleado, Nombre_Problematica, Descripcion_Solucion, Status FROM solucion";
        boolean tieneParametro = !Cliente.isEmpty();

        StringBuilder sqlBuilder = new StringBuilder(BASE_SQL);
        if (tieneParametro) {
            sqlBuilder.append(" WHERE Nombre_Empleado LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel();
        jTable1.setModel(modelo);

        // Definir las columnas del modelo de la tabla
        String[] columnas = {"Id_Solucion", "Nombre_Empleado", "Nombre_Problematica", "Descripcion_Solucion", "Status"};
        for (String columna : columnas) {
            modelo.addColumn(columna);
        }

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sqlBuilder.toString())) {

            // Si hay parámetro, establecerlo en la consulta
            if (tieneParametro) {
                ps.setString(1, "%" + Cliente + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsMd = rs.getMetaData();
                int cantidadColumnas = rsMd.getColumnCount();

                // Llenar la tabla con los resultados
                while (rs.next()) {
                    Object[] fila = new Object[cantidadColumnas];
                    for (int i = 0; i < cantidadColumnas; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    modelo.addRow(fila);
                }
                centrarColumnas();
            }

        } catch (SQLException ex) {
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void btnMenuSoporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSoporteActionPerformed
        MenuSoporte menu = new MenuSoporte();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuSoporteActionPerformed

    private void btnMenuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuModulosActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuModulosActionPerformed

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
            java.util.logging.Logger.getLogger(Solucion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Solucion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Solucion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Solucion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            @Override
            public void run() {
                new Solucion().setVisible(true);
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
    private javax.swing.JButton btnMenuSoporte;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
