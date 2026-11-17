
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public class ReporteDetallado extends javax.swing.JFrame {

    private ImageIcon imagen;
    private Icon icocno;
    private boolean modoCapturaManual = true;
    private String empresaFiltrada;
    private String puesto;
    private boolean formularioCargado = false;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public ReporteDetallado() {
        initComponents();

        JComboBox<?>[] combos = {jComboBox1, jComboBox2, jComboBox3, jComboBox4};

        for (JComboBox<?> combo : combos) {
            agregarValidacionUsuario(combo, () -> validarGastoDisponible(combo));
        }

        formularioCargado = true;

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("gastos", "NombreGastos", jComboBox1);

        re.RellenarComboBox("gastos", "NombreGastos", jComboBox2);

        re.RellenarComboBox("gastos", "NombreGastos", jComboBox3);

        re.RellenarComboBox("gastos", "NombreGastos", jComboBox4);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/extracto-de-cuenta.png"));
        setIconImage(logoIcon.getImage());

        jButton2.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/sobresalir.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        jButton5.setIcon(getIconUrl("/imagen/adjunto.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        try {
            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar filas existentes sin eliminar columnas

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            // ✅ Consulta ajustada a los campos que aparecen en tu imagen
            String sql = "SELECT idasistencia, Producto, Num_Etapas, Actividades, Empresa FROM asistencia";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int IdCosto = rs.getInt("idasistencia");
                String producto = rs.getString("Producto") != null ? rs.getString("Producto") : "";
                int numEtapas = rs.getInt("Num_Etapas");
                String actividades = rs.getString("Actividades") != null ? rs.getString("Actividades") : "";
                String empresa = rs.getString("Empresa") != null ? rs.getString("Empresa") : "";
                // ✅ Insertar en tabla costos sin la columna "Responsable"
                String sqlInsert = "INSERT INTO gastos2 (idasistencia, Producto, Num_Etapas, Actividades, Empresa) "
                        + "VALUES (?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "Producto = VALUES(Producto), "
                        + "Num_Etapas = VALUES(Num_Etapas), "
                        + "Actividades = VALUES(Actividades), "
                        + "Empresa = VALUES(Empresa)";

                PreparedStatement psInsert = cn.prepareStatement(sqlInsert);
                psInsert.setInt(1, IdCosto);
                psInsert.setString(2, producto);
                psInsert.setInt(3, numEtapas);
                psInsert.setString(4, actividades);
                psInsert.setString(5, empresa);
                psInsert.executeUpdate();
                psInsert.close();
                centrarColumnas();

                // ✅ Agregar fila al JTable con las columnas que aparecen en la imagen
                modelo.addRow(new Object[]{
                    IdCosto, producto, numEtapas, actividades, empresa
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

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        ReporteDetallado.this,
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

        jComboBox1.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField1.requestFocus();
            }
        });

        jTextField1.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox2.showPopup();
            }
        });

        jComboBox2.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField2.requestFocus();
            }
        });

        jTextField2.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox3.showPopup();
            }
        });

        jComboBox3.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField3.requestFocus();
            }
        });

        jTextField3.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox4.showPopup();
            }
        });

        jComboBox4.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField4.requestFocus();
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
        jComboBox1.setSelectedItem(obtenerValorDeTabla(fila, 5));
        jTextField1.setText(obtenerValorDeTabla(fila, 6));
        jComboBox2.setSelectedItem(obtenerValorDeTabla(fila, 7));
        jTextField2.setText(obtenerValorDeTabla(fila, 8));
        jComboBox3.setSelectedItem(obtenerValorDeTabla(fila, 9));
        jTextField3.setText(obtenerValorDeTabla(fila, 10));
        jComboBox4.setSelectedItem(obtenerValorDeTabla(fila, 11));
        jTextField4.setText(obtenerValorDeTabla(fila, 12));
        modoCapturaManual = true;
    }
