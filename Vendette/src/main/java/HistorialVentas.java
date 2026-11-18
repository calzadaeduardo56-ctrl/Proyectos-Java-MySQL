
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class HistorialVentas extends javax.swing.JFrame {

    private ImageIcon imagen;
    private Icon icocno;
    private boolean modoCapturaManual = true;
    private String puesto;

    public HistorialVentas() {
        initComponents();

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

            aplicarFiltroPorFechas();
        });

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/estadisticas.png"));
        setIconImage(logoIcon.getImage());

        jButton4.setIcon(getIconUrl("/imagen/barra-grafica.png", 30, 30));

        jButton7.setIcon(getIconUrl("/imagen/informe-de-venta.png", 30, 30));

        jButton6.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        centrarColumnas();

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        try {

            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0);

            Connection cn = new conexion().conectar();

            // 🔹 Traemos iddetalle porque será el Folio real
            String sql = "SELECT iddetalle, Producto, FechaVenta, ContadorProducto FROM nuevaventa";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // 🔹 INSERT CORREGIDO
            String sqlInsert = "INSERT INTO histventas "
                    + "(Folio, Producto, FechaVenta, ContadorProducto) "
                    + "VALUES (?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "Producto = VALUES(Producto), "
                    + "FechaVenta = VALUES(FechaVenta), "
                    + "ContadorProducto = VALUES(ContadorProducto)";

            PreparedStatement psInsert = cn.prepareStatement(sqlInsert);

            while (rs.next()) {

                // 🔥 iddetalle ahora es el Folio
                int Folio = rs.getInt("iddetalle");
                String Producto = rs.getString("Producto");
                java.sql.Date FechaVenta = rs.getDate("FechaVenta");
                int ContadorProducto = rs.getInt("ContadorProducto");

                psInsert.setInt(1, Folio);
                psInsert.setString(2, Producto);
                psInsert.setDate(3, FechaVenta);
                psInsert.setInt(4, ContadorProducto);

                psInsert.executeUpdate();

                // 👇 No mostramos iddetalle en la tabla
                modelo.addRow(new Object[]{
                    Folio,
                    Producto,
                    FechaVenta,
                    ContadorProducto
                });
            }

            psInsert.close();
            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        HistorialVentas.this,
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

    public void aplicarFiltroPorFechas() {

        DefaultTableModel modeloVentas = (DefaultTableModel) jTable1.getModel();

        // 🔥 LIMPIAR SIEMPRE
        modeloVentas.setRowCount(0);

        java.util.Date inicio = jDateChooser1.getDate();
        java.util.Date fin = jDateChooser2.getDate();

        // Si no hay fechas, NO CARGAR NADA
        if (inicio == null || fin == null) {
            return;
        }

        try {
            Connection cn = new conexion().conectar();

            // ===== VENTAS =====
            String sqlVentas = """
            SELECT Folio, Producto, FechaVenta, ContadorProducto FROM histventas WHERE FechaVenta BETWEEN ? AND ?
        """;

            PreparedStatement psV = cn.prepareStatement(sqlVentas);
            psV.setDate(1, new java.sql.Date(inicio.getTime()));
            psV.setDate(2, new java.sql.Date(fin.getTime()));
            ResultSet rs = psV.executeQuery();

            while (rs.next()) {

                modeloVentas.addRow(new Object[]{
                    rs.getInt("Folio"),
                    rs.getString("Producto"),
                    rs.getDate("FechaVenta"),
                    rs.getInt("ContadorProducto")
                }
                );
            }
            centrarColumnas();
            rs.close();
            psV.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
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
        }
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, jButton6, btnMenuContador);
    }

    private void generarGraficaProductosMasVendidos() {

        java.util.Date inicio = jDateChooser1.getDate();
        java.util.Date fin = jDateChooser2.getDate();

        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rango de fechas");
            return;
        }

        ArrayList<Map.Entry<String, Integer>> lista = new ArrayList<>();

        try {
            Connection cn = new conexion().conectar();

            String sql = """
            SELECT Producto, COUNT(*) as Total
            FROM histventas
            WHERE FechaVenta BETWEEN ? AND ?
              AND ContadorProducto > 0
            GROUP BY Producto
        """;

            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(inicio.getTime()));
            ps.setDate(2, new java.sql.Date(fin.getTime()));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(
                        Map.entry(
                                rs.getString("Producto"),
                                rs.getInt("Total")
                        )
                );
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay ventas en ese rango");
            return;
        }
        // 🔥 Ordenar DESC
        lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int mitad = (int) Math.ceil(lista.size() / 2.0);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < mitad; i++) {
            dataset.addValue(
                    lista.get(i).getValue(),
                    "Cantidad",
                    lista.get(i).getKey()
            );
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Productos mas vendidos",
                "Producto",
                "Cantidad Vendida",
                dataset
        );

        chart.removeLegend();
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 20));
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 16));
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6)
        );

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 16));

        int max = lista.get(0).getValue();
        rangeAxis.setRange(0, max + 1);

        // 🎨 MISMOS COLORES
        BarRenderer renderer = new BarRenderer() {
            private final Paint[] colores = {
                new Color(79, 129, 189),
                new Color(192, 80, 77),
                new Color(155, 187, 89),
                new Color(128, 100, 162),
                new Color(75, 172, 198),
                new Color(247, 150, 70),
                new Color(146, 208, 80),
                new Color(0, 176, 240)
            };

            @Override
            public Paint getItemPaint(int row, int column) {
                return colores[column % colores.length];
            }
        };

        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);

        // 🔥 Mostrar cantidad a la derecha
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getIntegerInstance())
        );

        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));

        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(
                        ItemLabelAnchor.OUTSIDE3,
                        TextAnchor.CENTER_LEFT
                )
        );

        plot.setRenderer(renderer);

        // Formatear fechas
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = formato.format(inicio);
        String fechaFin = formato.format(fin);

