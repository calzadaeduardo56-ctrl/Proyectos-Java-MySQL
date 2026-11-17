
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public class Costos extends javax.swing.JFrame {

    private ImageIcon imagen;
    private Icon icocno;
    private boolean modoCapturaManual = true;
    private String empresaFiltrada;
    private String puesto;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public Costos() {
        initComponents();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/dolar.png"));
        setIconImage(logoIcon.getImage());

        jButton1.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton2.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/sobresalir.png", 30, 30));

        jButton5.setIcon(getIconUrl("/imagen/adjunto.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        try {
            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar filas existentes sin eliminar columnas

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            // ✅ Consulta ajustada a los campos que aparecen en tu imagen
            String sql = "SELECT idasistencia, Producto, Num_Etapas, Actividades, Empleado, Empresa, Dias_Retraso, Monitoreo FROM asistencia";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idasistencia = rs.getInt("idasistencia");
                String producto = rs.getString("Producto") != null ? rs.getString("Producto") : "";
                int numEtapas = rs.getInt("Num_Etapas");
                String actividades = rs.getString("Actividades") != null ? rs.getString("Actividades") : "";
                String empleado = rs.getString("Empleado") != null ? rs.getString("Empleado") : "";
                String empresa = rs.getString("Empresa") != null ? rs.getString("Empresa") : "";
                int diasRetraso = rs.getInt("Dias_Retraso");
                String monitoreo = rs.getString("Monitoreo") != null ? rs.getString("Monitoreo") : "";

                // ✅ Insertar en tabla costos sin la columna "Responsable"
                String sqlInsert = "INSERT INTO finanzas (idasistencia, Producto, Num_Etapas, Actividades, Empleado, Empresa, Dias_Retraso, Monitoreo) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "Producto = VALUES(Producto), "
                        + "Num_Etapas = VALUES(Num_Etapas), "
                        + "Actividades = VALUES(Actividades), "
                        + "Empleado = VALUES(Empleado), "
                        + "Empresa = VALUES(Empresa), "
                        + "Dias_Retraso = VALUES(Dias_Retraso), "
                        + "Monitoreo = VALUES(Monitoreo)";

                PreparedStatement psInsert = cn.prepareStatement(sqlInsert);
                psInsert.setInt(1, idasistencia);
                psInsert.setString(2, producto);
                psInsert.setInt(3, numEtapas);
                psInsert.setString(4, actividades);
                psInsert.setString(5, empleado);
                psInsert.setString(6, empresa);
                psInsert.setInt(7, diasRetraso);
                psInsert.setString(8, monitoreo);
                psInsert.executeUpdate();
                psInsert.close();
                centrarColumnas();

                // ✅ Agregar fila al JTable con las columnas que aparecen en la imagen
                modelo.addRow(new Object[]{
                    idasistencia, producto, numEtapas, actividades, empleado, empresa, diasRetraso, monitoreo
                });
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        jTextField1.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField3.requestFocus();
            }
        });

        jTextField3.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField4.requestFocus();
            }
        });

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        ocultarIdDetalle();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        Costos.this,
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
        jTextField1.setText(obtenerValorDeTabla(fila, 8));
        jTextField3.setText(obtenerValorDeTabla(fila, 9));
        jTextField4.setText(obtenerValorDeTabla(fila, 10));
        modoCapturaManual = true;
    }

    private String obtenerValorDeTabla(int fila, int columna) {
        Object valor = jTable1.getValueAt(fila, columna);
        return (valor != null) ? valor.toString() : "";
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos);
    }

    private void pedirEmpleadoYEnviar(ActionEvent e) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JTextField campoEmpleado = new JTextField(15);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Nombre del director:"));
        inputPanel.add(campoEmpleado);

        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        JList<String> listaSugerencias = new JList<>(modeloLista);
        JScrollPane scrollSugerencias = new JScrollPane(listaSugerencias);
        scrollSugerencias.setPreferredSize(new Dimension(300, 80));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollSugerencias, BorderLayout.CENTER);

        // Mapa nombre -> teléfono
        Map<String, String> mapaEmpleadoTelefono = new HashMap<>();

        campoEmpleado.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }

            public void removeUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }

            public void changedUpdate(DocumentEvent e) {
                actualizarSugerencias();
            }

            private void actualizarSugerencias() {
                modeloLista.clear();
                mapaEmpleadoTelefono.clear();

                String texto = campoEmpleado.getText().trim();
                if (texto.length() >= 2) {
                    try (Connection cn = new conexion().conectar()) {
                        PreparedStatement pst = cn.prepareStatement("SELECT Nombre, Telefono FROM empleados WHERE Nombre LIKE ?");
                        pst.setString(1, "%" + texto + "%");

                        ResultSet rs = pst.executeQuery();
                        while (rs.next()) {
                            String nombre = rs.getString("Nombre");
                            String telefono = rs.getString("Telefono");
                            String entrada = nombre + " - " + telefono;

                            modeloLista.addElement(entrada);
                            mapaEmpleadoTelefono.put(nombre, telefono);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        listaSugerencias.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String seleccionado = listaSugerencias.getSelectedValue();
                    if (seleccionado != null) {
                        String[] partes = seleccionado.split(" - ");
                        if (partes.length == 2) {
                            campoEmpleado.setText(partes[0]); // Solo nombre
                        }
                        modeloLista.clear();
                    }
                }
            }
        });

        int resultado = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Seleccione el empleado",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String nombreEmpleado = campoEmpleado.getText().trim();
        String telefonoEmpleado = mapaEmpleadoTelefono.get(nombreEmpleado);

        if (telefonoEmpleado == null || telefonoEmpleado.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontró un número asociado a este empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener saludo dinámico según la hora del sistema
        String saludo;
        int horaActual = LocalTime.now().getHour();

        if (horaActual < 12) {
            saludo = "Buenos días";
        } else if (horaActual < 19) {
            saludo = "Buenas tardes";
        } else {
            saludo = "Buenas noches";
        }

        String numeroCompleto = "52" + telefonoEmpleado;
        String mensaje = saludo + " " + nombreEmpleado + ", este es el reporte de finanzas.";

        String url = "https://wa.me/" + numeroCompleto + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public double recalcularTotalDiasPorEmpresa() {

        double totalCostos = 0.0;

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        int columnaEmpresa = 5;
        int columnaCostos = 11;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object empresa = model.getValueAt(i, columnaEmpresa);
            Object costo = model.getValueAt(i, columnaCostos);

            if (empresa != null && costo != null
                    && empresaFiltrada.trim().equalsIgnoreCase(empresa.toString().trim())) {

                try {

                    String valor = costo.toString()
                            .replace("$", "")
                            .replace(",", "")
                            .trim();

                    totalCostos += Double.parseDouble(valor);

                } catch (NumberFormatException e) {
                    System.out.println("Error convirtiendo costo: " + costo);
                }
            }
        }

        totalCostos = Math.round(totalCostos * 100.0) / 100.0;

        if (totalCostos == 0) {
            jLabel6.setText("No hay costos registrados para " + empresaFiltrada);
        } else {
            jLabel6.setText("Suma de costos de " + empresaFiltrada + " = $"
                    + String.format("%,.2f", totalCostos) + " pesos.");
        }

        return totalCostos;
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
            jTable1.getColumnModel().getColumn(7).setCellRenderer(new Colorear_Status());
        }
    }

    public void actualizarRegistro() {
        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idasistencia = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());
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

        String texto = jTextField1.getText().trim().replace(",", ".");
        double costo1 = Double.parseDouble(texto);

        String texto1 = jTextField3.getText().trim().replace(",", ".");
        double costo2 = Double.parseDouble(texto1);

        String texto2 = jTextField4.getText().trim().replace(",", ".");
        double costo3 = Double.parseDouble(texto2);

        double total = costo1 + costo2 + costo3;

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String sql = "UPDATE finanzas SET Costo_1 = ?, Costo_2 = ?, Costo_3 = ?, Total = ? WHERE idasistencia = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setDouble(1, costo1);
            ps.setDouble(2, costo2);
            ps.setDouble(3, costo3);
            ps.setDouble(4, total);
            ps.setInt(5, idasistencia);
            ps.executeUpdate();
            ps.close();
            cn.close();
            cargarTabla();
            centrarColumnas();
            recalcularTotalDiasPorEmpresa();

            // Siempre reiniciamos los JTextField con 0
            jTextField1.setText("0");
            jTextField3.setText("0");
            jTextField4.setText("0");

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

            String sql = "SELECT idasistencia, Producto, Num_Etapas, Actividades, Empleado, Empresa, Dias_Retraso, "
                    + "Monitoreo, Costo_1, Costo_2, Costo_3, Total FROM finanzas";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idAsistencia = rs.getInt("idasistencia");
                String producto = rs.getString("Producto") != null ? rs.getString("Producto") : "";
                int numEtapas = rs.getInt("Num_Etapas");
                String actividades = rs.getString("Actividades") != null ? rs.getString("Actividades") : "";
                String empleado = rs.getString("Empleado") != null ? rs.getString("Empleado") : "";
                String empresa = rs.getString("Empresa") != null ? rs.getString("Empresa") : "";
                int diasRetraso = rs.getInt("Dias_Retraso");
                String monitoreo = rs.getString("Monitoreo") != null ? rs.getString("Monitoreo") : "";
                double costo1 = rs.getObject("Costo_1") != null ? rs.getDouble("Costo_1") : 0;
                double costo2 = rs.getObject("Costo_2") != null ? rs.getDouble("Costo_2") : 0;
                double costo3 = rs.getObject("Costo_3") != null ? rs.getDouble("Costo_3") : 0;
                double total = rs.getObject("Total") != null ? rs.getDouble("Total") : 0;

                modelo.addRow(new Object[]{
                    idAsistencia,
                    producto,
                    numEtapas,
                    actividades,
                    empleado,
                    empresa,
                    diasRetraso,
                    monitoreo,
                    String.format("%.2f", costo1),
                    String.format("%.2f", costo2),
                    String.format("%.2f", costo3),
                    String.format("%.2f", total)
                });
            }

            rs.close();
            ps.close();
            ocultarIdDetalle();
            cn.close();

            // Inicializamos los JTextField con 0 al cargar tabla
            jTextField1.setText("0");
            jTextField3.setText("0");
            jTextField4.setText("0");

        } catch (SQLException ex) {
            System.out.println("Error al cargar tabla: " + ex.getMessage());
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

            String IdCostos = jTable1.getValueAt(filaSeleccionada, 0).toString();

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                PreparedStatement pst = cn.prepareStatement("DELETE FROM finanzas WHERE idasistencia=?");
                pst.setString(1, IdCostos);
                pst.executeUpdate();
                recalcularTotalDiasPorEmpresa();
                limpiarCampos();

                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                modelo.removeRow(filaSeleccionada);

            }
        } catch (SQLException ex) {
        }
    }

    public void setEmpresaFiltrada(String empresa) {
        this.empresaFiltrada = empresa;
        cargarRegistrosFinanzasPorEmpresa(); // 🔹 carga al asignar
        recalcularTotalDiasPorEmpresa();
    }

    public void cargarRegistrosFinanzasPorEmpresa() {

        String sql = "SELECT idasistencia, Producto, Num_Etapas, Actividades, Empleado, Empresa, Dias_Retraso, Monitoreo, Costo_1, Costo_2, Costo_3, Total "
                + "FROM finanzas WHERE Empresa LIKE ?";

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{
                    "idasistencia", "Producto", "Num_Etapas", "Actividades",
                    "Empleado", "Empresa", "Días_Retraso", "Monitoreo",
                    "Costo_1", "Costo_2", "Costo_3", "Total"
                }, 0
        );

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + empresaFiltrada.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsMd = rs.getMetaData();
                int columnas = rsMd.getColumnCount();

                while (rs.next()) {
                    Object[] fila = new Object[columnas];

                    for (int i = 0; i < columnas; i++) {
                        Object valor = rs.getObject(i + 1);

                        // Formato especial para campos de costos y total
                        if (i >= 8 && i <= 11 && valor != null) {
                            try {
                                double monto = Double.parseDouble(valor.toString());
                                fila[i] = String.format("%.2f", monto);
                            } catch (NumberFormatException e) {
                                fila[i] = valor;
                            }
                        } else {
                            fila[i] = valor;
                        }
                    }
                    modelo.addRow(fila);
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();
            ocultarIdDetalle();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar registros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ocultarIdDetalle() {
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private void limpiarCampos() {
        jTextField1.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        btnMenuContador = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("REPORTE DE FINANZAS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, -1, -1));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "idasistencia", "Producto", "Num_Etapas", "Actividades", "Empleado", "Empresa", "Dias_Retraso", "Monitoreo", "Costo_1", "Costo_2", "Costo_3", "Total"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 1340, 290));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 60, 170, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Costo 1");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Eliminar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 180, 190, 40));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Editar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 210, 40));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Buscador");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 130, 180, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 120, 170, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Costo 2");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 180, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Costo 3");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 80, 720, 40));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Exportacion");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 180, 190, 40));

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setText("Enviar Reporte");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 180, 210, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 130, 250, 40));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Contador");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 180, 250, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 1370, 540));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        String textoFiltro = jTextField2.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT idasistencia, Producto, Num_Etapas, Actividades, Empleado, Empresa, Dias_Retraso, Monitoreo, Costo_1, Costo_2, Costo_3, Total FROM finanzas");
        boolean tieneTexto = !textoFiltro.isEmpty();

        if (tieneTexto) {
            sql.append(" WHERE Empleado LIKE ? OR Empresa LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"idasistencia", "Producto", "Num_Etapas", "Actividades", "Empleado", "Empresa", "Dias_Retraso", "Monitoreo", "Costo_1", "Costo_2", "Costo_3", "Total"}, 0);

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            if (tieneTexto) {
                String filtro = "%" + textoFiltro + "%";
                ps.setString(1, filtro); // Empresa
                ps.setString(2, filtro); // Empleado
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("idasistencia"),
                        rs.getString("Producto"),
                        rs.getInt("Num_Etapas"),
                        rs.getString("Actividades"),
                        rs.getString("Empleado"),
                        rs.getString("Empresa"),
                        rs.getInt("Dias_Retraso"),
                        rs.getString("Monitoreo"),
                        df.format(rs.getDouble("Costo_1")),
                        df.format(rs.getDouble("Costo_2")),
                        df.format(rs.getDouble("Costo_3")),
                        df.format(rs.getDouble("Total"))

                    });
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();
            ocultarIdDetalle();
            recalcularTotalDiasPorEmpresa();

        } catch (SQLException ex) {
        }
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (empresaFiltrada != null) {
            cargarRegistrosFinanzasPorEmpresa();
        } else {
            cargarTabla();
        }
        centrarColumnas();
        ocultarIdDetalle();
    }//GEN-LAST:event_formWindowOpened

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            // Suponiendo que tienes almacenado el nombre de la empresa filtrada en una variable
            String empresa = empresaFiltrada; // o podrías tomarlo desde un JTextField, ej: jTextFieldEmpresa.getText()

            // Crear objeto con el nombre de la empresa
            ExportarExcel3 obj = new ExportarExcel3(empresa);

            // Exportar la tabla
            obj.exportarExcel3(jTable1);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar el archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        pedirEmpleadoYEnviar(null);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnMenuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuModulosActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuModulosActionPerformed

    private void btnMenuContadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuContadorActionPerformed
        MenuContador menu = new MenuContador();
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
            java.util.logging.Logger.getLogger(Costos.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Costos.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Costos.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Costos.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new Costos().setVisible(true);
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
            e.printStackTrace();
        }
        this.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMenuContador;
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
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
