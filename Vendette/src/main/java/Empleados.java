
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
public class Empleados extends javax.swing.JFrame {

    private ImageIcon imagen;
    private Icon icocno;
    private boolean modoCapturaManual = true;
    private String puesto;

    public Empleados() {
        initComponents();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/empleado.png"));
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
                        Empleados.this,
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

        jTextField3.addActionListener(e -> {
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
                jTextField1.requestFocus();
            }
        });

        jTextField1.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField5.requestFocus();
            }
        });

        jTextField5.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField7.requestFocus();
            }
        });

        jTextField7.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox2.showPopup();
            }
        });

        jComboBox2.addActionListener(e -> {
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
        jTextField3.setText(obtenerValorDeTabla(fila, 1));
        jTextField2.setText(obtenerValorDeTabla(fila, 2));
        jTextField4.setText(obtenerValorDeTabla(fila, 3));
        jTextField1.setText(obtenerValorDeTabla(fila, 4));
        jTextField5.setText(obtenerValorDeTabla(fila, 5));
        jTextField7.setText(obtenerValorDeTabla(fila, 6));
        jComboBox2.setSelectedItem(obtenerValorDeTabla(fila, 7));
        jComboBox1.setSelectedItem(obtenerValorDeTabla(fila, 8));
        modoCapturaManual = true;
    }

    private String obtenerValorDeTabla(int fila, int columna) {
        Object valor = jTable1.getValueAt(fila, columna);
        return (valor != null) ? valor.toString() : "";
    }

    public void cargarTabla() {
        final String sql = "SELECT * FROM empleados";
        final String[] columnas = {"Id_Empleado", "Nombre", "Puesto", "Telefono", "Correo", "Direccion", "Tiempo_Laboral", "Tipo", "Status"};
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

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(Id_Empleado) FROM empleados"); ResultSet rs = pst.executeQuery()) {

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
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String Id_Empleado = jTable1.getValueAt(filaSeleccionada, 0).toString();

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                PreparedStatement pst = cn.prepareStatement("DELETE FROM empleados WHERE Id_Empleado=?");
                pst.setString(1, Id_Empleado);
                pst.executeUpdate();
                limpiarCampos();

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
            mostrarMensaje("Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos vacíos
        if (validarCamposVacios() || !validarTelefono()) {
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de actualizar este registro?",
                "Confirmar Actualización",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int Id_Empleado = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());
            String correo = jTextField1.getText().trim();
            String puesto = jTextField2.getText().trim();
            String nombre = jTextField3.getText().trim();
            String telefono = jTextField4.getText().trim();
            String direccion = jTextField5.getText().trim();
            String tiempo = jTextField7.getText().trim();
            String tipo = jComboBox2.getSelectedItem().toString();
            String status = jComboBox1.getSelectedItem().toString();

            String sql = "UPDATE empleados SET Nombre = ?, Puesto = ?, Telefono = ?, Correo = ?, Direccion = ?, Tiempo_Laboral = ?, Tipo = ?, Status = ? WHERE Id_Empleado = ?";

            try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
                pst.setString(1, nombre);
                pst.setString(2, puesto);
                pst.setString(3, telefono);
                pst.setString(4, correo); // lo manejamos como String para evitar errores
                pst.setString(5, direccion);
                pst.setString(6, tiempo);
                pst.setString(7, tipo);
                pst.setString(8, status);
                pst.setInt(9, Id_Empleado);

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

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipo);
    }

    private void seleccionarRegistroPorId(int idEmpleado) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Integer.parseInt(model.getValueAt(i, 0).toString()) == idEmpleado) {
                jTable1.setRowSelectionInterval(i, i);
                jTable1.scrollRectToVisible(jTable1.getCellRect(i, 0, true));
                break;
            }
        }
    }

    private void centrarColumnas() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centrado);
            jTable1.getColumnModel().getColumn(8).setCellRenderer(new ColorearStatus3());
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
            int Id_Empleado = generarNuevoIdAsistencia();
            String correo = jTextField1.getText().trim();
            String puesto = jTextField2.getText().trim();
            String nombre = jTextField3.getText().trim();
            String telefono = jTextField4.getText().trim();
            String direccion = jTextField5.getText().trim();
            String tiempo = jTextField7.getText().trim();
            String tipo = jComboBox2.getSelectedItem().toString();
            String status = jComboBox1.getSelectedItem().toString();

            String sql = "INSERT INTO empleados (Id_Empleado, Nombre, Puesto, Telefono, Correo, Direccion,  Tiempo_Laboral, Tipo, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                pst.setInt(1, Id_Empleado);
                pst.setString(2, nombre);
                pst.setString(3, puesto);
                pst.setString(4, telefono);
                pst.setString(5, correo);
                pst.setString(6, direccion);
                pst.setString(7, tiempo);
                pst.setString(8, tipo);
                pst.setString(9, status);

                if (pst.executeUpdate() > 0) {
                    cargarTabla();
                    centrarColumnas();
                    limpiarCampos();
                    seleccionarRegistroPorId(Id_Empleado);

                } else {
                }
            }

        } catch (SQLException ex) {
        } catch (Exception ex) {
        }
    }

    private boolean validarCamposVacios() {
        JTextField[] campos = {jTextField1, jTextField2, jTextField3, jTextField4, jTextField5, jTextField7};
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

    private void limpiarCampos() {
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField5.setText("");
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
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
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
        jLabel2.setText("CATALOGO-EMPLEADOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 50, 200, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Nombre del Empleado");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 110, 200, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Puesto de Trabajo");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Numero de Telefono");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 170, 200, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Correo Electronico");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, -1, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 230, 290, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Direccion");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, -1, -1));

        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 50, 330, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Tiempo Laboral");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 110, -1, -1));

        jTextField7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 110, 180, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Tipo de Empleado");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 170, -1, -1));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Externo", "Interno" }));
        getContentPane().add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 170, 180, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Status");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 230, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Esta Disponible", "No esta Disponible" }));
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 230, 200, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Buscador");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 170, -1, -1));

        jTextField6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 170, 110, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 180, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Editar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 290, 190, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 290, 200, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 290, 250, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id_Empleado", "Nombre", "Puesto ", "Telefono", "Correo", "Direccion", "Tiempo_Laboral", "Tipo", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 1260, 300));

        btnMenuCapturista.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuCapturista.setText("Menu del Supervisor");
        btnMenuCapturista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuCapturistaActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuCapturista, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 220, 240, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1290, 660));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        String Nombre = jTextField6.getText().trim();
        final String BASE_SQL = "SELECT Id_Empleado, Nombre, Puesto, Telefono, Correo, Direccion, Tiempo_Laboral, Tipo, Status FROM empleados";
        boolean tieneParametro = !Nombre.isEmpty();

        StringBuilder sqlBuilder = new StringBuilder(BASE_SQL);
        if (tieneParametro) {
            sqlBuilder.append(" WHERE Nombre LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel();
        jTable1.setModel(modelo);

        // Definir las columnas del modelo de la tabla
        String[] columnas = {"Id_Empleado", "Nombre", "Puesto", "Telefono", "Correo", "Direccion", "Tiempo_Laboral", "Tipo", "Status"};
        for (String columna : columnas) {
            modelo.addColumn(columna);
        }

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sqlBuilder.toString())) {

            // Si hay parámetro, establecerlo en la consulta
            if (tieneParametro) {
                ps.setString(1, "%" + Nombre + "%");
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
                 ocultarIdDetalle();
            }

        } catch (SQLException ex) {
        }
    }//GEN-LAST:event_jTextField6KeyReleased

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
            java.util.logging.Logger.getLogger(Empleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Empleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Empleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Empleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            System.err.println("No se pudo aplicar FlatLaf");
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Empleados().setVisible(true);
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
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