// Crear subtítulo con rango
        TextTitle subtitulo = new TextTitle(
                "Rango: " + fechaInicio + "  -  " + fechaFin,
                new Font("Segoe UI", Font.BOLD, 14)
        );

        subtitulo.setPaint(Color.DARK_GRAY);

        chart.addSubtitle(subtitulo);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);

        JButton btnExportarPDF = new JButton("Exportar");

// 🔥 Cargar icono desde resources
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/imagen/expediente.png"));

// Escalar imagen
        java.awt.Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(28, 28, java.awt.Image.SCALE_SMOOTH);
        btnExportarPDF.setIcon(new ImageIcon(imagenEscalada));

// Texto a la derecha del icono
        btnExportarPDF.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnExportarPDF.setIconTextGap(10);

// Tamaño del botón
        btnExportarPDF.setPreferredSize(new Dimension(200, 55));

// Fuente
        btnExportarPDF.setFont(new Font("Segoe UI", Font.BOLD, 16));

// Estilo moderno
        btnExportarPDF.setFocusPainted(false);
        btnExportarPDF.setBackground(Color.WHITE);
        btnExportarPDF.setForeground(Color.BLACK);
        btnExportarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));

// Bordes redondeados
        btnExportarPDF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        btnExportarPDF.addActionListener(e -> {
            exportarGraficaYTablaPDF(chart, jTable1);
        });

