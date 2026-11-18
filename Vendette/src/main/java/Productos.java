
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class Productos extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;
    private boolean formularioCargado = false;
    private String puesto;

    public Productos() {
        initComponents();

        agregarValidacionUsuario(jComboBox1, this::validarProveedorDisponible);

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("proveedores", "Nombre", jComboBox1);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/agregar-producto.png"));
        setIconImage(logoIcon.getImage());

        jButton2.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton1.setIcon(getIconUrl("/imagen/diskette.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton5.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/importar.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        cargarTabla();

        centrarColumnas();

        ocultarIdDetalle();

        formularioCargado = true;

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        Productos.this,
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

        jTextField1.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField2.requestFocus();
            }
        });

        jTextField2.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField3.requestFocus();
            }
        });

        jTextField3.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField5.requestFocus();
            }
        });

        jTextField5.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField4.requestFocus();
            }
        });

        jTextField4.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField6.requestFocus();
            }
        });

        jTextField6.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox1.showPopup();
            }
        });

        jComboBox1.addActionListener(e -> {
            if (modoCapturaManual) {
                jTextField7.requestFocus();
            }
        });

        jTextField7.addActionListener(e -> {
            if (modoCapturaManual) {
                jComboBox2.showPopup();
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
        jTextField4.setText(obtenerValorDeTabla(fila, 2));
        jComboBox1.setSelectedItem(obtenerValorDeTabla(fila, 3));
        jTextField7.setText(obtenerValorDeTabla(fila, 4));
        jTextField2.setText(obtenerValorDeTabla(fila, 5));
        jTextField6.setText(obtenerValorDeTabla(fila, 6));
        jTextField3.setText(obtenerValorDeTabla(fila, 7));
        jTextField5.setText(obtenerValorDeTabla(fila, 8));
        jComboBox2.setSelectedItem(obtenerValorDeTabla(fila, 9));
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
            jTable1.getColumnModel().getColumn(10).setCellRenderer(new ColorearStatus3());
        }
    }

    public void guardarRegistro() {

        if (validarCamposVacios()) {
            return;
        }

        // 🔴 VALIDAR CÓDIGO DE BARRAS
        String CodigoBarras = jTextField1.getText().trim();

        // Solo números
        if (!CodigoBarras.matches("\\d+")) {
            JOptionPane.showMessageDialog(null,
                    "Error: el código de barras '" + CodigoBarras
                    + "' no es válido (solo números)");
            jTextField1.requestFocus();
            jTextField1.selectAll();
            return;
        }

        // Validar que el código de barras no sea 0
        if (CodigoBarras.matches("0+")) {
            JOptionPane.showMessageDialog(null,
                    "Error: el código de barras '" + CodigoBarras
                    + "' debe ser mayor a 0");
            jTextField1.requestFocus();
            jTextField1.selectAll();
            return;
        }

        int InventarioMaximo = Integer.parseInt(jTextField5.getText().trim());

        // 🔴 VALIDAR INVENTARIO MÁXIMO
        if (InventarioMaximo >= 100) {
            JOptionPane.showMessageDialog(
                    null,
                    "En inventario máximo no debe existir más de 100 productos,\n"
                    + "debe ser menor a 100",
                    "Inventario máximo inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            jTextField5.requestFocus();
            jTextField5.selectAll();
            return;
        }

        String Descripcion, Proveedor, Status;
        int InventarioMinimo, Stock, AlertaInventarioMin;
        int idproductos = generarNuevoIdAsistencia();
        double PrecioInicial, PrecioFinal;

        Proveedor = jComboBox1.getSelectedItem().toString();

        String texto = jTextField2.getText().trim().replace(",", ".");
        PrecioInicial = Double.parseDouble(texto);

        InventarioMinimo = Integer.parseInt(jTextField3.getText().trim());

        // 🔴 ASIGNAR ALERTA DE INVENTARIO MÍNIMO
        if (InventarioMinimo <= 30) {
            AlertaInventarioMin = 1;
        } else {
            AlertaInventarioMin = 0;
        }

        String texto3 = jTextField6.getText().trim().replace(",", ".");
        PrecioFinal = Double.parseDouble(texto3);

        Stock = Integer.parseInt(jTextField7.getText().trim());

        Descripcion = jTextField4.getText().trim();

        Status = jComboBox2.getSelectedItem().toString();

        try (Connection cn = new conexion().conectar()) {

            // 🔴 VALIDAR: Código de barras repetido en BD
            String sqlValidar = "SELECT idproductos FROM productos WHERE CodigoBarras = ?";

            try (PreparedStatement pstValidar = cn.prepareStatement(sqlValidar)) {

                // El código de barras ahora se envía como String
                pstValidar.setString(1, CodigoBarras);

                ResultSet rs = pstValidar.executeQuery();

                if (rs.next()) {

                    JOptionPane.showMessageDialog(null,
                            "El código de barras " + CodigoBarras
                            + " ya está registrado ");

                    jTextField1.requestFocus();
                    jTextField1.selectAll();
                    return;
                }
            }

            int confirmacion = JOptionPane.showConfirmDialog(
                    null,
                    "¿Desea guardar este registro?",
                    "Confirmar Registro",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {

                String sql = "INSERT INTO productos "
                        + "(idproductos, CodigoBarras, Descripcion, Proveedor, "
                        + "Stock, PrecioInicial, PrecioFinal, InventarioMinimo, "
                        + "InventarioMaximo, AlertaInventarioMin, Status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pst = cn.prepareStatement(sql)) {

                    pst.setInt(1, idproductos);

                    // 🔴 Código de barras como String
                    pst.setString(2, CodigoBarras);

                    pst.setString(3, Descripcion);
                    pst.setString(4, Proveedor);
                    pst.setInt(5, Stock);
                    pst.setDouble(6, PrecioInicial);
                    pst.setDouble(7, PrecioFinal);
                    pst.setInt(8, InventarioMinimo);
                    pst.setInt(9, InventarioMaximo);
                    pst.setInt(10, AlertaInventarioMin);
                    pst.setString(11, Status);

                    int filasInsertadas = pst.executeUpdate();

                    if (filasInsertadas > 0) {

                        cargarTabla();
                        centrarColumnas();
                        limpiarCampos();

                        DefaultTableModel model
                                = (DefaultTableModel) jTable1.getModel();

                        for (int i = 0; i < model.getRowCount(); i++) {

                            int idTabla = Integer.parseInt(
                                    model.getValueAt(i, 0).toString()
                            );

                            if (idTabla == idproductos) {

                                jTable1.setRowSelectionInterval(i, i);

                                jTable1.scrollRectToVisible(
                                        jTable1.getCellRect(i, 0, true)
                                );

                                break;
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al guardar: " + e.getMessage()
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error: uno de los campos numéricos contiene un valor no válido."
            );
        }
    }

    private boolean validarCamposVacios() {
        JTextField[] campos = {jTextField2, jTextField3, jTextField5, jTextField4, jTextField6, jTextField7};
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
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField5.setText("");
        jTextField4.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField1.setText("");
    }

    private int generarNuevoIdAsistencia() {
        int nuevoId = 1; // Por defecto empezamos en 1

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(idproductos) FROM productos"); ResultSet rs = pst.executeQuery()) {

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

            String idSolucion = jTable1.getValueAt(filaSeleccionada, 0).toString();

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                PreparedStatement pst = cn.prepareStatement("DELETE FROM productos WHERE idproductos=?");
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

    public void actualizarRegistro() {

        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String Descripcion, Proveedor, Status;

            int CodigoBarras, InventarioMinimo, InventarioMaximo, Stock, AlertaInventarioMin;

            double PrecioInicial, PrecioFinal;

            CodigoBarras = Integer.parseInt(jTextField1.getText().trim());
            Proveedor = jComboBox1.getSelectedItem().toString();
            String texto = jTextField2.getText().trim();
// Reemplaza coma por punto para que Double.parseDouble lo acepte
            texto = texto.replace(",", ".");
            PrecioInicial = Double.parseDouble(texto);

            InventarioMinimo = Integer.parseInt(jTextField3.getText().trim());

            // 🔴 ASIGNAR ALERTA DE INVENTARIO MÍNIMO
            if (InventarioMinimo <= 30) {
                AlertaInventarioMin = 1;
            } else {
                AlertaInventarioMin = 0;
            }

            InventarioMaximo = Integer.parseInt(jTextField5.getText().trim());

            // 🔴 VALIDAR INVENTARIO MÁXIMO
            if (InventarioMaximo >= 100) {
                JOptionPane.showMessageDialog(
                        null,
                        "En inventario máximo no debe existir más de 100 productos,\n"
                        + "debe ser menor a 100",
                        "Inventario máximo inválido",
                        JOptionPane.WARNING_MESSAGE
                );
                jTextField5.requestFocus();
                jTextField5.selectAll();
                return;
            }

            String texto3 = jTextField6.getText().trim();
            texto3 = texto3.replace(",", ".");
            PrecioFinal = Double.parseDouble(texto3);

            Stock = Integer.parseInt(jTextField7.getText().trim());

            Descripcion = jTextField4.getText().trim();

            Status = jComboBox2.getSelectedItem().toString();

            int idproductos = Integer.parseInt(jTable1.getValueAt(filaSeleccionada, 0).toString());

            int confirmacion = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de actualizar este registro?",
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                String sqlUpdate = "UPDATE productos SET CodigoBarras = ?, Descripcion = ?, Proveedor = ?, Stock = ?, PrecioInicial = ?, PrecioFinal = ?, InventarioMinimo = ?, InventarioMaximo = ?, AlertaInventarioMin = ?, Status = ?  WHERE idproductos = ?";
                try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sqlUpdate)) {

                    pst.setInt(1, CodigoBarras);
                    pst.setString(2, Descripcion);
                    pst.setString(3, Proveedor);
                    pst.setInt(4, Stock);
                    pst.setDouble(5, PrecioInicial);
                    pst.setDouble(6, PrecioFinal);
                    pst.setInt(7, InventarioMinimo);
                    pst.setInt(8, InventarioMaximo);
                    pst.setInt(9, AlertaInventarioMin);
                    pst.setString(10, Status);
                    pst.setInt(11, idproductos);

                    int filasAfectadas = pst.executeUpdate();
                    if (filasAfectadas > 0) {
                        cargarTabla();
                        centrarColumnas();
                    } else {
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error: uno de los campos numéricos contiene un valor no válido."
            );

        } catch (SQLException e) {
        }
    }

    public void cargarTabla() {
        final String sql = "SELECT idproductos, CodigoBarras, Descripcion, Proveedor, Stock, PrecioInicial, PrecioFinal, InventarioMinimo, InventarioMaximo, AlertaInventarioMin, Status FROM productos";
        final String[] columnas = {"idproductos", "CodigoBarras", "Descripcion", "Proveedor", "Stock", "PrecioInicial", "PrecioFinal", "InventarioMinimo", "InventarioMaximo", "AlertaInventarioMin", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        conexion conexion = new conexion();
        try (Connection conn = conexion.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String precioInicialFmt = String.format("%.2f", rs.getDouble("PrecioInicial"));
                String precioFinalFmt = String.format("%.2f", rs.getDouble("PrecioFinal"));

                Object[] fila = {
                    rs.getInt("idproductos"),
                    rs.getString("CodigoBarras"),
                    rs.getString("Descripcion"),
                    rs.getString("Proveedor"),
                    rs.getInt("Stock"),
                    precioInicialFmt,
                    precioFinalFmt,
                    rs.getInt("InventarioMinimo"),
                    rs.getInt("InventarioMaximo"),
                    rs.getInt("AlertaInventarioMin"),
                    rs.getString("Status")
                };

                modelo.addRow(fila);
            }

            jTable1.setModel(modelo);
            ocultarIdDetalle();

        } catch (SQLException ex) {
        }
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, jButton5, btnMenuContador);
    }

    private void ocultarIdDetalle() {
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    public void importarExcelMasivo() {

        int opcion = JOptionPane.showConfirmDialog(
                null,
                "La importación reemplazará todos los registros actuales de la tabla productos.\n"
                + "Los datos visibles serán eliminados y sustituidos por los del archivo Excel.\n\n"
                + "¿Desea continuar?",
                "Confirmar Importación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // 🔴 SI ELIGE NO
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        JFileChooser seleccionar = new JFileChooser();
        int estado = seleccionar.showOpenDialog(null);

        if (estado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = seleccionar.getSelectedFile();

        SwingWorker<Void, Object[]> worker = new SwingWorker<Void, Object[]>() {

            @Override
            protected Void doInBackground() throws Exception {

                Connection cn = new conexion().conectar();
                cn.setAutoCommit(false);

                try {

                    FileInputStream fis = new FileInputStream(archivo);
                    XSSFWorkbook libro = new XSSFWorkbook(fis);
                    XSSFSheet hoja = libro.getSheetAt(0);

                    DataFormatter formatter = new DataFormatter();

                    // ===== ENCABEZADOS =====
                    Row encabezado = hoja.getRow(0);

                    String[] columnasEsperadas = {
                        "idproductos", "CodigoBarras", "Descripcion", "Proveedor",
                        "Stock", "PrecioInicial", "PrecioFinal", "InventarioMinimo", "InventarioMaximo", "AlertaInventarioMin", "Status"
                    };

                    for (int i = 0; i < columnasEsperadas.length; i++) {

                        Cell celda = encabezado.getCell(i);

                        if (celda == null
                                || !celda.getStringCellValue().trim().equalsIgnoreCase(columnasEsperadas[i])) {

                            throw new Exception("Falta la columna: " + columnasEsperadas[i]);
                        }
                    }

                    Map<Long, Integer> mapaCodigos = new HashMap<>();

                    // 🔥 LISTA PARA VALIDAR TODO PRIMERO
                    List<Object[]> datosValidos = new ArrayList<>();

                    // ===== VALIDACIÓN =====
                    for (int i = 1; i <= hoja.getLastRowNum(); i++) {

                        Row fila = hoja.getRow(i);
                        if (fila == null) {
                            continue;
                        }

                        for (int c = 0; c < columnasEsperadas.length; c++) {

                            Cell celda = fila.getCell(c);

                            if (celda == null || celda.toString().trim().isEmpty()) {

                                throw new Exception("Error en fila " + (i + 1)
                                        + ", columna '" + columnasEsperadas[c] + "' está vacía");
                            }
                        }

                        int idproductos = (int) fila.getCell(0).getNumericCellValue();
                        // 🔥 VALIDACIÓN COMPLETA DE CODIGO DE BARRAS
                        String codigoTexto = formatter.formatCellValue(fila.getCell(1));

                        if (!codigoTexto.matches("\\d+")) {
                            throw new Exception("Error en fila " + (i + 1)
                                    + ": el código de barras '" + codigoTexto + "' no es válido (solo se permiten números)");
                        }

                        long CodigoBarras = Long.parseLong(codigoTexto);

                        if (CodigoBarras <= 0) {
                            throw new Exception("Error en fila " + (i + 1)
                                    + ": el código de barras '" + CodigoBarras + "' no es válido (debe ser mayor a 0)");
                        }

// 🔴 VALIDAR DUPLICADOS
                        if (mapaCodigos.containsKey(CodigoBarras)) {

                            int filaOriginal = mapaCodigos.get(CodigoBarras);

                            throw new Exception("El código de barras " + CodigoBarras + " está repetido en la fila " + (i + 1)
                                    + " (ya existe en la fila " + filaOriginal + ")");
                        }

                        mapaCodigos.put(CodigoBarras, i + 1);

                        String Descripcion = fila.getCell(2).getStringCellValue();
                        String Proveedor = fila.getCell(3).getStringCellValue();
                        int Stock = (int) fila.getCell(4).getNumericCellValue();
                        double PrecioInicial = (double) fila.getCell(5).getNumericCellValue();
                        double PrecioFinal = (double) fila.getCell(6).getNumericCellValue();
                        int InventarioMinimo = (int) fila.getCell(7).getNumericCellValue();
                        int InventarioMaximo = (int) fila.getCell(8).getNumericCellValue();
                        String Status = fila.getCell(9).getStringCellValue();

                        // 🔥 GUARDAR (NO INSERTAR AÚN)
                        datosValidos.add(new Object[]{
                            idproductos, CodigoBarras, Descripcion, Proveedor, Stock, PrecioInicial, PrecioFinal, InventarioMinimo, InventarioMaximo, Status
                        });
                    }

                    // ===== SI TODO ES VÁLIDO =====
                    // 🔴 BORRAR REGISTROS VIEJOS
                    Statement st = cn.createStatement();
                    st.executeUpdate("DELETE FROM productos");

                    String sql = "INSERT INTO productos VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement ps = cn.prepareStatement(sql);

                    int contador = 0;

                    for (Object[] fila : datosValidos) {

                        ps.setInt(1, (int) fila[0]);
                        ps.setLong(2, (long) fila[1]);
                        ps.setString(3, (String) fila[2]);
                        ps.setString(4, (String) fila[3]);
                        ps.setInt(5, (int) fila[4]);
                        ps.setDouble(6, (double) fila[5]);
                        ps.setDouble(7, (double) fila[6]);
                        ps.setInt(8, (int) fila[7]);
                        ps.setString(9, (String) fila[8]);
                        ps.setInt(10, (int) fila[9]);

                        ps.addBatch();

                        // 🔥 SOLO AQUÍ se muestran (ya todo es válido)
                        publish(fila);

                        contador++;

                        if (contador % 500 == 0) {
                            ps.executeBatch();
                        }
                    }

                    ps.executeBatch();
                    cn.commit();

                } catch (Exception e) {

                    cn.rollback(); // 🔴 NO guarda nada
                    throw e;
                }

                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {

                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                modelo.setRowCount(0); // 🔥 limpia antes de mostrar nuevos datos

                for (Object[] fila : chunks) {
                    modelo.addRow(fila);
                }
            }

            @Override
            protected void done() {

                try {
                    get();
                    JOptionPane.showMessageDialog(null, "Importación completada correctamente");

                } catch (Exception e) {

                    String mensaje = e.getCause() != null
                            ? e.getCause().getMessage()
                            : e.getMessage();

                    JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        };

        worker.execute();
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
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        btnMenuContador = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("PRODUCTOS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Codigo de Barras");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 110, 150, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Costo de Compra Inicial");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 170, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 180, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Inventario Minimo");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, 170, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Descripcion");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, -1, -1));

        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 230, 180, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Inventario Maximo");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        jTextField6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 110, 120, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Precio Venta al Publico");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 170, 190, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Proveedor");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 170, -1, -1));

        jTextField7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 220, 190, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Stock");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 230, -1, -1));

        jTextField8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextField8MouseReleased(evt);
            }
        });
        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 50, 170, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Buscar Producto");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 50, -1, -1));

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setText("Menu de Modulos");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 280, 240, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 280, 180, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Editar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 280, 170, 40));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 160, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Idproductos", "CodigoBarras", "Descripcion", "Proveedor", "Stock", "PrecioInicial", "PrecioFinal", "InventarioMinimo", "InventarioMaximo", "AlertaInventarioMin", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 1200, 310));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 50, 150, 30));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Cajero");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 220, 240, 40));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Importar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 280, 240, 40));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Esta Disponible", "No esta Disponible" }));
        getContentPane().add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 110, 220, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Status");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 110, -1, -1));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 660));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField8MouseReleased

    }//GEN-LAST:event_jTextField8MouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        guardarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        String textoFiltro = jTextField8.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT idproductos, CodigoBarras, Descripcion, Proveedor, Stock, PrecioInicial, PrecioFinal, InventarioMinimo, InventarioMaximo, AlertaInventarioMin, Status FROM productos");
        boolean tieneTexto = !textoFiltro.isEmpty();

        if (tieneTexto) {
            sql.append(" WHERE CodigoBarras LIKE ? OR Descripcion LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"idproductos", "CodigoBarras", "Descripcion", "Proveedor", "Stock", "PrecioInicial", "PrecioFinal", "InventarioMinimo", "InventarioMaximo", "AlertaInventarioMin", "Status"}, 0);

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
    }//GEN-LAST:event_jTextField8KeyReleased

    private void btnMenuContadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuContadorActionPerformed
        MenuCajero menu = new MenuCajero();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuContadorActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        importarExcelMasivo();
    }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(Productos.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Productos.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Productos.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Productos.class
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
            public void run() {
                new Productos().setVisible(true);
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
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
