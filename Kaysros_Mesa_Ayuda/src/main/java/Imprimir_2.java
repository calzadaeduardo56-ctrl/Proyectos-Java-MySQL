
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author HP
 */
public class Imprimir_2 extends javax.swing.JPanel {

    /**
     * Creates new form Imprimir_2
     */
    private boolean modoCapturaManual = true;
    private boolean formularioCargado = false;

    public Imprimir_2() {
        initComponents();

        agregarValidacionUsuario(jComboBox4, this::validarClienteDisponible);

        formularioCargado = true;

        RellenarCombos re = new RellenarCombos();
        re.RellenarComboBox("empleados", "Nombre", jComboBox3);

        re.RellenarComboBox("empleados", "Nombre", jComboBox4);

        re.RellenarTextField("solucion", "Descripcion_Solucion", jTextField3);

        cargarLogoGuardado();
    }

    public void inicializarLogicaDeCarga() {
        cargarTickets();

        jComboBox1.addActionListener(e -> {
            String Id_ticket = (String) jComboBox1.getSelectedItem();
            if (Id_ticket != null && !Id_ticket.trim().isEmpty()) {
                cargarDatosDeTicket(Id_ticket);
            }
        });
    }

    public void inicializarLogicaDeCarga1() {
        cargarTickets1();

        jComboBox5.addActionListener(e -> {
            String Producto = (String) jComboBox5.getSelectedItem();
            if (Producto != null && !Producto.trim().isEmpty()) {
                cargarDatosDeTicket1(Producto);
            }
        });
    }