// Panel contenedor
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.add(chartPanel, BorderLayout.CENTER);
        panelPrincipal.add(btnExportarPDF, BorderLayout.SOUTH);

        JFrame ventana = new JFrame("Productos mas vendidos");
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setContentPane(panelPrincipal);
        ventana.setSize(900, 650);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void generarGraficaProductosMenosVendidos() {

        java.util.Date inicio = jDateChooser1.getDate();
        java.util.Date fin = jDateChooser2.getDate();

        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rango de fechas");
            return;
        }

        ArrayList<Map.Entry<String, Integer>> lista = new ArrayList<>();

        try {
            Connection cn = new conexion().conectar();

            String sql = """
            SELECT Producto, COUNT(*) as Total
            FROM histventas
            WHERE FechaVenta BETWEEN ? AND ?
              AND ContadorProducto > 0
            GROUP BY Producto           
        """;

            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(inicio.getTime()));
            ps.setDate(2, new java.sql.Date(fin.getTime()));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(Map.entry(
                        rs.getString("Producto"),
                        rs.getInt("Total")
                ));
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        if (lista.size() <= 3) {
            JOptionPane.showMessageDialog(this, "No hay suficientes productos para calcular menos vendidos.");
            return;
        }

        // 🔥 Ordenar DESC para sacar los más vendidos
        lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int mitad = (int) Math.ceil(lista.size() / 2.0);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = mitad; i < lista.size(); i++) {
            dataset.addValue(
                    lista.get(i).getValue(),
                    "Cantidad",
                    lista.get(i).getKey()
            );
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Productos menos vendidos",
                "Producto",
                "Cantidad Vendida",
                dataset
        );

        chart.removeLegend();
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 20));
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 16));
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6)
        );

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 16));

        int max = 0;

        for (int i = 0; i < mitad; i++) {
            int valor = lista.get(i).getValue();
            if (valor > max) {
                max = valor;
            }
        }

        rangeAxis.setRange(0, max + 1);

        // 🎨 MISMOS COLORES
        BarRenderer renderer = new BarRenderer() {
            private final Paint[] colores = {
                new Color(79, 129, 189),
                new Color(192, 80, 77),
                new Color(155, 187, 89),
                new Color(128, 100, 162),
                new Color(75, 172, 198),
                new Color(247, 150, 70),
                new Color(146, 208, 80),
                new Color(0, 176, 240)
            };

            @Override
            public Paint getItemPaint(int row, int column) {
                return colores[column % colores.length];
            }
        };

        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);

        // 🔥 Mostrar cantidad a la derecha
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getIntegerInstance())
        );

        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));

        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(
                        ItemLabelAnchor.OUTSIDE3,
                        TextAnchor.CENTER_LEFT
                )
        );

        plot.setRenderer(renderer);

        // Formatear fechas
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = formato.format(inicio);
        String fechaFin = formato.format(fin);

// Crear subtítulo con rango
        TextTitle subtitulo = new TextTitle(
                "Rango: " + fechaInicio + "  -  " + fechaFin,
                new Font("Segoe UI", Font.BOLD, 14)
        );

        subtitulo.setPaint(Color.DARK_GRAY);

        chart.addSubtitle(subtitulo);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);

        JButton btnExportarPDF = new JButton("Exportar");

// 🔥 Cargar icono desde resources
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/imagen/expediente.png"));

// Escalar imagen
        java.awt.Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(28, 28, java.awt.Image.SCALE_SMOOTH);
        btnExportarPDF.setIcon(new ImageIcon(imagenEscalada));

// Texto a la derecha del icono
        btnExportarPDF.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnExportarPDF.setIconTextGap(10);

// Tamaño del botón
        btnExportarPDF.setPreferredSize(new Dimension(200, 55));

// Fuente
        btnExportarPDF.setFont(new Font("Segoe UI", Font.BOLD, 16));

// Estilo moderno
        btnExportarPDF.setFocusPainted(false);
        btnExportarPDF.setBackground(Color.WHITE);
        btnExportarPDF.setForeground(Color.BLACK);
        btnExportarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));

// Bordes redondeados
        btnExportarPDF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        btnExportarPDF.addActionListener(e -> {
            exportarGraficaYTablaPDF(chart, jTable1);
        });

// Panel contenedor
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.add(chartPanel, BorderLayout.CENTER);
        panelPrincipal.add(btnExportarPDF, BorderLayout.SOUTH);

        JFrame ventana = new JFrame("Productos menos vendidos");
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setContentPane(panelPrincipal);
        ventana.setSize(900, 650);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    public void exportarGraficaYTablaPDF(JFreeChart chart, JTable tabla) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Reporte PDF");

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            String ruta = chooser.getSelectedFile().toString();
            if (!ruta.toLowerCase().endsWith(".pdf")) {
                ruta += ".pdf";
            }

            Document documento = new Document(PageSize.A4.rotate());

            try {
                PdfWriter.getInstance(documento, new FileOutputStream(ruta));
                documento.open();

                // Fecha actual
                String fechaHora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(new java.util.Date());

                Paragraph fecha = new Paragraph(
                        "Reporte generado el: " + fechaHora + "\n\n",
                        FontFactory.getFont(FontFactory.HELVETICA, 10)
                );
                fecha.setAlignment(Element.ALIGN_RIGHT);
                documento.add(fecha);

                // 🔥 Título
                Paragraph titulo = new Paragraph(
                        "REPORTE DE VENTAS\n\n",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE)
                );
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);

                // 🔥 Convertir gráfica a imagen
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ChartUtils.writeChartAsPNG(baos, chart, 800, 400);
                com.itextpdf.text.Image imagenGrafica
                        = com.itextpdf.text.Image.getInstance(baos.toByteArray());
                imagenGrafica.setAlignment(Element.ALIGN_CENTER);
                imagenGrafica.scaleToFit(750, 400);

                // Agregar gráfica
                documento.add(imagenGrafica);