// También este método debe estar dentro de la clase

    private String obtenerValorDeTabla(int fila, int columna) {
        Object valor = jTable1.getValueAt(fila, columna);
        return (valor != null) ? valor.toString() : "";
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuContador);
    }

    public double recalcularTotalDiasPorEmpresa() {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        int columnaEmpresa = 4;   // Empresa (columna 5 visual)
        int columnaCostos = 13;   // Costos (columna 11 visual)

        double totalCostos = 0.0;  // ✅ FALTABA ESTO

        for (int i = 0; i < jTable1.getRowCount(); i++) {

            int modelRow = jTable1.convertRowIndexToModel(i);

            Object empresa = model.getValueAt(modelRow, columnaEmpresa);
            Object costo = model.getValueAt(modelRow, columnaCostos);

            if (empresa != null && costo != null) {

                if (empresaFiltrada.trim()
                        .equalsIgnoreCase(empresa.toString().trim())) {

                    try {
                        String valorLimpio = costo.toString()
                                .replace("$", "")
                                .replace(",", "")
                                .trim();

                        totalCostos += Double.parseDouble(valorLimpio);

                    } catch (NumberFormatException e) {
                        System.out.println("Error convirtiendo: " + costo);
                    }
                }
            }
        } // ✅ EL FOR TERMINA AQUÍ

        totalCostos = Math.round(totalCostos * 100.0) / 100.0;

        // ✅ Ahora sí se evalúa después de recorrer todo
        if (totalCostos == 0) {
            jLabel12.setText("No hay costos registrados para " + empresaFiltrada);
        } else {
            jLabel12.setText("Suma de costos de " + empresaFiltrada + " = $"
                    + String.format("%,.2f", totalCostos) + " pesos.");
        }

        return totalCostos; // ✅ El return va fuera del for
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

        double costo1, costo2, costo3, costo4;
        String gasto1, gasto2, gasto3, gasto4;
        try {
            gasto1 = jComboBox1.getSelectedItem().toString();

            String texto = jTextField1.getText().trim().replace(",", ".");
            costo1 = Double.parseDouble(texto);

            gasto2 = jComboBox2.getSelectedItem().toString();

            String texto1 = jTextField2.getText().trim().replace(",", ".");
            costo2 = Double.parseDouble(texto1);

            gasto3 = jComboBox3.getSelectedItem().toString();

            String texto2 = jTextField3.getText().trim().replace(",", ".");
            costo3 = Double.parseDouble(texto2);

            gasto4 = jComboBox4.getSelectedItem().toString();

            String texto3 = jTextField4.getText().trim().replace(",", ".");
            costo4 = Double.parseDouble(texto3);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ingrese solo números válidos en los campos de costo.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double total = costo1 + costo2 + costo3 + costo4;

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String sql = "UPDATE gastos2 SET Gasto_1 = ?, Costo_1 = ?, Gasto_2 = ?, Costo_2 = ?, Gasto_3 = ?, Costo_3 = ?, Gasto_4 = ?, Costo_4 = ?, Total = ? WHERE idasistencia = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, gasto1);
            ps.setDouble(2, costo1);
            ps.setString(3, gasto2);
            ps.setDouble(4, costo2);
            ps.setString(5, gasto3);
            ps.setDouble(6, costo3);
            ps.setString(7, gasto4);
            ps.setDouble(8, costo4);
            ps.setDouble(9, total);
            ps.setInt(10, idasistencia);
            ps.executeUpdate();
            ps.close();
            cn.close();
            cargarTabla();
            centrarColumnas();

            // Siempre reiniciamos los JTextField con 0
            jTextField1.setText("0");
            jTextField2.setText("0");
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

            String sql = "SELECT idasistencia, Producto, Num_Etapas, Actividades, Empresa, "
                    + "Gasto_1, Costo_1, Gasto_2, Costo_2, Gasto_3, Costo_3, Gasto_4, Costo_4, Total FROM gastos2";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int IdCosto = rs.getInt("idasistencia");
                String producto = rs.getString("Producto") != null ? rs.getString("Producto") : "";
                int numEtapas = rs.getInt("Num_Etapas");
                String actividades = rs.getString("Actividades") != null ? rs.getString("Actividades") : "";
                String empresa = rs.getString("Empresa") != null ? rs.getString("Empresa") : "";
                String gasto1 = rs.getString("Gasto_1") != null ? rs.getString("Gasto_1") : "";
                double costo1 = rs.getObject("Costo_1") != null ? rs.getDouble("Costo_1") : 0;
                String gasto2 = rs.getString("Gasto_2") != null ? rs.getString("Gasto_2") : "";
                double costo2 = rs.getObject("Costo_2") != null ? rs.getDouble("Costo_2") : 0;
                String gasto3 = rs.getString("Gasto_3") != null ? rs.getString("Gasto_3") : "";
                double costo3 = rs.getObject("Costo_3") != null ? rs.getDouble("Costo_3") : 0;
                String gasto4 = rs.getString("Gasto_4") != null ? rs.getString("Gasto_4") : "";
                double costo4 = rs.getObject("Costo_4") != null ? rs.getDouble("Costo_4") : 0;
                double total = rs.getObject("Total") != null ? rs.getDouble("Total") : 0;

                modelo.addRow(new Object[]{
                    IdCosto,
                    producto,
                    numEtapas,
                    actividades,
                    empresa,
                    gasto1,
                    String.format("%.2f", costo1),
                    gasto2,
                    String.format("%.2f", costo2),
                    gasto3,
                    String.format("%.2f", costo3),
                    gasto4,
                    String.format("%.2f", costo4),
                    String.format("%.2f", total),});
            }

            rs.close();
            ps.close();
            cn.close();
            ocultarIdDetalle();

            jTextField1.setText("0");
            jTextField2.setText("0");
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
                PreparedStatement pst = cn.prepareStatement("DELETE FROM gastos2 WHERE idasistencia=?");
                pst.setString(1, IdCostos);
                pst.executeUpdate();
                recalcularTotalDiasPorEmpresa();
                limpiarCampos();

                // Eliminar del modelo asociado a jTable2
                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                modelo.removeRow(filaSeleccionada);
            }
        } catch (SQLException ex) {
        }
    }

    public void setEmpresaFiltrada(String empresa) {
        this.empresaFiltrada = empresa;
        cargarRegistrosFinanzasPorEmpresa(); // 🔹 carga al asignar
        recalcularTotalDiasPorEmpresa();      // calcula solo para esa empresa
    }

    public void cargarRegistrosFinanzasPorEmpresa() {

        String sql = "SELECT idasistencia, Producto, Num_Etapas, Actividades, Empresa, Gasto_1, Costo_1, Gasto_2, Costo_2, Gasto_3, Costo_3, Gasto_4, Costo_4, Total "
                + "FROM gastos2 WHERE Empresa LIKE ?";

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{
                    "idasistencia", "Producto", "Num_Etapas", "Actividades",
                    "Empresa", "Gasto_1", "Costo_1", "Gasto_2", "Costo_2", "Gasto_3", "Costo_3", "Gasto_4", "Costo_4", "Total"
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

    private void validarGastoDisponible(JComboBox<?> combo) {

        Object seleccionado = combo.getSelectedItem();

        if (seleccionado == null) {
            return;
        }

        String proveedorSeleccionado = seleccionado.toString().trim();

        if (proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM gastos WHERE NombreGastos = ?";

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, proveedorSeleccionado);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    String status = rs.getString("Status");

                    if ("No esta Disponible".equalsIgnoreCase(status)) {

                        JOptionPane.showMessageDialog(
                                null,
                                proveedorSeleccionado + " no está disponible",
                                "Gasto no disponible",
                                JOptionPane.WARNING_MESSAGE
                        );

                        SwingUtilities.invokeLater(() -> {
                            combo.requestFocusInWindow();
                            combo.showPopup();
                        });
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar gasto: " + e.getMessage(),
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
        String mensaje = saludo + " " + nombreEmpleado + ", este es el reporte detallado de los gastos.";

        String url = "https://wa.me/" + numeroCompleto + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jTextField3 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnMenuContador = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("REPORTE DE GASTOS A DETALLE");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Gasto 1");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 200, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Costo 1");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 200, 30));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 180, 200, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Gasto 2");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 60, 200, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Costo 2");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, -1, -1));

        jComboBox3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, 200, 30));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 180, 200, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Gasto 3");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 120, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Costo 3");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, -1, -1));

        jComboBox4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 60, 170, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Gasto 4");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 60, -1, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 120, 170, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Costo 4");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 120, -1, -1));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "idasistencia", "Producto", "Num_Etapas", "Actividades", "Empresa", "Gasto_1", "Costo_1", "Gasto_2", "Costo_2", "Gasto_3", "Costo_3", "Gasto_4", "Costo_4", "Total"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 1260, 270));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Contador");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 180, 280, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 240, 270, 40));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Exportacion");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 240, 230, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 240, 170, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Editar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 180, 40));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Buscador");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 190, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 100, 370, 40));

        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 180, 170, 30));

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setText("Enviar Reporte");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 240, 210, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 580));

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton3ActionPerformed

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
            ExportarExcel4 obj = new ExportarExcel4(empresa);

            // Exportar la tabla
            obj.exportarExcel4(jTable1);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar el archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        String textoFiltro = jTextField5.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT idasistencia, Producto, Num_Etapas, Actividades, Empresa, Gasto_1, Costo_1, Gasto_2, Costo_2, Gasto_3, Costo_3, Gasto_4, Costo_4, Total FROM gastos2");
        boolean tieneTexto = !textoFiltro.isEmpty();

        if (tieneTexto) {
            sql.append(" WHERE Actividades LIKE ? OR Empresa LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"idasistencia", "Producto", "Num_Etapas", "Actividades",
                    "Empresa", "Gasto_1", "Costo_1", "Gasto_2", "Costo_2", "Gasto_3", "Costo_3", "Gasto_4", "Costo_4", "Total"}, 0);

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
                        rs.getString("Empresa"),
                        rs.getString("Gasto_1"),
                        df.format(rs.getDouble("Costo_1")),
                        rs.getString("Gasto_2"),
                        df.format(rs.getDouble("Costo_2")),
                        rs.getString("Gasto_3"),
                        df.format(rs.getDouble("Costo_3")),
                        rs.getString("Gasto_4"),
                        df.format(rs.getDouble("Costo_4")),
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
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        pedirEmpleadoYEnviar(null);
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(ReporteDetallado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReporteDetallado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReporteDetallado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReporteDetallado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new ReporteDetallado().setVisible(true);
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
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    // End of variables declaration//GEN-END:variables
}
