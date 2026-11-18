
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
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
public class HistorialSurtido extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;
    private String puesto;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public HistorialSurtido() {
        initComponents();

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("productos", "CodigoBarras", jComboBox1);

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

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/historial-de-transacciones.png"));
        setIconImage(logoIcon.getImage());

        jButton3.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        jButton2.setIcon(getIconUrl("/imagen/sobresalir.png", 30, 30));

        jButton1.setIcon(getIconUrl("/imagen/expediente.png", 30, 30));

        btnMenuCapturista.setIcon(getIconUrl("/imagen/menu1.png", 30, 30));

        btnMenuContador.setIcon(getIconUrl("/imagen/menu2.png", 30, 30));

        pintarImagen(jLabel1, "Imagen3.png");

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        centrarColumnas();

        ocultarIdDetalle();

        try {

            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0); // Limpiar JTable

            Connection cn = new conexion().conectar();

            String sql = "SELECT IdSurtido, Proveedor, MetodoPago, FechaCompra, FolioCompra, Codigo, Cantidad, PrecioCompra, Stock, "
                    + "Descripcion, PrecioTotal, TipoPrecio FROM surtido";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String sqlInsert = "INSERT INTO historialsurtido "
                    + "(IdSurtido, Proveedor, MetodoPago, FechaCompra, FolioCompra, Codigo, Cantidad, "
                    + "PrecioCompra, Stock, Descripcion, PrecioTotal, TipoPrecio) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "Proveedor = VALUES(Proveedor), "
                    + "MetodoPago = VALUES(MetodoPago), "
                    + "FechaCompra = VALUES(FechaCompra), "
                    + "FolioCompra = VALUES(FolioCompra),"
                    + "Codigo = VALUES(Codigo), "
                    + "Cantidad = VALUES(Cantidad), "
                    + "PrecioCompra = VALUES(PrecioCompra), "
                    + "Stock = VALUES(Stock),"
                    + "Descripcion = VALUES(Descripcion), "
                    + "PrecioTotal = VALUES(PrecioTotal), "
                    + "TipoPrecio = VALUES(TipoPrecio)";

            PreparedStatement psInsert = cn.prepareStatement(sqlInsert);

            while (rs.next()) {

                int IdSurtido = rs.getInt("IdSurtido");
                String Proveedor = rs.getString("Proveedor");
                String MetodoPago = rs.getString("MetodoPago");
                java.sql.Date FechaCompra = rs.getDate("FechaCompra");
                int FolioCompra = rs.getInt("FolioCompra");
                int Codigo = rs.getInt("Codigo");
                int Cantidad = rs.getInt("Cantidad");
                double PrecioCompra = rs.getDouble("PrecioCompra");
                int Stock = rs.getInt("Stock");
                String Descripcion = rs.getString("Descripcion");
                double PrecioTotal = rs.getDouble("PrecioTotal");
                String TipoPrecio = rs.getString("TipoPrecio");

                psInsert.setInt(1, IdSurtido);
                psInsert.setString(2, Proveedor);
                psInsert.setString(3, MetodoPago);
                psInsert.setDate(4, FechaCompra);
                psInsert.setInt(5, FolioCompra);
                psInsert.setInt(6, Codigo);
                psInsert.setInt(7, Cantidad);
                psInsert.setDouble(8, PrecioCompra);
                psInsert.setInt(9, Stock);
                psInsert.setString(10, Descripcion);
                psInsert.setDouble(11, PrecioTotal);
                psInsert.setString(12, TipoPrecio);

                psInsert.executeUpdate();

                modelo.addRow(new Object[]{
                    IdSurtido,
                    Proveedor,
                    MetodoPago,
                    FechaCompra,
                    FolioCompra,
                    Codigo,
                    Cantidad,
                    String.format("%.2f", PrecioCompra),
                    Stock,
                    Descripcion,
                    String.format("%.2f", PrecioTotal),
                    TipoPrecio

                });
            }
            rs.close();
            ps.close();
            cn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e
            ) {
                int opcion = JOptionPane.showConfirmDialog(
                        HistorialSurtido.this,
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

    private void centrarColumnas() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centrado);
            jTable1.getColumnModel().getColumn(11).setCellRenderer(new TipoPrecio());
        }
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
            SELECT IdSurtido, Proveedor, MetodoPago, FechaCompra, FolioCompra, Codigo, Cantidad, 
                               PrecioCompra, Stock, Descripcion, PrecioTotal, TipoPrecio
            FROM historialsurtido
            WHERE FechaCompra BETWEEN ? AND ?
        """;

            PreparedStatement psV = cn.prepareStatement(sqlVentas);
            psV.setDate(1, new java.sql.Date(inicio.getTime()));
            psV.setDate(2, new java.sql.Date(fin.getTime()));
            ResultSet rs = psV.executeQuery();

            while (rs.next()) {
                String PrecioCompra = String.format("%.2f", rs.getDouble("PrecioCompra"));
                String PrecioTotal = String.format("%.2f", rs.getDouble("PrecioTotal"));

                modeloVentas.addRow(new Object[]{
                    rs.getInt("IdSurtido"),
                    rs.getString("Proveedor"),
                    rs.getString("MetodoPago"),
                    rs.getDate("FechaCompra"),
                    rs.getInt("FolioCompra"),
                    rs.getInt("Codigo"),
                    rs.getInt("Cantidad"),
                    PrecioCompra,
                    rs.getInt("Stock"),
                    rs.getString("Descripcion"),
                    PrecioTotal,
                    rs.getString("TipoPrecio")

                }
                );
            }
            centrarColumnas();
            ocultarIdDetalle();
            rs.close();
            psV.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void filtrarVentasPorMetodoPago(String textoFiltro) {

        String sql = "SELECT IdSurtido, Proveedor, MetodoPago, FechaCompra, FolioCompra, Codigo, Cantidad, "
                + "PrecioCompra, Stock, Descripcion, PrecioTotal, TipoPrecio "
                + "FROM historialsurtido "
                + "WHERE Codigo LIKE ?";

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"IdSurtido", "Proveedor", "MetodoPago", "FechaCompra", "FolioCompra", "Codigo",
                    "Cantidad", "PrecioCompra", "Stock", "Descripcion", "PrecioTotal", "TipoPrecio"}, 0
        );

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + textoFiltro + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String PrecioCompra = String.format("%.2f", rs.getDouble("PrecioCompra"));
                    String PrecioTotal = String.format("%.2f", rs.getDouble("PrecioTotal"));
                    modelo.addRow(new Object[]{
                        rs.getInt("IdSurtido"),
                        rs.getString("Proveedor"),
                        rs.getString("MetodoPago"),
                        rs.getDate("FechaCompra"),
                        rs.getInt("FolioCompra"),
                        rs.getInt("Codigo"),
                        rs.getInt("Cantidad"),
                        PrecioCompra,
                        rs.getInt("Stock"),
                        rs.getString("Descripcion"),
                        PrecioTotal,
                        rs.getString("TipoPrecio")
                    });
                }
            }

            jTable1.setModel(modelo);
            centrarColumnas();
            ocultarIdDetalle();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void exportarTablaPDF(JTable tabla) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo PDF (.pdf)", "pdf");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Guardar archivo PDF");
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().toString();
            if (!ruta.toLowerCase().endsWith(".pdf")) {
                ruta += ".pdf";
            }

            Document documento = new Document(PageSize.A4.rotate());

            try {
                PdfWriter.getInstance(documento, new FileOutputStream(ruta));
                documento.open();

                String fechaHora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        .format(new java.util.Date());

                PdfPTable tablaEncabezado = new PdfPTable(1);
                tablaEncabezado.setWidthPercentage(100);

                Font fuenteEncabezado = FontFactory.getFont("Tahoma", 12, Font.BOLD, BaseColor.DARK_GRAY);
                PdfPCell celdaFecha = new PdfPCell(new Phrase("Reporte generado el: " + fechaHora, fuenteEncabezado));
                celdaFecha.setBorder(Rectangle.NO_BORDER);
                celdaFecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaFecha.setPadding(8);
                tablaEncabezado.addCell(celdaFecha);

                documento.add(tablaEncabezado);
                documento.add(new Paragraph("\n"));

                Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
                Paragraph titulo = new Paragraph("REPORTE DEL SURTIDO DE MERCANCIAS\n\n", fuenteTitulo);
                titulo.setAlignment(Paragraph.ALIGN_CENTER);
                documento.add(titulo);

                // 🔴 IMPORTANTE: restamos 1 porque no queremos la columna 0
                int columnasVisibles = tabla.getColumnCount() - 1;

                PdfPTable pdfTabla = new PdfPTable(columnasVisibles);
                pdfTabla.setWidthPercentage(100);
                pdfTabla.setSpacingBefore(10f);
                pdfTabla.setSpacingAfter(10f);

                Font fuenteColumna = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                BaseColor colorEncabezado = new BaseColor(0, 102, 204);

                // 🔴 Empezamos desde columna 1 (ignoramos la 0)
                for (int col = 1; col < tabla.getColumnCount(); col++) {
                    PdfPCell celda = new PdfPCell(new Phrase(tabla.getColumnName(col), fuenteColumna));
                    celda.setBackgroundColor(colorEncabezado);
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda.setPadding(6);
                    pdfTabla.addCell(celda);
                }

                Font fuenteDatos = FontFactory.getFont(FontFactory.HELVETICA, 10);

                for (int fila = 0; fila < tabla.getRowCount(); fila++) {
                    BaseColor fondoFila = (fila % 2 == 0) ? BaseColor.LIGHT_GRAY : BaseColor.WHITE;

                    for (int col = 1; col < tabla.getColumnCount(); col++) {
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

                File archivo = new File(ruta);
                if (archivo.exists()) {
                    Desktop.getDesktop().open(archivo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, jButton3, btnMenuCapturista, btnMenuContador);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        btnMenuCapturista = new javax.swing.JButton();
        btnMenuContador = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "IdSurtido", "Proveedor", "MetodoPago", "FechaCompra", "FolioCompra", "Codigo", "Cantidad", "PrecioCompra", "Stock", "Descripcion", "PrecioTotal", "TipoPrecio"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 1160, 290));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("HISTORIAL DE SURTIDO DE MERCANCIAS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, -1, -1));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Menu de Modulos");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, 260, 40));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Exportar a Excel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 240, 40));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Exportar a PDF");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 230, 40));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 130, 180, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Fecha Inicial");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 130, 180, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Fecha Final");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, -1, -1));

        jDateChooser2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jDateChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 130, 190, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Filtro por Codigo de Barras");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 130, -1, -1));

        btnMenuCapturista.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuCapturista.setText("Menu del Supervisor");
        btnMenuCapturista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuCapturistaActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuCapturista, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 60, 290, 40));

        btnMenuContador.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuContador.setText("Menu del Cajero");
        btnMenuContador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuContadorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuContador, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 10, 290, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 480));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ExportarExcel obj;

        try {
            obj = new ExportarExcel();
            obj.exportarExcel(jTable1);
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        exportarTablaPDF(jTable1);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (evt.getStateChange() != java.awt.event.ItemEvent.SELECTED) {
            return;
        }

        String metodoPago = jComboBox1.getSelectedItem().toString().trim();

        // 🔴 Opción tipo "Seleccione..."
        if (metodoPago.equals("Seleccione") || metodoPago.isEmpty()) {

            ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
            return;
        }

        filtrarVentasPorMetodoPago(metodoPago);
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void btnMenuCapturistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuCapturistaActionPerformed
        MenuSupervisor menu = new MenuSupervisor();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuCapturistaActionPerformed

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
            java.util.logging.Logger.getLogger(HistorialSurtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistorialSurtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistorialSurtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistorialSurtido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HistorialSurtido().setVisible(true);
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
    private javax.swing.JButton btnMenuContador;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