// 🔥 Nueva hoja
                documento.newPage();

/// 🔥 Obtener dataset de la gráfica
                CategoryPlot plot = chart.getCategoryPlot();
                CategoryDataset dataset = plot.getDataset();

// Guardar los productos que aparecen en la gráfica
                Set<String> productosGrafica = new HashSet<>();

                for (int i = 0; i < dataset.getColumnCount(); i++) {
                    productosGrafica.add(dataset.getColumnKey(i).toString());
                }

// 🔥 Crear tabla PDF
                PdfPTable pdfTabla = new PdfPTable(tabla.getColumnCount());
                pdfTabla.setWidthPercentage(100);

// Encabezado
                for (int col = 0; col < tabla.getColumnCount(); col++) {
                    PdfPCell celda = new PdfPCell(
                            new Phrase(tabla.getColumnName(col),
                                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE))
                    );
                    celda.setBackgroundColor(new BaseColor(0, 102, 204));
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTabla.addCell(celda);
                }

// 🔥 Solo agregar filas que estén en la gráfica
                for (int fila = 0; fila < tabla.getRowCount(); fila++) {

                    String productoTabla = tabla.getValueAt(fila, 1).toString();
                    // ⚠️ IMPORTANTE: aquí usa la columna donde está el nombre del producto

                    if (productosGrafica.contains(productoTabla)) {

                        for (int col = 0; col < tabla.getColumnCount(); col++) {

                            Object valor = tabla.getValueAt(fila, col);

                            PdfPCell celda = new PdfPCell(
                                    new Phrase(valor != null ? valor.toString() : "",
                                            FontFactory.getFont(FontFactory.HELVETICA, 10))
                            );

                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfTabla.addCell(celda);
                        }
                    }
                }

                documento.add(pdfTabla);
                documento.close();

                Desktop.getDesktop().open(new File(ruta));

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al exportar PDF");
            }
        }
    }

    private void mostrarMenuSeleccionGrafica() {

        JDialog dialog = new JDialog(this, "Seleccionar Reporte", true);
        dialog.setSize(400, 180);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pregunta
        JLabel lblPregunta = new JLabel("¿Qué tipo de gráfica deseas ver?");
        lblPregunta.setHorizontalAlignment(SwingConstants.CENTER);
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(lblPregunta, BorderLayout.NORTH);

        // Panel botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));

        JButton btnAltas = new JButton("Grafica de Altas");
        JButton btnBajas = new JButton("Grafica de Bajas");

        // Acción botón ALTAS
        btnAltas.addActionListener(e -> {
            dialog.dispose();
            generarGraficaProductosMasVendidos();
        });

        // Acción botón BAJAS
        btnBajas.addActionListener(e -> {
            dialog.dispose();
            generarGraficaProductosMenosVendidos();
        });

        panelBotones.add(btnAltas);
        panelBotones.add(btnBajas);

        panel.add(panelBotones, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
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
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnMenuContador = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("GRAFICAS DE VENTAS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, -1, -1));

        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 180, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Fecha de Inicio");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jDateChooser2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, 170, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Fecha de Termino");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta de Credito o Debito", "Transferencia" }));
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 190, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Tipo de Pago");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, -1, -1));

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setText("Menu de Modulos");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 290, 40));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Generar Grafica");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 280, 40));

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setText("Informe de Ventas");
        getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 170, 260, 40));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Folio", "Producto", "FechaVenta", "ContadorProducto"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 970, 300));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Cajero ");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 110, 280, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 990, 530));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnMenuContadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuContadorActionPerformed
        MenuCajero menu = new MenuCajero();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuContadorActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        mostrarMenuSeleccionGrafica();

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
            java.util.logging.Logger.getLogger(HistorialVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistorialVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistorialVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistorialVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new HistorialVentas().setVisible(true);
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
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
