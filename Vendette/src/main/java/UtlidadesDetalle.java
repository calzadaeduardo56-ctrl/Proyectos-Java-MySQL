
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
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
public class UtlidadesDetalle extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;
    private String puesto;
    private final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Creates new form Utlidades_Detalle
     */
    public UtlidadesDetalle() {
        initComponents();

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("metdodopago", "NombreMetodo", jComboBox1);

        jDateChooser2.addPropertyChangeListener("date", evt -> {

            if (jDateChooser1.getDate() == null) {
                JOptionPane.showMessageDialog(
                        null,
                        "Selecciona primero la Fecha Inicial",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            aplicarFiltroGeneral();
        });

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/flujo-de-fondos.png"));
        setIconImage(logoIcon.getImage());

        jButton2.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        jButton1.setIcon(getIconUrl("/imagen/sobresalir.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/expediente.png", 30, 30));

        btnMenuCapturista.setIcon(getIconUrl("/imagen/menu1.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        cargarVentasDesdeBD();

        centrarColumnas();

        recalcularTotalVenta();

        recalcularBalance();

        recalcularTotalUtilidades();

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e
            ) {
                int opcion = JOptionPane.showConfirmDialog(UtlidadesDetalle.this,
                        "¿Estás seguro de que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    dispose(); // Cierra la ventana
                }
            }
        }
        );
    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    public static void guardarVenta(
            int folio,
            String metodoPago,
            double totalVenta,
            JTable tabla
    ) {

        Connection cn = null;
        PreparedStatement ps = null;
        PreparedStatement psUtilidad = null;

        try {
            cn = new conexion().conectar();

            String sql = """
            INSERT INTO ventas1
            (Folio, CodigoBarras, FechaVenta, MetodoPago,
             TotalPagar, CalculoUtilidades)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

            String sqlUtilidad = """
            SELECT p.PrecioInicial, p.PrecioFinal, nv.Cantidad
            FROM productos p
            INNER JOIN nuevaventa nv
            ON p.CodigoBarras = nv.CodigoBarras
            WHERE nv.Folio = ? AND nv.CodigoBarras = ?
        """;

            ps = cn.prepareStatement(sql);
            psUtilidad = cn.prepareStatement(sqlUtilidad);

            java.sql.Date fechaVenta
                    = java.sql.Date.valueOf(LocalDate.now());

            for (int i = 0; i < tabla.getRowCount(); i++) {

                String codigoBarras
                        = tabla.getValueAt(i, 2).toString();

                // Consultar precios y cantidad
                psUtilidad.setInt(1, folio);
                psUtilidad.setString(2, codigoBarras);

                BigDecimal utilidad = BigDecimal.ZERO;

                try (ResultSet rs = psUtilidad.executeQuery()) {

                    if (rs.next()) {

                        BigDecimal precioCompra
                                = rs.getBigDecimal("PrecioInicial");

                        BigDecimal precioVenta
                                = rs.getBigDecimal("PrecioFinal");

                        // Calcular utilidad en Java
                        // Calcular utilidad en Java
                        utilidad = precioVenta.subtract(precioCompra);
                    }
                }

                // Insertar venta y utilidad
                ps.setInt(1, folio);
                ps.setString(2, codigoBarras);
                ps.setDate(3, fechaVenta);
                ps.setString(4, metodoPago);
                ps.setDouble(5, totalVenta);
                ps.setBigDecimal(6, utilidad);

                ps.executeUpdate();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al guardar venta:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }

                if (psUtilidad != null) {
                    psUtilidad.close();
                }

                if (cn != null) {
                    cn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarVentasDesdeBD() {

        DefaultTableModel modelo
                = (DefaultTableModel) jTable1.getModel();

        modelo.setRowCount(0);

        try (Connection cn = new conexion().conectar()) {

            String sql = """
            SELECT Folio, CodigoBarras, FechaVenta,
                   MetodoPago, TotalPagar, CalculoUtilidades
            FROM ventas1
            ORDER BY idVenta DESC
        """;

            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                double TotalPagar
                        = rs.getDouble("TotalPagar");

                double CalculoUtilidades
                        = rs.getDouble("CalculoUtilidades");

                modelo.addRow(new Object[]{
                    rs.getInt("Folio"),
                    rs.getString("CodigoBarras"),
                    rs.getDate("FechaVenta"),
                    rs.getString("MetodoPago"),
                    df.format(TotalPagar),
                    df.format(CalculoUtilidades)
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void centrarColumnas() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }
    }

    public double recalcularTotalVenta() {

        double total = 0.0;

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        // Columna 4 = TotalPagar
        int columnaTotalPagar = 4;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object valor = model.getValueAt(i, columnaTotalPagar);

            if (valor != null && !valor.toString().trim().isEmpty()) {

                total += Double.parseDouble(
                        valor.toString().replace("$", "")
                                .replace(",", "")
                                .trim()
                );
            }
        }

        // Redondear a dos decimales
        total = Math.round(total * 100.0) / 100.0;

        jLabel7.setText(
                "Total Ventas = $" + String.format("%.2f", total) + " pesos"
        );

        return total;
    }

    public void recalcularBalance() {

        double totalVentas = recalcularTotalVenta();
        double totalUtilidades = recalcularTotalUtilidades();

        double balance = totalVentas + totalUtilidades;
        balance = Math.round(balance * 100.0) / 100.0;

        jLabel9.setText("Gran Total = $" + String.format("%.2f", balance) + " pesos");
    }

    public double recalcularTotalUtilidades() {

        double total = 0.0;

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        // Columna 5 = Utilidades
        int columnaUtilidades = 5;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object valor = model.getValueAt(i, columnaUtilidades);

            if (valor != null && !valor.toString().trim().isEmpty()) {

                total += Double.parseDouble(
                        valor.toString().replace("$", "")
                                .replace(",", "")
                                .trim()
                );
            }
        }

        // Redondear a dos decimales
        total = Math.round(total * 100.0) / 100.0;

        jLabel8.setText(
                "Total Utilidades = $" + String.format("%.2f", total) + " pesos"
        );

        return total;
    }

    public void aplicarFiltroGeneral() {

        DefaultTableModel modeloVentas = (DefaultTableModel) jTable1.getModel();
        modeloVentas.setRowCount(0);

        java.util.Date inicio = jDateChooser1.getDate();
        java.util.Date fin = jDateChooser2.getDate();
        String metodoPago = jComboBox1.getSelectedItem().toString().trim();

        // 🚫 Si no hay fechas, no mostrar nada
        if (inicio == null || fin == null) {
            return;
        }

        try {
            Connection cn = new conexion().conectar();

            // =======================
            // ======= VENTAS ========
            // =======================
            String sqlVentas = """
            SELECT Folio, CodigoBarras, FechaVenta, MetodoPago, TotalPagar, CalculoUtilidades
            FROM ventas1
            WHERE FechaVenta BETWEEN ? AND ?
        """;

            // Si hay método seleccionado y no es "Seleccione"
            if (!metodoPago.equals("Seleccione") && !metodoPago.isEmpty()) {
                sqlVentas += " AND MetodoPago = ?";
            }

            PreparedStatement psV = cn.prepareStatement(sqlVentas);
            psV.setDate(1, new java.sql.Date(inicio.getTime()));
            psV.setDate(2, new java.sql.Date(fin.getTime()));

            if (!metodoPago.equals("Seleccione") && !metodoPago.isEmpty()) {
                psV.setString(3, metodoPago);
            }

            ResultSet rsV = psV.executeQuery();

            while (rsV.next()) {
                modeloVentas.addRow(new Object[]{
                    rsV.getInt("Folio"),
                    rsV.getString("CodigoBarras"),
                    rsV.getDate("FechaVenta"),
                    rsV.getString("MetodoPago"),
                    df.format(rsV.getDouble("TotalPagar")),
                    df.format(rsV.getDouble("CalculoUtilidades"))
                });
            }

            rsV.close();
            psV.close();
            cn.close();

            centrarColumnas();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void exportarVentasYGastosPDF(JTable ventas) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar archivo PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivo PDF (.pdf)", "pdf"));

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String ruta = chooser.getSelectedFile().toString();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta += ".pdf";
        }

        // Documento horizontal
        Document documento = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            /* ================= FECHA ================= */
            String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

            Font fuenteFecha = FontFactory.getFont(
                    FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);

            Paragraph fecha = new Paragraph("Fecha de generación: " + fechaHora, fuenteFecha);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            documento.add(fecha);

            documento.add(new Paragraph("\n"));

            /* ================= TÍTULO ================= */
            Font fuenteTitulo = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);

            Paragraph titulo = new Paragraph("UTILIDADES A DETALLE\n\n", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            /* ================= TABLA VENTAS ================= */
            agregarTablaPDF(documento, "VENTAS", ventas);
            documento.add(new Paragraph("\n\n"));

            /* ================= CÁLCULOS ================= */
            double totalVentas = calcularTotal(ventas, 4);
            double totalUtilidades = calcularTotal(ventas, 5);
            double granTotal = totalVentas + totalUtilidades;

            PdfPTable tablaTotales = new PdfPTable(1);
            tablaTotales.setWidthPercentage(40);
            tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.setSpacingBefore(10f);

            // 🔥 CLAVE PARA QUE NO SE VAYA A OTRA HOJA
            tablaTotales.setKeepTogether(true);
            tablaTotales.setSplitLate(false);

            tablaTotales.addCell(crearCeldaTotal(
                    "Total Ventas = $" + String.format("%.2f", totalVentas),
                    new BaseColor(0, 128, 0))); // verde fuerte

            tablaTotales.addCell(crearCeldaTotal(
                    "Total Utilidades = $" + String.format("%.2f", totalUtilidades),
                    new BaseColor(180, 0, 0))); // rojo fuerte

            tablaTotales.addCell(crearCeldaTotal(
                    "Gran Total = $" + String.format("%.2f", granTotal),
                    granTotal >= 0
                            ? new BaseColor(0, 128, 0)
                            : new BaseColor(180, 0, 0)));

            documento.add(tablaTotales);

            documento.close();
            Desktop.getDesktop().open(new File(ruta));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void agregarTablaPDF(Document documento, String titulo, JTable tabla) throws Exception {

        Font fuenteTitulo = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);

        Paragraph pTitulo = new Paragraph(titulo + "\n\n", fuenteTitulo);
        pTitulo.setSpacingBefore(10f);
        documento.add(pTitulo);

        PdfPTable pdfTabla = new PdfPTable(tabla.getColumnCount());
        pdfTabla.setWidthPercentage(100);

        pdfTabla.setHeaderRows(1);
        pdfTabla.setSplitLate(false);
        pdfTabla.setSplitRows(true);

        // Encabezado
        Font fuenteEncabezado = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);

        BaseColor fondoEncabezado = new BaseColor(170, 170, 170);

        for (int c = 0; c < tabla.getColumnCount(); c++) {
            PdfPCell celda = new PdfPCell(new Phrase(tabla.getColumnName(c), fuenteEncabezado));
            celda.setBackgroundColor(fondoEncabezado);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setPadding(8);
            pdfTabla.addCell(celda);
        }

        // Datos
        Font fuenteDatos = FontFactory.getFont(FontFactory.HELVETICA, 10);

        BaseColor fila1 = new BaseColor(200, 245, 245);
        BaseColor fila2 = new BaseColor(230, 255, 255);

        for (int f = 0; f < tabla.getRowCount(); f++) {

            BaseColor fondo = (f % 2 == 0) ? fila1 : fila2;

            for (int c = 0; c < tabla.getColumnCount(); c++) {

                Object v = tabla.getValueAt(f, c);

                PdfPCell celda = new PdfPCell(
                        new Phrase(v != null ? v.toString() : "", fuenteDatos));

                celda.setBackgroundColor(fondo);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(7);

                pdfTabla.addCell(celda);
            }
        }

        documento.add(pdfTabla);
    }

    private PdfPCell crearCeldaTotal(String texto, BaseColor color) {
        Font fuente = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 12, color);

        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celda.setPadding(6);
        return celda;
    }

    private double calcularTotal(JTable tabla, int colTotal) {

        double total = 0;
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {

            Object v = model.getValueAt(i, colTotal);

            if (v != null) {
                try {
                    total += Double.parseDouble(v.toString().replace(",", ""));
                } catch (NumberFormatException e) {
                    System.out.println("Valor no numérico ignorado: " + v);
                }
            }
        }

        return Math.round(total * 100.0) / 100.0;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, jButton2, btnMenuCapturista);
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
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnMenuCapturista = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("UTILIDADES A DETALLE");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, -1, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Menu de Modulos");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 250, 40));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Fecha Inicial");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, -1, -1));

        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 180, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Fecha Final");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, -1, -1));

        jDateChooser2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jDateChooser2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser2PropertyChange(evt);
            }
        });
        getContentPane().add(jDateChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 80, 160, 30));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 170, 70, 40));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Filtro por Tipo de Pago");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 80, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 80, 190, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 204, 102));
        jLabel5.setText("VENTAS");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 120, -1, -1));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Folio", "CodigoBarras", "FechaVenta", "MetodoPago", "TotalPagar", "CalculoUtilidades"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 800, 160));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 204, 51));
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 10, 280, 30));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 170, 70, 40));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 110, 270, 30));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 51, 51));
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 70, 280, 30));

        btnMenuCapturista.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuCapturista.setText("Menu del Supervisor");
        btnMenuCapturista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuCapturistaActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuCapturista, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 270, 230, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1300, 320));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jDateChooser2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser2PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jDateChooser2PropertyChange

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            aplicarFiltroGeneral();
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        exportarVentasYGastosPDF(jTable1);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ExportarExcel2 obj;

        try {
            obj = new ExportarExcel2();
            obj.exportarExcel2(jTable1);
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(UtlidadesDetalle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UtlidadesDetalle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UtlidadesDetalle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UtlidadesDetalle.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
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
                new UtlidadesDetalle().setVisible(true);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
