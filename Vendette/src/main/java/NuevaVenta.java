
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class NuevaVenta extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;
    private double totalVentaActual = 0.0;
    // ===== ANCHO TICKET 80 MM =====
    private static final int CHARS_80MM = 48;
    private int anchoTicket = CHARS_80MM;

// Ancho real en puntos (80 mm)
    private static final float ANCHO_80MM_PT = 80f * 2.83465f; // ≈ 226.77 pt

// Usa SOLO UNO
    private float anchoPDF = ANCHO_80MM_PT;

    private boolean formularioCargado = false;
    private String puesto;
    public int idVentaActual;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public NuevaVenta() {
        initComponents();

        jComboBox1.addActionListener(e
                -> validarComboSiUsuarioInteractuo(this::validarProveedorDisponible)
        );

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("metdodopago", "NombreMetodo", jComboBox1);

        jTextField9.addActionListener(e -> {
            calcularCambioEfectivo();
        });

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/nuevo-producto.png"));
        setIconImage(logoIcon.getImage());

        jButton1.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton2.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/impresora.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        idVentaActual = generarNuevoIdAsistencia(); // ✅ SOLO UNA VEZ
        cargarTabla();
        ocultarIdDetalle();
        recalcularTotalVenta();
        centrarColumnas();

        configurarEnterGuardar();
        configurarEnterCodigo();

        ocultarVentaDeTabla();

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        formularioCargado = true;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        NuevaVenta.this,
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
    }

    private void calcularCambioEfectivo() {

        String metodo = jComboBox1.getSelectedItem().toString();
        if (!metodo.equalsIgnoreCase("Efectivo")) {
            jLabel5.setText("");
            return;
        }

        try {
            double pagoCliente = Double.parseDouble(
                    jTextField9.getText().trim().replace(",", ".")
            );

            double cambio = pagoCliente - totalVentaActual;

            jLabel5.setText(
                    cambio >= 0 ? String.format("%.2f", cambio) : "0.00"
            );

        } catch (NumberFormatException ex) {
            jLabel5.setText("");
        }
    }

    public double recalcularTotalVenta() {

        double total = 0.0;
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int columnaPrecioTotal = 9; // ajusta si cambia

        for (int i = 0; i < model.getRowCount(); i++) {
            Object valor = model.getValueAt(i, columnaPrecioTotal);
            if (valor != null) {
                total += Double.parseDouble(valor.toString());
            }
        }

        total = Math.round(total * 100.0) / 100.0;
        totalVentaActual = total; // 🔥 guardar total REAL

        jLabel16.setText("Total a Pagar = $" + String.format("%.2f", total) + " pesos");

        return total;
    }

    public void guardarRegistro() {

        String Codigo = jTextField1.getText().trim();
        String Descripcion = jTextField5.getText().trim();

        String texto = jTextField4.getText().trim().replace(",", ".");
        double PrecioCompra = Double.parseDouble(texto);
        int Cantidad = Integer.parseInt(jTextField2.getText().trim());
        String TipoVenta = jComboBox2.getSelectedItem().toString();
        LocalDate FechaVenta = LocalDate.now();

        String MetodoPago = jComboBox1.getSelectedItem().toString();

        double PrecioTotal = Math.round((Cantidad * PrecioCompra) * 100.0) / 100.0;

        Connection cn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            cn = new conexion().conectar();

            // 1️⃣ CONSULTAR INVENTARIO
            String sqlInv = "SELECT Stock FROM productos WHERE CodigoBarras = ?";
            pst = cn.prepareStatement(sqlInv);
            pst.setString(1, Codigo);
            rs = pst.executeQuery();

            if (rs.next()) {
                int stock = rs.getInt("Stock");

                // 2️⃣ VALIDACIÓN DE INVENTARIO (EXCLUYENTE)
                if (stock == 0) {
                    JOptionPane.showMessageDialog(
                            null,
                            "No tienes inventario de " + Descripcion,
                            "Sin inventario",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return; // ⛔ no continúa la venta
                } else if (stock < 10) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Tienes poco inventario de " + Descripcion,
                            "Inventario bajo",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

                // 4️⃣ DESCONTAR INVENTARIO
                int nuevoInventario = stock - Cantidad;
                String sqlUpdate = "UPDATE productos SET Stock = ? WHERE CodigoBarras = ?";
                PreparedStatement pstUpdate = cn.prepareStatement(sqlUpdate);
                pstUpdate.setInt(1, nuevoInventario);
                pstUpdate.setString(2, Codigo);
                pstUpdate.executeUpdate();
            }

            // 2️⃣ 🔢 OBTENER CONTADOR POR PRODUCTO
            int contadorProducto = 1;

            String sqlContador = "SELECT MAX(ContadorProducto) AS ultimo "
                    + "FROM nuevaventa WHERE CodigoBarras = ?";

            PreparedStatement pstContador = cn.prepareStatement(sqlContador);
            pstContador.setString(1, Codigo);
            ResultSet rsContador = pstContador.executeQuery();

            if (rsContador.next()) {
                int ultimo = rsContador.getInt("ultimo");
                if (!rsContador.wasNull()) {
                    contadorProducto = ultimo + 1;
                }
            }

            String sql = "INSERT INTO nuevaventa "
                    + "(Folio, CodigoBarras, Producto, PrecioVenta, Cantidad, TipoVenta, FechaVenta, MetodoPago, PrecioTotal, ContadorProducto) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pst = cn.prepareStatement(sql);

            pst.setInt(1, idVentaActual); // ✅ MISMO ID PARA TODOS
            pst.setString(2, Codigo);
            pst.setString(3, Descripcion);
            pst.setDouble(4, PrecioCompra);
            pst.setInt(5, Cantidad);
            pst.setString(6, TipoVenta);
            pst.setDate(7, java.sql.Date.valueOf(FechaVenta));
            pst.setString(8, MetodoPago);
            pst.setDouble(9, PrecioTotal);
            pst.setInt(10, contadorProducto);

            pst.executeUpdate();
            cargarTabla(); // ahora sí traerá TODOS
            centrarColumnas();
            recalcularTotalVenta();
            seleccionarUltimaFila();
            limpiarCampos();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void seleccionarUltimaFila() {

        int ultimaFila = jTable1.getRowCount() - 1;

        if (ultimaFila >= 0) {
            jTable1.setRowSelectionInterval(ultimaFila, ultimaFila);
            jTable1.scrollRectToVisible(
                    jTable1.getCellRect(ultimaFila, 0, true)
            );
        }
    }

    private int generarNuevoIdAsistencia() {
        int nuevoId = 1; // Por defecto empezamos en 1

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(Folio) FROM nuevaventa"); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                nuevoId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
        }
        return nuevoId;
    }

    public void eliminarRegistro() {

        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe seleccionar un registro para eliminar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();

        int idDetalle = Integer.parseInt(
                modelo.getValueAt(filaSeleccionada, 0).toString());

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(
                "DELETE FROM nuevaventa WHERE iddetalle = ?")) {

            pst.setInt(1, idDetalle);
            pst.executeUpdate();

            // eliminar solo la fila seleccionada
            modelo.removeRow(filaSeleccionada);

            recalcularTotalVenta();
            limpiarCampos();
            jTextField1.requestFocus();

        } catch (SQLException ex) {
            System.err.println("Error al eliminar registro: " + ex.getMessage());
        }
    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    private void cargarTabla() {

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);

        String sql = "SELECT iddetalle, Folio, CodigoBarras, Producto, PrecioVenta, Cantidad, TipoVenta, FechaVenta, MetodoPago, PrecioTotal "
                + "FROM nuevaventa WHERE Folio = ?";

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setInt(1, idVentaActual);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                double precioVenta = rs.getDouble("PrecioVenta");
                double precioTotal = rs.getDouble("PrecioTotal");

                modelo.addRow(new Object[]{
                    rs.getInt("iddetalle"),
                    rs.getInt("Folio"),
                    rs.getString("CodigoBarras"),
                    rs.getString("Producto"),
                    df.format(precioVenta), // ✅ 2 decimales
                    rs.getInt("Cantidad"),
                    rs.getString("TipoVenta"),
                    rs.getDate("FechaVenta"),
                    rs.getString("MetodoPago"),
                    df.format(precioTotal) // ✅ 2 decimales
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String obtenerCambio() {

        String metodo = jComboBox1.getSelectedItem().toString();
        if (!metodo.equalsIgnoreCase("Efectivo")) {
            return "";
        }

        String cambio = jLabel5.getText().trim();
        return cambio.isEmpty() ? "0.00" : cambio;
    }

    private void centrarColumnas() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }
    }

    private void configurarEnterGuardar() {
        JRootPane rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "guardar");

        rootPane.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarRegistro();
            }
        });
    }

    private void limpiarCampos() {
        jTextField1.setText("");     // Código
        jTextField2.setText(""); // Cantidad (opcional dejar en 1)
        jTextField5.setText("");
        jTextField4.setText("");
        jTextField1.requestFocus(); // Regresa el foco al código
    }

    private void buscarProductoPorCodigo() {

        String codigo = jTextField1.getText().trim();

        if (codigo.isEmpty()) {
            return;
        }

        try (Connection cn = new conexion().conectar()) {

            String sql = "SELECT Descripcion, PrecioFinal "
                    + "FROM productos "
                    + "WHERE CodigoBarras = ?";

            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, codigo);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                jTextField5.setText(rs.getString("Descripcion"));
                jTextField4.setText(
                        String.format("%.2f", rs.getDouble("PrecioFinal"))
                );

                jTextField2.requestFocus();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Producto no encontrado",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);

                jTextField1.selectAll();
                jTextField1.requestFocus();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar producto: " + ex.getMessage());
        }
    }

    private void configurarEnterCodigo() {
        jTextField1.addActionListener(e -> {
            buscarProductoPorCodigo();
        });
    }

    private String generarTicket(int folio) {

        double totalVenta = recalcularTotalVenta();

        StringBuilder ticket = new StringBuilder();

        String fechaActual = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String horaActual = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("hh:mm a"));

        Properties datosEmpresa = obtenerDatosEmpresa();

        String empresa = datosEmpresa.getProperty("empresa", "");
        String telefono = datosEmpresa.getProperty("telefono", "");
        String direccion = datosEmpresa.getProperty("direccion", "");
        String descripcion = datosEmpresa.getProperty("descripcion", "");

        // ==========================================
        // DATOS DE LA EMPRESA
        // ==========================================
        ticket.append("DATOS DE LA EMPRESA\n");
        ticket.append("Empresa: ").append(empresa).append("\n");
        ticket.append("Telefono: ").append(telefono).append("\n");
        ticket.append("Direccion: ").append(direccion).append("\n");
        ticket.append("Descripcion: ").append(descripcion).append("\n\n");

        // ==========================================
        // DATOS DE LA VENTA
        // ==========================================
        ticket.append("Folio: ")
                .append(folio)
                .append(" Fecha: ")
                .append(fechaActual)
                .append("\n");

        ticket.append("Hora: ")
                .append(horaActual)
                .append(" Vendedor: Vendette\n\n");

        // ==========================================
        // ENCABEZADOS DE PRODUCTOS
        // ==========================================
        ticket.append(String.format(
                "%-4s %-11s %4.5s %3.5s %-11s\n",
                "Cant",
                "Producto",
                "Costo",
                "Total",
                "TipoVenta"
        ));

        // ==========================================
        // PRODUCTOS
        // ==========================================
        for (int i = 0; i < jTable1.getRowCount(); i++) {

            double cant = Double.parseDouble(
                    jTable1.getValueAt(i, 5).toString()
            );

            String desc = jTable1.getValueAt(i, 3).toString();

            double precio = Double.parseDouble(
                    jTable1.getValueAt(i, 4).toString()
            );

            double total = Double.parseDouble(
                    jTable1.getValueAt(i, 9).toString()
            );

            String tipoVenta = jTable1.getValueAt(i, 6).toString();

            // ==========================================
            // LIMITAR NOMBRE DEL PRODUCTO
            // ==========================================
            String producto = desc.length() > 11
                    ? desc.substring(0, 11)
                    : desc;

            // ==========================================
            // FORMATEAR PRECIOS
            // ==========================================
            String[] pUnit = formatearPrecio(precio, 5);
            String[] pTotal = formatearPrecio(total, 5);

            // ==========================================
            // PRIMERA LINEA
            // ==========================================
            ticket.append(String.format(
                    "%-4.2f %-11s %s %s %s\n",
                    cant,
                    producto,
                    pUnit[0],
                    pTotal[0],
                    centrarEnColumna(tipoVenta, 11)
            ));

            // ==========================================
            // SEGUNDA LINEA SI EL PRECIO DESBORDA
            // ==========================================
            if (!pUnit[1].isEmpty() || !pTotal[1].isEmpty()) {

                ticket.append(String.format(
                        "%-4s %-11s %s %s %-11s\n",
                        "",
                        "",
                        pUnit[1],
                        pTotal[1],
                        ""
                ));
            }
        }

        // ==========================================
        // TOTAL
        // ==========================================
        ticket.append("\n");

        String totalTexto = "Total $: "
                + String.format("%.2f", totalVenta);

        // ==========================================
        // METODO DE PAGO
        // ==========================================
        String metodoPago = jComboBox1.getSelectedItem().toString();

        ticket.append(
                centrarTexto(
                        "Tipo Pago: " + metodoPago.toUpperCase()
                )
        ).append("\n");

        // ==========================================
        // CAMBIO
        // ==========================================
        String cambio = obtenerCambio();

        if (!cambio.isEmpty()) {

            ticket.append(
                    centrarTexto("Cambio $: " + cambio)
            ).append("\n");
        }

        // ==========================================
        // TOTAL FINAL
        // ==========================================
        ticket.append(
                centrarTexto(totalTexto)
        ).append("\n");

        // ==========================================
        // CANCELACION
        // ==========================================
        ticket.append(
                centrarTexto("Cancelacion")
        ).append("\n\n");

        // ==========================================
        // FIRMA
        // ==========================================
        ticket.append(
                centrarTexto("______________")
        ).append("\n\n");

        ticket.append(
                centrarTexto("Firma")
        ).append("\n\n");

        // ==========================================
        // MENSAJE FINAL
        // ==========================================
        ticket.append(
                centrarTexto("Gracias por su compra")
        ).append("\n");

        // ==========================================
        // GUARDAR VENTA
        // ==========================================
        Concentrado.guardarVentaEnBD(
                folio,
                metodoPago,
                totalVenta,
                jTable1
        );

        UtlidadesDetalle.guardarVenta(
                folio,
                metodoPago,
                totalVenta,
                jTable1
        );

        return ticket.toString();

    }

    /**
     * Formatea un precio para que pueda imprimirse correctamente dentro de las
     * columnas del ticket.
     */
    private String[] formatearPrecio(double valor, int ancho) {

        String texto = String.format("%.2f", valor);

        if (texto.length() <= ancho) {

            return new String[]{
                String.format("%" + ancho + "s", texto),
                ""
            };
        }

        // Parte el número como en tickets reales
        String arriba = texto.substring(0, ancho);
        String abajo = texto.substring(ancho);

        return new String[]{
            arriba,
            String.format("%" + ancho + "s", abajo)
        };
    }

    public void imprimirTicketTermico(int idVentaActual) {

        try {

            // ==========================================
            // GENERAR EL TEXTO DEL TICKET
            // ==========================================
            String textoTicket = generarTicket(idVentaActual);

            // ==========================================
            // BUSCAR IMPRESORA TERMICA
            // ==========================================
            PrintService impresora = obtenerImpresoraTermica();

            if (impresora == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró la impresora térmica.\n\n"
                        + "Verifica que la BIXOLON SRP-F312 esté "
                        + "encendida y agregada en Windows.",
                        "Impresora no encontrada",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // ==========================================
            // CREAR SALIDA DE IMPRESION
            // ==========================================
            PrinterOutputStream printerOutputStream
                    = new PrinterOutputStream(impresora);

            EscPos escpos
                    = new EscPos(printerOutputStream);

            // ==========================================
            // CARGAR LOGO DEL NEGOCIO
            // ==========================================
            BufferedImage logo
                    = cargarLogoGuardadoTermico();

            if (logo != null) {

                // ======================================
                // CONVERTIR LOGO A ESC/POS
                // ======================================
                EscPosImage imagenEscPos
                        = new EscPosImage(
                                new CoffeeImageImpl(logo),
                                new BitonalThreshold(127)
                        );

                // ======================================
                // WRAPPER RASTER
                // ======================================
                RasterBitImageWrapper imageWrapper
                        = new RasterBitImageWrapper();

                // ======================================
                // CENTRAR LOGO
                // ======================================
                imageWrapper.setJustification(
                        com.github.anastaciocintra.escpos.EscPosConst.Justification.Center
                );

                // ======================================
                // IMPRIMIR LOGO
                // ======================================
                escpos.write(
                        imageWrapper,
                        imagenEscPos
                );

                // ======================================
                // ESPACIO DESPUES DEL LOGO
                // ======================================
                escpos.feed(1);
            }

            // ==========================================
            // IMPRIMIR TEXTO DEL TICKET
            // ==========================================
            escpos.writeLF(textoTicket);

            // ==========================================
            // AVANZAR PAPEL
            // ==========================================
            escpos.feed(5);

            // ==========================================
            // CORTAR TICKET
            // ==========================================
            escpos.cut(
                    EscPos.CutMode.FULL
            );

            // ==========================================
            // CERRAR IMPRESORA
            // ==========================================
            escpos.close();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error al imprimir el ticket:\n\n"
                    + e.getMessage(),
                    "Error de impresión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Busca la impresora térmica instalada en Windows.
     *
     * Primero intenta encontrar una impresora cuyo nombre contenga "ACTIVO
     * POS", después busca nombres comunes de impresoras térmicas POS.
     */
    private PrintService obtenerImpresoraTermica() {

        PrintService[] services
                = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService ps : services) {

            String nombre
                    = ps.getName().toLowerCase();

            if (nombre.contains("srp-f312")
                    || nombre.contains("bixolon")) {

                return ps;
            }
        }

        return null;
    }

    /**
     * Centra un texto utilizando un ancho de 48 caracteres, correspondiente
     * aproximadamente a una impresora térmica de 80 mm utilizando fuente
     * estándar.
     */
    private String centrarTexto(String texto) {

        if (texto == null || texto.isBlank()) {

            return "";
        }

        // Ancho típico de ticket 80 mm
        int anchoTicket = 48;

        // Si el texto es más largo,
        // se imprime normalmente.
        if (texto.length() >= anchoTicket) {

            return texto;
        }

        // Ajuste visual
        int ajusteVisual = 2;

        int espacios
                = (anchoTicket - texto.length()) / 2
                - ajusteVisual;

        if (espacios < 0) {

            espacios = 0;
        }

        return " ".repeat(espacios) + texto;
    }

    /**
     * Centra un texto dentro de una columna determinada.
     */
    private String centrarEnColumna(
            String texto,
            int anchoColumna) {

        if (texto == null) {

            texto = "";
        }

        if (texto.length() >= anchoColumna) {

            return texto.substring(0, anchoColumna);
        }

        int espaciosTotales
                = anchoColumna - texto.length();

        int espaciosIzq
                = espaciosTotales / 2;

        int espaciosDer
                = espaciosTotales - espaciosIzq;

        return " ".repeat(espaciosIzq)
                + texto
                + " ".repeat(espaciosDer);
    }

    private BufferedImage cargarLogoGuardadoTermico() {

        try {
            // 1. Obtener la carpeta de Ticketix del usuario.
            String appData = System.getenv("APPDATA");

            File carpetaUsuario;

            if (appData != null && !appData.trim().isEmpty()) {
                carpetaUsuario = new File(appData, "Ticketix");
            } else {
                carpetaUsuario = new File(
                        System.getProperty("user.home"),
                        "Ticketix"
                );
            }

            // 2. Buscar el archivo de configuración del logo.
            File propertiesFile = new File(
                    carpetaUsuario,
                    "config/logo.properties"
            );

            if (!propertiesFile.exists()) {
                System.out.println(
                        "No existe la configuración del logo: "
                        + propertiesFile.getAbsolutePath()
                );
                return null;
            }

            // 3. Leer la configuración.
            Properties props = new Properties();

            try (InputStream in = new FileInputStream(propertiesFile)) {
                props.load(in);
            }

            String rutaLogo = props.getProperty("logo_path");

            if (rutaLogo == null || rutaLogo.trim().isEmpty()) {
                System.out.println(
                        "La propiedad logo_path está vacía."
                );
                return null;
            }

            // 4. Obtener la ruta del archivo.
            File logoFile = new File(rutaLogo);

            // Compatibilidad con rutas relativas.
            if (!logoFile.isAbsolute()) {
                logoFile = new File(carpetaUsuario, rutaLogo);
            }

            if (!logoFile.exists() || !logoFile.isFile()) {
                System.out.println(
                        "No se encontró el logo: "
                        + logoFile.getAbsolutePath()
                );
                return null;
            }

            // 5. Cargar la imagen para la impresión térmica.
            BufferedImage logo = ImageIO.read(logoFile);

            if (logo == null) {
                System.out.println(
                        "El archivo no contiene una imagen válida."
                );
                return null;
            }

            System.out.println(
                    "Logo térmico cargado correctamente: "
                    + logoFile.getAbsolutePath()
            );

            return logo;

        } catch (IOException | SecurityException e) {

            System.out.println(
                    "Error al cargar el logo térmico: "
                    + e.getMessage()
            );

            e.printStackTrace();
            return null;
        }
    }

    private void ocultarVentaDeTabla() {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);

        totalVentaActual = 0.0;

        jLabel16.setText("Total a Pagar = $0.00 pesos");
    }

    private void validarProveedorDisponible() {

        String proveedorSeleccionado = jComboBox1.getSelectedItem().toString();

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

    private void validarComboSiUsuarioInteractuo(Runnable validacion) {

        // ⛔ Ignorar eventos automáticos al cargar
        if (!formularioCargado) {
            return;
        }

        validacion.run();
    }

    private void ocultarIdDetalle() {
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    public static String
            getAppBasePath() {
        try {
            return new File(NuevaVenta.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .getParentFile()
                    .getAbsolutePath();
        } catch (Exception e) {
            return new File("").getAbsolutePath(); // Fallback
        }
    }

    private Properties obtenerDatosEmpresa() {

        Properties props = new Properties();

        try {
            String basePath = getAppBasePath();
            File propertiesFile = new File(basePath, "config/texto.properties");

            if (propertiesFile.exists()) {
                try (InputStream in = new FileInputStream(propertiesFile)) {
                    props.load(in);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return props;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, jButton2, btnMenuContador);
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
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField9 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        btnMenuContador = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("NUEVA VENTA");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 140, 30));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 180, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Codigo");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 160, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Cantidad");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Precio de Venta");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Producto");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Eliminar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 420, 180, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "iddetalle", "Folio", "CodigoBarras", "Producto", "PrecioVenta", "Cantidad", "TipoVenta", "FechaVenta", "MetodoPago", "PrecioTotal"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 870, 240));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Menu de modulos");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 120, 240, 40));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Metodo de Pago");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 490, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 490, 190, 30));

        jTextField9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 490, 190, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Pago del Cliente");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 500, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Cambio");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 490, -1, -1));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Imprimir Ticket");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 550, 350, 40));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 420, 420, 40));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 128, 0));
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 490, 120, 30));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 50, 250, 30));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Buscador");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 50, -1, -1));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "VE", "VN" }));
        getContentPane().add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 130, 110, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Tipo de Venta");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, -1, -1));

        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, 180, 30));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 120, 30));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Cajero");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 550, 310, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        String textoFiltro = jTextField3.getText().trim();

        // 🔴 SI NO HAY TEXTO → NO MOSTRAR NADA
        if (textoFiltro.isEmpty()) {
            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0);   // deja la tabla vacía
            recalcularTotalVenta(); // total en 0
            return;
        }

        String sql = "SELECT iddetalle, Folio, CodigoBarras, Producto, PrecioVenta, Cantidad, TipoVenta, FechaVenta, MetodoPago, PrecioTotal "
                + "FROM nuevaventa "
                + "WHERE TipoVenta LIKE ? OR Producto LIKE ?";

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"iddetalle", "Folio", "CodigoBarras", "Producto", "PrecioVenta", "Cantidad", "TipoVenta", "FechaVenta", "MetodoPago", "PrecioTotal"}, 0
        );

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            String filtro = "%" + textoFiltro + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("iddetalle"),
                        rs.getInt("Folio"),
                        rs.getString("CodigoBarras"),
                        rs.getString("Producto"),
                        df.format(rs.getDouble("PrecioVenta")),
                        rs.getInt("Cantidad"),
                        rs.getString("TipoVenta"),
                        rs.getDate("FechaVenta"),
                        rs.getString("MetodoPago"),
                        df.format(rs.getDouble("PrecioTotal"))
                    });
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();
            recalcularTotalVenta();
            ocultarIdDetalle();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_jTextField3KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        imprimirTicketTermico(idVentaActual);
        ocultarVentaDeTabla();
        idVentaActual = generarNuevoIdAsistencia();
        jTextField9.setText("");
        jLabel5.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(NuevaVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NuevaVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NuevaVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NuevaVenta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        }/* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NuevaVenta().setVisible(true);
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
                                lbl.getWidth(), lbl.getHeight(), java.awt.Image.SCALE_SMOOTH));
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
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
