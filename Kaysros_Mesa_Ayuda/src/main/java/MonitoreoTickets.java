
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import java.io.File;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import java.awt.Color;
import java.sql.ResultSetMetaData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CategoryPlot;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.axis.NumberAxis;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.table.TableRowSorter;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.title.TextTitle;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import com.itextpdf.text.Image;
import com.itextpdf.text.Element;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class MonitoreoTickets extends javax.swing.JFrame {

    private ImageIcon imagen;
    private Icon icocno;
    private String puesto;

    public MonitoreoTickets() {
        initComponents();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/controlar.png"));
        setIconImage(logoIcon.getImage());

        jButton3.setIcon(getIconUrl("/imagen/reporte.png", 30, 30));

        jButton4.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton6.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton7.setIcon(getIconUrl("/imagen/adjunto.png", 30, 30));

        btnMenuSupervisor.setIcon(getIconUrl("/imagen/menu1.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        MonitoreoTickets.this,
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

        try {

            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0);

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            // ==========================================================
            // CONSULTAR TICKETS Y SU STATUS ACTUAL EN TOMAR
            // ==========================================================
            String sql
                    = "SELECT a.Id_Ticket, "
                    + "a.Nombre_Cliente, "
                    + "a.Tipo_Poliza, "
                    + "a.Nombre_Producto, "
                    + "asig.Nombre_Empleado, "
                    + "asig.Fecha_Asignacion, "
                    + "asig.Hora_Asignacion, "
                    + "t.Status AS StatusActual "
                    + "FROM alta_ticket a "
                    + "LEFT JOIN tomar t "
                    + "ON a.Id_Ticket = t.Id_Monitoreo "
                    + "LEFT JOIN asignacion asig "
                    + "ON a.Id_Ticket = asig.Id_Asignacion";

            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int idTicket = rs.getInt("Id_Ticket");

                String cliente = rs.getString("Nombre_Cliente") != null
                        ? rs.getString("Nombre_Cliente")
                        : "";

                String empleado = rs.getString("Nombre_Empleado") != null
                        ? rs.getString("Nombre_Empleado")
                        : "";

                String tipoPoliza = rs.getString("Tipo_Poliza") != null
                        ? rs.getString("Tipo_Poliza")
                        : "";

                String tipoProducto = rs.getString("Nombre_Producto") != null
                        ? rs.getString("Nombre_Producto")
                        : "";

                java.sql.Date fecha = rs.getDate("Fecha_Asignacion");
                java.sql.Time hora = rs.getTime("Hora_Asignacion");

                String statusActual = rs.getString("StatusActual");

                // ==========================================================
                // DETERMINAR SI EL MONITOREO YA ESTÁ FINALIZADO
                // ==========================================================
                boolean finalizado = statusActual != null
                        && statusActual.trim().equalsIgnoreCase("Se ha Realizado");

                boolean cancelado = statusActual != null
                        && statusActual.trim().equalsIgnoreCase("Cancelado");

                // ==========================================================
// VARIABLES DE LOS CONTADORES
// ==========================================================
                long diasRetraso = 0;
                long horasRetraso = 0;
                long minutosRetraso = 0;

// ==========================================================
// SOLO CALCULAR SI EL TICKET SIGUE EN PROCESO
// ==========================================================
                if (!finalizado && !cancelado
                        && fecha != null && hora != null) {

                    LocalDateTime fechaAsignacion = LocalDateTime.of(
                            fecha.toLocalDate(),
                            hora.toLocalTime()
                    );

                    LocalDateTime ahora = LocalDateTime.now();

                    // ======================================================
                    // MINUTOS TOTALES TRANSCURRIDOS
                    // ======================================================
                    long minutosTotales = ChronoUnit.MINUTES.between(
                            fechaAsignacion,
                            ahora
                    );

                    // Evitar valores negativos
                    if (minutosTotales < 0) {
                        minutosTotales = 0;
                    }

                    // ======================================================
                    // MINUTOS DE RETRASO
                    // ======================================================
                    minutosRetraso = minutosTotales;

                    // ======================================================
                    // HORAS DE RETRASO
                    // ======================================================
                    horasRetraso = minutosTotales / 60;

                    // ======================================================
                    // DÍAS DE RETRASO
                    // CAMBIA CUANDO CAMBIA EL DÍA CALENDARIO
                    // ======================================================
                    diasRetraso = ChronoUnit.DAYS.between(
                            fechaAsignacion.toLocalDate(),
                            ahora.toLocalDate()
                    );

                    // Evitar valores negativos
                    if (diasRetraso < 0) {
                        diasRetraso = 0;
                    }
                }
                String sqlInsert
                        = "INSERT INTO tomar "
                        + "(Id_Monitoreo, "
                        + "Nombre_Cliente, "
                        + "Nombre_Empleado, "
                        + "Tipo_Poliza, "
                        + "Nombre_Producto, "
                        + "Fecha_Asignacion, "
                        + "Hora_Asignacion, "
                        + "Horas_Retraso, "
                        + "Minutos_Retraso, "
                        + "Dias_Retraso, "
                        + "Status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "Nombre_Cliente = VALUES(Nombre_Cliente), "
                        + "Nombre_Empleado = VALUES(Nombre_Empleado), "
                        + "Tipo_Poliza = VALUES(Tipo_Poliza), "
                        + "Nombre_Producto = VALUES(Nombre_Producto), "
                        + "Fecha_Asignacion = VALUES(Fecha_Asignacion), "
                        + "Hora_Asignacion = VALUES(Hora_Asignacion), "
                        // ==================================================
                        // SI YA FINALIZÓ, CONSERVAR LOS CONTADORES
                        // ==================================================

                        + "Horas_Retraso = IF(Status = 'Se ha Realizado' "
                        + "OR Status = 'Cancelado', "
                        + "Horas_Retraso, VALUES(Horas_Retraso)), "
                        + "Minutos_Retraso = IF(Status = 'Se ha Realizado' "
                        + "OR Status = 'Cancelado', "
                        + "Minutos_Retraso, VALUES(Minutos_Retraso)), "
                        + "Dias_Retraso = IF(Status = 'Se ha Realizado' "
                        + "OR Status = 'Cancelado', "
                        + "Dias_Retraso, VALUES(Dias_Retraso)), "
                        // ==================================================
                        // SI YA FINALIZÓ, CONSERVAR EL STATUS
                        // ==================================================

                        + "Status = IF(Status = 'Se ha Realizado' "
                        + "OR Status = 'Cancelado', "
                        + "Status, VALUES(Status))";

                PreparedStatement psInsert = cn.prepareStatement(sqlInsert);

                psInsert.setInt(1, idTicket);
                psInsert.setString(2, cliente);
                psInsert.setString(3, empleado);
                psInsert.setString(4, tipoPoliza);
                psInsert.setString(5, tipoProducto);
                psInsert.setDate(6, fecha);
                psInsert.setTime(7, hora);

                psInsert.setLong(8, horasRetraso);
                psInsert.setLong(9, minutosRetraso);
                psInsert.setLong(10, diasRetraso);

                // ==========================================================
                // STATUS NUEVO
                // ==========================================================
                psInsert.setString(11, "En Proceso");

                psInsert.executeUpdate();
                psInsert.close();

                // ==========================================================
                // MOSTRAR EN JTable
                // ==========================================================
                String fechaStr = (fecha != null)
                        ? fecha.toString()
                        : "";

                String horaStr = (hora != null)
                        ? hora.toString()
                        : "";

                modelo.addRow(new Object[]{
                    idTicket,
                    cliente,
                    empleado,
                    tipoPoliza,
                    tipoProducto,
                    fechaStr,
                    horaStr
                });
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException ex) {

            System.out.println("Error SQL: " + ex.getMessage());
            ex.printStackTrace();

        } catch (Exception e) {

            System.out.println("Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuSupervisor);
    }

    private void insertarMonitoreo() {

        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe seleccionar un registro para actualizar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int idTicket = Integer.parseInt(
                jTable1.getValueAt(filaSeleccionada, 0).toString()
        );

        // ==========================================================
        // CONFIRMAR FINALIZACIÓN
        // ==========================================================
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de finalizar este monitoreo?",
                "Confirmar finalización",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // ==========================================================
        // OBTENER FECHA DE TÉRMINO DEL JDATECHOOSER
        // ==========================================================
        java.util.Date fechaFinalDate = jDateChooser1.getDate();

        if (fechaFinalDate == null) {

            JOptionPane.showMessageDialog(
                    null,
                    "Seleccione una fecha de término.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        LocalDate fechaFinal = fechaFinalDate
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // ==========================================================
        // SOLO ACTUALIZAR FECHA DE TÉRMINO Y STATUS
        // ==========================================================
        //
        // IMPORTANTE:
        // NO modificamos:
        // Dias_Retraso
        // Horas_Retraso
        // Minutos_Retraso
        //
        // Se conservan los valores que ya están guardados.
        // ==========================================================
        String sql = """
        UPDATE tomar
        SET Fecha_Termino = ?,
            Status = ?
        WHERE Id_Monitoreo = ?
        """;

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {

            pst.setDate(1, java.sql.Date.valueOf(fechaFinal));
            pst.setString(2, "Se ha Realizado");
            pst.setInt(3, idTicket);

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {

                JOptionPane.showMessageDialog(
                        null,
                        "El monitoreo se ha finalizado correctamente.",
                        "Proceso completado",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Solo vuelve a consultar y mostrar los datos
                cargarTabla();
                centrarColumnas();

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "No se encontró el monitoreo seleccionado.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al finalizar el monitoreo:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    public void cargarTabla() {

        final String sqlSelect
                = "SELECT Id_Monitoreo, Nombre_Cliente, Nombre_Empleado, "
                + "Tipo_Poliza, Nombre_Producto, Fecha_Asignacion, "
                + "Hora_Asignacion, Fecha_Termino, Dias_Retraso, "
                + "Horas_Retraso, Minutos_Retraso, Status "
                + "FROM tomar";

        final String[] columnas = {
            "Id_Monitoreo",
            "Nombre_Cliente",
            "Nombre_Empleado",
            "Tipo_Poliza",
            "Nombre_Producto",
            "Fecha_Asignacion",
            "Hora_Asignacion",
            "Fecha_Termino",
            "Dias_Retraso",
            "Horas_Retraso",
            "Minutos_Retraso",
            "Status"
        };

        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        conexion conexion = new conexion();

        try (Connection conn = conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sqlSelect); ResultSet rs = pst.executeQuery()) {

            DateTimeFormatter formatoHora12
                    = DateTimeFormatter.ofPattern("hh:mm a");

            while (rs.next()) {

                int idTicket = rs.getInt("Id_Monitoreo");

                // ==========================================================
                // DATOS DEL REGISTRO
                // ==========================================================
                String cliente = rs.getString("Nombre_Cliente");
                String empleado = rs.getString("Nombre_Empleado");
                String tipoPoliza = rs.getString("Tipo_Poliza");
                String producto = rs.getString("Nombre_Producto");

                java.sql.Date fechaInicioSQL
                        = rs.getDate("Fecha_Asignacion");

                java.sql.Time horaSQL
                        = rs.getTime("Hora_Asignacion");

                java.sql.Date fechaFinalSQL
                        = rs.getDate("Fecha_Termino");

                // ==========================================================
                // FORMATEAR FECHAS
                // ==========================================================
                String fechaInicioStr
                        = fechaInicioSQL != null
                                ? fechaInicioSQL.toString()
                                : "";

                String horaStr
                        = horaSQL != null
                                ? horaSQL.toLocalTime().format(formatoHora12)
                                : "";

                String fechaFinalStr
                        = fechaFinalSQL != null
                                ? fechaFinalSQL.toString()
                                : "";

                // ==========================================================
                // LEER LOS CONTADORES DE LA BD
                // ==========================================================
                //
                // NO SE CALCULAN.
                // SOLO SE LEEN.
                // ==========================================================
                int diasRetraso
                        = rs.getInt("Dias_Retraso");

                int horasRetraso
                        = rs.getInt("Horas_Retraso");

                int minutosRetraso
                        = rs.getInt("Minutos_Retraso");

                String status
                        = rs.getString("Status");

                // ==========================================================
                // AGREGAR FILA AL JTable
                // ==========================================================
                modelo.addRow(new Object[]{
                    idTicket,
                    cliente != null ? cliente : "",
                    empleado != null ? empleado : "",
                    tipoPoliza != null ? tipoPoliza : "",
                    producto != null ? producto : "",
                    fechaInicioStr,
                    horaStr,
                    fechaFinalStr,
                    diasRetraso,
                    horasRetraso,
                    minutosRetraso,
                    status != null ? status : ""
                });
            }

            // ==========================================================
            // MOSTRAR DATOS
            // ==========================================================
            jTable1.setModel(modelo);
            centrarColumnas();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al cargar la tabla:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    private void generarGrafica(ActionEvent e) {

        Map<String, Integer> conteoTickets = new HashMap<>();
        Map<String, String> nombresOriginales = new HashMap<>();
        Set<String> empresasVisibles = new HashSet<>();

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        TableRowSorter<?> sorter = (TableRowSorter<?>) jTable1.getRowSorter(); // Si usas sorter

        int columnaEmpleado = -1;
        int columnaEmpresa = -1;

        // Buscar columnas
        for (int i = 0; i < modelo.getColumnCount(); i++) {

            String colName = modelo.getColumnName(i).trim();

            if ("Nombre_Empleado".equalsIgnoreCase(colName)) {
                columnaEmpleado = i;
            }

            if ("Nombre_Cliente".equalsIgnoreCase(colName)) {
                columnaEmpresa = i;
            }
        }

        if (columnaEmpleado == -1 || columnaEmpresa == -1) {
            return;
        }

        // Recorrer filas visibles
        for (int i = 0; i < jTable1.getRowCount(); i++) {

            int modeloIndex = jTable1.convertRowIndexToModel(i);

            Object valorEmpleado = modelo.getValueAt(modeloIndex, columnaEmpleado);
            Object valorEmpresa = modelo.getValueAt(modeloIndex, columnaEmpresa);

            if (valorEmpleado != null && valorEmpresa != null) {

                String nombreOriginal = valorEmpleado.toString()
                        .replaceAll("\\s+", " ")
                        .trim();

                String claveNormalizada = nombreOriginal.toLowerCase();
                String empresa = valorEmpresa.toString().trim();

                conteoTickets.put(
                        claveNormalizada,
                        conteoTickets.getOrDefault(claveNormalizada, 0) + 1
                );

                nombresOriginales.putIfAbsent(claveNormalizada, nombreOriginal);
                empresasVisibles.add(empresa);
            }
        }

        if (conteoTickets.isEmpty()) {
            return;
        }

        // Nombre empresa
        String nombreEmpresa = (empresasVisibles.size() == 1)
                ? empresasVisibles.iterator().next()
                : "Varias empresas";

        // Ordenar
        List<Map.Entry<String, Integer>> listaOrdenada
                = new ArrayList<>(conteoTickets.entrySet());

        listaOrdenada.sort((a, b)
                -> Integer.compare(b.getValue(), a.getValue()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entry : listaOrdenada) {

            String nombreMostrado = nombresOriginales.get(entry.getKey());

            dataset.addValue(entry.getValue(), "Tickets", nombreMostrado);
        }

        // Crear gráfica
        JFreeChart chart = ChartFactory.createBarChart(
                "Tickets por Empleado",
                "Empleado",
                "Cantidad de Tickets",
                dataset
        );

        chart.addSubtitle(new TextTitle(
                nombreEmpresa,
                new java.awt.Font("Segoe UI", Font.BOLD, 16)
        ));

        chart.setBackgroundPaint(null);
        chart.getTitle().setFont(new java.awt.Font("Segoe UI", Font.BOLD, 18));
        chart.removeLegend();

        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(new Color(0, 0, 0, 0));
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();

        domainAxis.setTickLabelFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        domainAxis.setLabelFont(new java.awt.Font("Segoe UI", Font.BOLD, 16));

        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4)
        );

        // Eje Y dinámico
        int maxTickets = listaOrdenada.get(0).getValue();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        rangeAxis.setTickLabelFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        rangeAxis.setLabelFont(new java.awt.Font("Segoe UI", Font.BOLD, 16));

        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setRange(0, maxTickets + 1);

        // Renderer de barras
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
        renderer.setMaximumBarWidth(0.05);

        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator(
                        "{2}",
                        NumberFormat.getIntegerInstance()
                )
        );

        renderer.setDefaultItemLabelsVisible(true);

        renderer.setDefaultItemLabelFont(
                new java.awt.Font("SansSerif", Font.BOLD, 12)
        );

        renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(
                        ItemLabelAnchor.OUTSIDE12,
                        TextAnchor.BASELINE_CENTER
                )
        );

        plot.setRenderer(renderer);

        // Panel de gráfica
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setOpaque(true);

        JButton btnPDF = new JButton("Exportar PDF");
        btnPDF.setIcon(getIconUrl("/imagen/expediente.png", 30, 30));

        JButton btnExcel = new JButton("Exportar Excel");
        btnExcel.setIcon(getIconUrl("/imagen/sobresalir.png", 30, 30));

// Eventos botones
        btnPDF.addActionListener(ev -> {
            exportarReportePDF(jTable1, chart);
        });

        btnExcel.addActionListener(ev -> {
            try {
                exportarExcel(jTable1, chart);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

// ===== PANEL BOTONES =====
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(btnPDF);
        panelBotones.add(btnExcel);

// ===== PANEL PRINCIPAL =====
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.add(chartPanel, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        JFrame ventanaGrafica = new JFrame();

        ventanaGrafica.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaGrafica.setContentPane(panelPrincipal);
        ventanaGrafica.getContentPane().setBackground(Color.WHITE);

        ventanaGrafica.setSize(800, 600);
        ventanaGrafica.setLocationRelativeTo(null);
        ventanaGrafica.setVisible(true);
    }

    public void exportarReportePDF(JTable tabla, JFreeChart chart) {

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo PDF (.pdf)", "pdf");
        chooser.setFileFilter(filter);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            String ruta = chooser.getSelectedFile().toString();

            if (!ruta.toLowerCase().endsWith(".pdf")) {
                ruta += ".pdf";
            }

            Document documento = new Document(PageSize.A4.rotate());

            try {

                PdfWriter.getInstance(documento, new FileOutputStream(ruta));
                documento.open();

                String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(new Date());

                // ===== ENCABEZADO =====
                Font fuenteEncabezado = FontFactory.getFont("Tahoma", 12, Font.BOLD, BaseColor.DARK_GRAY);

                PdfPTable tablaEncabezado = new PdfPTable(1);
                tablaEncabezado.setWidthPercentage(100);

                PdfPCell celdaFecha = new PdfPCell(
                        new Phrase("Reporte generado el: " + fechaHora, fuenteEncabezado));

                celdaFecha.setBorder(Rectangle.NO_BORDER);
                celdaFecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaFecha.setPadding(8);

                tablaEncabezado.addCell(celdaFecha);

                documento.add(tablaEncabezado);
                documento.add(new Paragraph("\n"));

                // ===== TITULO =====
                Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);

                Paragraph titulo = new Paragraph("REPORTE DE TICKETS\n\n", fuenteTitulo);
                titulo.setAlignment(Paragraph.ALIGN_CENTER);

                documento.add(titulo);

                // ===== GRAFICA =====
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                ChartUtils.writeChartAsPNG(baos, chart, 700, 400);

                Image imagenGrafica = Image.getInstance(baos.toByteArray());
                imagenGrafica.setAlignment(Element.ALIGN_CENTER);

                documento.add(imagenGrafica);
                documento.add(new Paragraph("\n"));

                // ===== TABLA =====
                int columnasVisibles = tabla.getColumnCount();

                PdfPTable pdfTabla = new PdfPTable(columnasVisibles);
                pdfTabla.setWidthPercentage(100);
                pdfTabla.setSpacingBefore(10f);

                Font fuenteColumna = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                BaseColor colorEncabezado = new BaseColor(0, 102, 204);

                // ENCABEZADO
                for (int col = 0; col < tabla.getColumnCount(); col++) {

                    PdfPCell celda = new PdfPCell(new Phrase(tabla.getColumnName(col), fuenteColumna));

                    celda.setBackgroundColor(colorEncabezado);
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda.setPadding(6);

                    pdfTabla.addCell(celda);
                }

                Font fuenteDatos = FontFactory.getFont(FontFactory.HELVETICA, 10);

                for (int fila = 0; fila < tabla.getRowCount(); fila++) {

                    BaseColor fondoFila = (fila % 2 == 0)
                            ? BaseColor.LIGHT_GRAY
                            : BaseColor.WHITE;

                    for (int col = 0; col < tabla.getColumnCount(); col++) {

                        Object valor = tabla.getValueAt(fila, col);

                        PdfPCell celda = new PdfPCell(new Phrase(
                                valor != null ? valor.toString() : "", fuenteDatos));

                        celda.setBackgroundColor(fondoFila);
                        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celda.setPadding(5);

                        pdfTabla.addCell(celda);
                    }
                }

                documento.add(pdfTabla);

                documento.close();

                Desktop.getDesktop().open(new File(ruta));

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    public void exportarExcel(JTable t, JFreeChart chart) throws Exception {

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel (.xlsx)", "xlsx");
        chooser.setFileFilter(filter);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            String ruta = chooser.getSelectedFile().toString();

            if (!ruta.toLowerCase().endsWith(".xlsx")) {
                ruta += ".xlsx";
            }

            File archivoXLSX = new File(ruta);
            Workbook libro = new XSSFWorkbook(); // OK en POI 3.7

            try (FileOutputStream archivo = new FileOutputStream(archivoXLSX)) {

                Sheet hoja = libro.createSheet("Reporte Tickets");

                int columnas = t.getColumnCount(); // 🔥 incluye ID

                // ===== TITULO =====
                org.apache.poi.ss.usermodel.Font fuenteTitulo = libro.createFont();
                fuenteTitulo.setFontHeightInPoints((short) 14);
                fuenteTitulo.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
                fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());

                CellStyle estiloTitulo = libro.createCellStyle();
                estiloTitulo.setFont(fuenteTitulo);
                estiloTitulo.setAlignment(CellStyle.ALIGN_CENTER);
                estiloTitulo.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloTitulo.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                estiloTitulo.setFillPattern(CellStyle.SOLID_FOREGROUND);

                Row filaTitulo = hoja.createRow(0);
                Cell celdaTitulo = filaTitulo.createCell(0);
                celdaTitulo.setCellValue("Reporte de Tickets");
                celdaTitulo.setCellStyle(estiloTitulo);

                hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columnas - 1));

                // ===== FECHA =====
                String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                Row filaFecha = hoja.createRow(1);
                Cell celdaFecha = filaFecha.createCell(0);
                celdaFecha.setCellValue("Reporte generado el: " + fechaHora);

                hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, columnas - 1));

                // ===== GRAFICA =====
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ChartUtils.writeChartAsPNG(baos, chart, 800, 400);

                int pictureIdx = libro.addPicture(baos.toByteArray(), Workbook.PICTURE_TYPE_PNG);

                CreationHelper helper = libro.getCreationHelper();
                Drawing drawing = hoja.createDrawingPatriarch();

                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(3);

                Picture pict = drawing.createPicture(anchor, pictureIdx);
                pict.resize();

                // ===== ENCABEZADO =====
                org.apache.poi.ss.usermodel.Font fuenteEncabezado = libro.createFont();
                fuenteEncabezado.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);

                CellStyle estiloEncabezado = libro.createCellStyle();
                estiloEncabezado.setFont(fuenteEncabezado);
                estiloEncabezado.setAlignment(CellStyle.ALIGN_CENTER);
                estiloEncabezado.setBorderTop(CellStyle.BORDER_THIN);
                estiloEncabezado.setBorderBottom(CellStyle.BORDER_THIN);
                estiloEncabezado.setBorderLeft(CellStyle.BORDER_THIN);
                estiloEncabezado.setBorderRight(CellStyle.BORDER_THIN);
                estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                estiloEncabezado.setFillPattern(CellStyle.SOLID_FOREGROUND);

                // ===== CELDAS =====
                CellStyle estiloCelda = libro.createCellStyle();
                estiloCelda.setAlignment(CellStyle.ALIGN_CENTER);
                estiloCelda.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloCelda.setBorderTop(CellStyle.BORDER_THIN);
                estiloCelda.setBorderBottom(CellStyle.BORDER_THIN);
                estiloCelda.setBorderLeft(CellStyle.BORDER_THIN);
                estiloCelda.setBorderRight(CellStyle.BORDER_THIN);
                estiloCelda.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                estiloCelda.setFillPattern(CellStyle.SOLID_FOREGROUND);

                // ===== TABLA =====
                int filaInicio = 25;

                Row filaEncabezado = hoja.createRow(filaInicio);

                for (int c = 0; c < t.getColumnCount(); c++) { // 🔥 desde 0
                    Cell celda = filaEncabezado.createCell(c);
                    celda.setCellValue(t.getColumnName(c));
                    celda.setCellStyle(estiloEncabezado);
                }

                for (int f = 0; f < t.getRowCount(); f++) {

                    Row fila = hoja.createRow(f + filaInicio + 1);

                    for (int c = 0; c < t.getColumnCount(); c++) {

                        Object valor = t.getValueAt(f, c);

                        Cell celda = fila.createCell(c);

                        if (valor instanceof Number) {
                            celda.setCellValue(Double.parseDouble(valor.toString()));
                        } else {
                            celda.setCellValue(valor != null ? valor.toString() : "");
                        }

                        celda.setCellStyle(estiloCelda);
                    }
                }

                for (int c = 0; c < columnas; c++) {
                    hoja.autoSizeColumn(c);
                }

                libro.write(archivo);

                Desktop.getDesktop().open(archivoXLSX);
            }
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
            jTable1.getColumnModel().getColumn(11).setCellRenderer(new Colorear_Status());
        }
    }

    private void pedirEmpleadoYEnviar(ActionEvent e) {
        Object[] opciones = {"Reporte de Entrada", "Reporte de Salida"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "¿Qué tipo de mensaje deseas enviar?",
                "Seleccionar Reporte",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (seleccion == -1) {
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JTextField campoNombre = new JTextField(15);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Nombre del empleado:"));
        inputPanel.add(campoNombre);

        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        JList<String> listaSugerencias = new JList<>(modeloLista);
        JScrollPane scrollSugerencias = new JScrollPane(listaSugerencias);
        scrollSugerencias.setPreferredSize(new Dimension(300, 80));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollSugerencias, BorderLayout.CENTER);

        Map<String, String> mapaNombreTelefono = new HashMap<>();

        campoNombre.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                actualizar();
            }

            public void removeUpdate(DocumentEvent e) {
                actualizar();
            }

            public void changedUpdate(DocumentEvent e) {
                actualizar();
            }

            private void actualizar() {
                modeloLista.clear();
                mapaNombreTelefono.clear();
                String texto = campoNombre.getText().trim();
                if (texto.length() >= 2) {
                    try (Connection cn = new conexion().conectar()) {
                        PreparedStatement pst = cn.prepareStatement("SELECT Nombre, Telefono FROM empleados WHERE Nombre LIKE ?");
                        pst.setString(1, "%" + texto + "%");
                        ResultSet rs = pst.executeQuery();
                        while (rs.next()) {
                            String nombre = rs.getString("Nombre");
                            String tel = rs.getString("Telefono");
                            modeloLista.addElement(nombre + " - " + tel);
                            mapaNombreTelefono.put(nombre, tel);
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
                            campoNombre.setText(partes[0]);
                        }
                        modeloLista.clear();
                    }
                }
            }
        });

        int resultado = JOptionPane.showConfirmDialog(null, panel, "Ingrese el nombre del empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String nombreEmpleado = campoNombre.getText().trim();
        String telefono = mapaNombreTelefono.get(nombreEmpleado);
        if (telefono == null || telefono.isEmpty()) {
            try (Connection cn = new conexion().conectar()) {
                PreparedStatement pst = cn.prepareStatement("SELECT Telefono FROM empleados WHERE Nombre = ?");
                pst.setString(1, nombreEmpleado);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    telefono = rs.getString("Telefono");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró ese empleado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }

        String numeroCompleto = "52" + telefono;
        String mensaje;
        List<String> listaTickets = new ArrayList<>();

        try (Connection cn = new conexion().conectar()) {
            PreparedStatement pst = cn.prepareStatement("SELECT Id_Monitoreo FROM tomar WHERE Nombre_Empleado = ?");
            pst.setString(1, nombreEmpleado);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                listaTickets.add(rs.getString("Id_Monitoreo"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (listaTickets.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontró ningún ticket asignado a este empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (seleccion == 0) {
            if (listaTickets.size() == 1) {
                mensaje = "El ticket " + listaTickets.get(0) + " se te asignó, para que lo atiendas conforme a los plazos establecidos. Te mando el reporte.";
            } else {
                mensaje = "Los tickets " + String.join(", ", listaTickets) + " se te asignaron, para que los atiendas conforme a los plazos establecidos. Te mando el reporte.";
            }
        } else {
            if (listaTickets.size() == 1) {
                mensaje = "Felicidades " + nombreEmpleado + ", el ticket " + listaTickets.get(0) + " se ha resuelto satisfactoriamente. ¡Gracias por tu paciencia!";
            } else {
                mensaje = "Felicidades " + nombreEmpleado + ", los tickets " + String.join(", ", listaTickets) + " se han resuelto satisfactoriamente. ¡Gracias por tu paciencia!";
            }
        }

        String url = "https://wa.me/" + numeroCompleto + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
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
                PreparedStatement pst = cn.prepareStatement("DELETE FROM tomar WHERE Id_Monitoreo=?");
                pst.setString(1, idSolucion);
                pst.executeUpdate();

                // Eliminar del modelo asociado a jTable2
                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
                modelo.removeRow(filaSeleccionada);
            }
        } catch (SQLException ex) {
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        btnMenuSupervisor = new javax.swing.JButton();
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
        jLabel2.setText("MONITOREO DE TICKETS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id_Monitoreo", "Nombre_Cliente", "Nombre_Empleado", "Tipo_Poliza", "Nombre_Producto", "Fecha_Asignacion", "Hora_Asignacion", "Fecha_Termino", "Horas_Retraso", "Minutos_Retraso", "Dias_Retraso", "Status"
            }
        ));
        jTable1.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 1230, 260));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 180, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Buscador");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 80, 90, -1));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Generar Reporte");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 420, 230, 40));

        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 190, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Fecha de Termino");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Editar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 420, 180, 40));

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setText("Enviar reporte");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 420, 260, 40));

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setText("Eliminar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 420, 210, 40));

        btnMenuSupervisor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSupervisor.setText("Menu del Supervisor");
        btnMenuSupervisor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSupervisorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuSupervisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 70, 260, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 70, 240, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 1270, 480));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String textoFiltro = jTextField1.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT Id_Monitoreo, Nombre_Cliente, Nombre_Empleado, Tipo_Poliza, Nombre_Producto, Fecha_Asignacion, Hora_Asignacion, Fecha_Termino, Dias_Retraso, Horas_Retraso, Minutos_Retraso, Status FROM tomar");

        boolean tieneTexto = !textoFiltro.isEmpty();
        if (tieneTexto) {
            sql.append(" WHERE Nombre_Cliente LIKE ? OR Nombre_Empleado LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"Id_Monitoreo", "Nombre_Cliente", "Nombre_Empleado", "Tipo_Poliza", "Nombre_Producto", "Fecha_Asignacion", "Hora_Asignacion", "Fecha_Termino", "Dias_Retraso", "Horas_Retraso", "Minutos_Retraso", "Status"}, 0
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
                        String nombreColumna = rsMd.getColumnLabel(i + 1);

                        if (nombreColumna.equalsIgnoreCase("Hora_Asignacion")) {
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
            ex.printStackTrace(); // útil para depuración
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        generarGrafica(null);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        insertarMonitoreo();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        cargarTabla();
        centrarColumnas();
    }//GEN-LAST:event_formWindowOpened

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        pedirEmpleadoYEnviar(null);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton6ActionPerformed

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
            java.util.logging.Logger.getLogger(MonitoreoTickets.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MonitoreoTickets.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitoreoTickets.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MonitoreoTickets.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        } catch (Exception ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MonitoreoTickets().setVisible(true);
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
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton btnMenuSupervisor;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