    public void cargarTickets() {
        try (Connection cn = new conexion().conectar(); PreparedStatement stmt = cn.prepareStatement("SELECT Id_Ticket FROM alta_ticket"); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                jComboBox1.addItem(rs.getString("Id_Ticket"));
            }

        } catch (SQLException e) {
        }
    }

    public void cargarTickets1() {
        try (Connection cn = new conexion().conectar(); PreparedStatement stmt = cn.prepareStatement("SELECT Producto FROM productos"); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                jComboBox5.addItem(rs.getString("Producto"));
            }

        } catch (SQLException e) {
        }
    }

    public void cargarDatosDeTicket(String Id_ticket) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Connection cn = new conexion().conectar();

            String sql = "SELECT Nombre_Cliente, Tipo_Poliza, Nombre_Problematica, Nombre_Producto FROM alta_ticket WHERE Id_Ticket = ?";
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, Id_ticket);
            rs = stmt.executeQuery();

            if (rs.next()) {
                jTextField7.setText(rs.getString("Nombre_Cliente"));
                jTextField2.setText(rs.getString("Tipo_Poliza"));
                jTextField12.setText(rs.getString("Nombre_Problematica"));
            }
        } catch (SQLException ex) {
        }
    }

    public void cargarDatosDeTicket1(String Producto) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Connection cn = new conexion().conectar();

            String sql = "SELECT  Marca, Proveedor FROM productos WHERE Producto = ?";
            stmt = cn.prepareStatement(sql);
            stmt.setString(1, Producto);
            rs = stmt.executeQuery();

            if (rs.next()) {
                jTextField9.setText(rs.getString("Marca"));
                jTextField10.setText(rs.getString("Proveedor"));
            }
        } catch (SQLException ex) {
        }
    }

    public void Tickets() {

        jComboBox1.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jComboBox2.showPopup();
            }
        }
        );

        jComboBox2.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jTextField6.requestFocus();
            }
        }
        );

        jTextField6.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jComboBox3.showPopup();
            }
        }
        );

        jComboBox3.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jComboBox5.showPopup();
            }
        }
        );

        jComboBox5.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jDateChooser1.getCalendarButton().doClick();
            }
        }
        );

        jDateChooser1.getDateEditor()
                .addPropertyChangeListener("date", e -> {
                    if (modoCapturaManual && jDateChooser1.getDate() != null) {
                        jComboBox4.showPopup();
                    }
                }
                );
        jComboBox4.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jTextField11.requestFocus();
            }
        }
        );

        jTextField11.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jTextField4.requestFocus();
            }
        }
        );

        jTextField4.addActionListener(e
                -> {
            if (modoCapturaManual) {
                jTextField5.requestFocus();
            }
        }
        );

        actualizarFechaHora();

        this.revalidate();

        this.repaint();
        int preferredWidth = this.getPreferredSize().width;
        int preferredHeight = this.getPreferredSize().height;

        this.setPreferredSize(
                new Dimension(preferredWidth, preferredHeight + 300)); // Añadir espacio extra si es necesario

    }

    private void actualizarFechaHora() {
        // Formato de fecha y hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String fechaHoraActual = sdf.format(new Date());
        jTextField1.setText(fechaHoraActual);
    }

    public static String
            getAppBasePath() {
        try {
            return new File(Imprimir_2.class
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

    public void seleccionarYGuardarImagen(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona el nuevo logo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "png", "jpg", "jpeg"));

        int resultado = fileChooser.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File imagenSeleccionada = fileChooser.getSelectedFile();

            try {
                String basePath = getAppBasePath(); // carpeta del .jar

                File imageDir = new File(basePath, "imagenes");
                File configDir = new File(basePath, "config");
                imageDir.mkdirs();
                configDir.mkdirs();

                File logoDestino = new File(imageDir, "logo.png");
                Files.copy(imagenSeleccionada.toPath(), logoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Guardar ruta en properties
                Properties props = new Properties();
                props.setProperty("logo_path", "imagenes/logo.png");

                File propertiesFile = new File(configDir, "logo.properties");
                try (OutputStream out = new FileOutputStream(propertiesFile)) {
                    props.store(out, "Configuración del logo");
                }

                mostrarLogo(logoDestino.getAbsolutePath());

            } catch (IOException ex) {
            }
        }
    }

    public void cargarLogoGuardado() {
        String basePath = getAppBasePath();
        File propertiesFile = new File(basePath, "config/logo.properties");

        if (propertiesFile.exists()) {
            try (InputStream in = new FileInputStream(propertiesFile)) {
                Properties props = new Properties();
                props.load(in);
                String rutaRelativa = props.getProperty("logo_path");

                if (rutaRelativa != null && !rutaRelativa.isEmpty()) {
                    File logoFile = new File(basePath, rutaRelativa);

                    // Esperar a que jLabel16 tenga un tamaño válido para redimensionar correctamente
                    jLabel16.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            mostrarLogo(logoFile.getAbsolutePath());
                            jLabel16.removeComponentListener(this); // Ya no se necesita después
                        }
                    });
                }

            } catch (IOException e) {
            }
        } else {
        }

    }

    private void mostrarLogo(String rutaCompleta) {
        try {
            // Carga la imagen original
            ImageIcon icon = new ImageIcon(rutaCompleta);

            // Obtiene el ancho y alto actual del JLabel
            int ancho = jLabel16.getWidth();
            int alto = jLabel16.getHeight();

            // Verifica que el JLabel tenga dimensiones válidas (mayores a 0)
            if (ancho > 0 && alto > 0) {
                java.awt.Image imagenRedimensionada = icon.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH);
                jLabel16.setIcon(new ImageIcon(imagenRedimensionada));
                jLabel16.setText(""); // Limpia texto si lo hubiera
            } else {
                // Si el JLabel aún no tiene tamaño, carga la imagen original sin escalar
                jLabel16.setIcon(icon);
                jLabel16.setText("");
            }

        } catch (Exception e) {
            jLabel16.setText("Error al cargar imagen.");
            jLabel16.setIcon(null);
        }
    }

    public JPanel obtenerPanelAImprimir() {
        // Retorna el panel que contiene los componentes que deseas imprimir
        return jPanel1; // reemplaza con tu panel real
    }

    public void Imprimir(Component parentComponent) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte como PDF");
        fileChooser.setSelectedFile(new File("reporte.pdf"));

        int userSelection = fileChooser.showSaveDialog(parentComponent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                JPanel jPanel1 = obtenerPanelAImprimir(); // Asegúrate que este es tu panel exacto

                int panelWidth = jPanel1.getWidth();
                int panelHeight = jPanel1.getHeight();

                // Crear imagen exacta del panel
                BufferedImage image = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                jPanel1.paint(g2);
                g2.dispose();

                // Guardar imagen temporal
                File tempImage = File.createTempFile("panel_temp", ".png");
                ImageIO.write(image, "png", tempImage);

                // Crear documento del mismo tamaño que el panel
                Rectangle pageSize = new Rectangle(panelWidth, panelHeight);
                Document document = new Document(pageSize, 0, 0, 0, 0); // márgenes: 0
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Insertar la imagen sin escalar y sin alineación
                com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(tempImage.getAbsolutePath());
                img.setAbsolutePosition(0, 0); // posición en esquina inferior izquierda
                document.add(img);
                document.close();

                // Abrir el PDF
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToSave);
                }

            } catch (DocumentException | IOException ex) {
            }
        }
    }

    private void validarClienteDisponible() {

        String proveedorSeleccionado = jComboBox4.getSelectedItem().toString();

        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM empleados WHERE Nombre = ?";

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
                            "Empleado no disponible",
                            JOptionPane.WARNING_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> {
                        jComboBox4.requestFocusInWindow();
                        jComboBox4.showPopup();
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("REPORTE DE VISITA DE SOPORTE TECNICO ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 15, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Num. Ticket");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 90, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("No. De Reporte ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, -1, -1));

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        jPanel1.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, 93, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Fecha y Hora");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 140, 143, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Personas Citadas");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Motivo de la Visita");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Asuntos Tratados");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 500, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Conclusiones");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 600, -1, -1));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 580, 570, 62));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Responsables de la Visita");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 850, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Nombre y Firma");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 900, -1, -1));

        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 940, 310, 102));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Nombre Y Firma (Cliente)");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 900, -1, -1));

        jTextField5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 940, 340, 110));
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 250, 80));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Cita hecha por");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, -1, -1));

        jTextField6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 230, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Nombre de la Marca");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 350, -1, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Nombre del Producto");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, -1, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("Seguimiento para la Proxima Visita");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 670, 340, 30));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("Fecha");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 730, -1, -1));

        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 730, 240, -1));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Responsable");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 790, -1, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("Punto a Seguir");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 790, 130, 20));

        jTextField11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField11, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 790, 250, -1));

        jComboBox4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 790, 240, -1));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setText("Nombre de la Empresa");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 260, -1, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("Tipo de Poliza");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 200, -1, -1));

        jComboBox3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 260, 230, -1));

        jComboBox5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jComboBox5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 350, 180, -1));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setText("Nombre del Proveedor");
        jPanel1.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, -1, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 140, 250, 30));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 200, 210, 30));

        jTextField7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 260, 210, 30));

        jTextField9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 350, 250, 30));

        jTextField10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField10, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 410, 230, 30));

        jTextField12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jTextField12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 480, 570, 50));

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables

}
