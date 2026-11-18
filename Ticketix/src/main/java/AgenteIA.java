
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class AgenteIA extends javax.swing.JFrame {

    private GroqClient ai;
    private ImageIcon imagen;
    private Icon icocno;
    private JPopupMenu popupMenu;
    private String puesto;

    public AgenteIA() {
        initComponents();

        popupMenu = new JPopupMenu();
        popupMenu.setFocusable(false); // <-- importante, no roba foco

        configurarEnterGuardar();

        // Listener para detectar cambios en el texto
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mostrarSugerencias();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mostrarSugerencias();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mostrarSugerencias();
            }
        });

        ai = new GroqClient(); // Inicializa tu cliente Groq

        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        AgenteIA.this,
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

        pintarImagen(jLabel1, "Imagen3.png");

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/inteligencia-artificial.png"));
        setIconImage(logoIcon.getImage());

        SwingUtilities.invokeLater(() -> {
            ImageIcon wallpaper = new ImageIcon(getClass().getResource("/imagen/imagen4.png"));
            Icon icono = new ImageIcon(wallpaper.getImage().getScaledInstance(
                    jLabel3.getWidth(),
                    jLabel3.getHeight(),
                    Image.SCALE_DEFAULT));
            jLabel3.setIcon(icono);
            repaint();

            jButton3.setIcon(getIconUrl("/imagen/manual.png", 30, 30));

            jButton4.setIcon(getIconUrl("/imagen/microfono-grabador.png", 30, 30));

            btnMenuSoporte.setIcon(getIconUrl("/imagen/menu-abierto.png", 30, 30));

            btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));
        });

    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuSoporte);
    }

    public File grabarAudioConVentana() {
        final File[] archivoFinal = {null};

        // Ventana de grabación
        JDialog ventana = new JDialog((Frame) null, "Grabación", true);
        ventana.setLayout(new BorderLayout());
        JLabel lbl = new JLabel("🎤 Hola, te escucho... ¿Qué necesitas?", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        ventana.add(lbl, BorderLayout.CENTER);
        ventana.pack(); // Ajusta la ventana al tamaño del contenido
        ventana.setLocationRelativeTo(null);

        // Hilo de grabación
        new Thread(() -> {
            try {
                File archivoTemp = File.createTempFile("grabacion_", ".wav");
                archivoTemp.deleteOnExit();

                AudioFormat formato = new AudioFormat(
                        16000.0f, 16, 1, true, false
                );

                DataLine.Info info = new DataLine.Info(TargetDataLine.class, formato);
                if (!AudioSystem.isLineSupported(info)) {
                    throw new LineUnavailableException("Micrófono no soportado");
                }

                TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);
                mic.open(formato);
                mic.start();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] datos = new byte[4096];

                long silencioDetectado = 0;
                long maxSilencio = 2000; // 2 segundos de silencio
                long ultimoSonido = System.currentTimeMillis();

                while (true) {
                    int bytesRead = mic.read(datos, 0, datos.length);
                    if (bytesRead > 0) {
                        buffer.write(datos, 0, bytesRead);

                        // Calcular RMS (nivel de volumen)
                        double rms = calcularRMS(datos, bytesRead);
                        if (rms > 50) { // umbral de voz
                            ultimoSonido = System.currentTimeMillis();
                        }

                        silencioDetectado = System.currentTimeMillis() - ultimoSonido;
                        if (silencioDetectado > maxSilencio) {
                            break; // terminó la voz
                        }
                    }
                }

                mic.stop();
                mic.close();

                // Guardar archivo WAV temporal
                try (AudioInputStream ais = new AudioInputStream(
                        new ByteArrayInputStream(buffer.toByteArray()), formato,
                        buffer.size() / formato.getFrameSize())) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, archivoTemp);
                }

                archivoFinal[0] = archivoTemp;

                // Cerrar ventana
                SwingUtilities.invokeLater(ventana::dispose);

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(ventana::dispose);
            }
        }).start();

        // Mostrar ventana (bloquea hasta dispose)
        ventana.setVisible(true);

        return archivoFinal[0]; // Devuelve el archivo WAV
    }

    // Detector de volumen (Root Mean Square)
    private double calcularRMS(byte[] audioData, int bytesRead) {
        long sum = 0;
        for (int i = 0; i < bytesRead; i++) {
            sum += audioData[i] * audioData[i];
        }
        return Math.sqrt(sum / (double) bytesRead);
    }

    private String transcribirConGroq(File audioFile) {
        String apiKey = "TU_GROQ_API_KEY_AQUI"; // ⚠️ pon aquí tu clave
        String url = "https://api.groq.com/openai/v1/audio/transcriptions";

        OkHttpClient client = new OkHttpClient();

        try {
            MediaType mediaType = MediaType.parse("audio/wav");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", audioFile.getName(),
                            RequestBody.create(audioFile, mediaType))
                    .addFormDataPart("model", "whisper-large-v3")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Error " + response);
                }

                String respuesta = response.body().string();
                JSONObject json = new JSONObject(respuesta);
                return json.getString("text");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void pedirEmpleadoYEnviar(ActionEvent e, String problematica) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JTextField campoEmpleado = new JTextField(15);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Nombre del empleado:"));
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
        String mensaje = saludo + " " + nombreEmpleado + ", esta es la guia tecnica de " + problematica + ".";

        String url = "https://wa.me/" + numeroCompleto + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarDialogoSolucionMarkdown(String problema) {
        // Crear JDialog modal
        JDialog dlg = new JDialog(this, "Solución propuesta", true);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        // Área de visualización (HTML)
        JEditorPane editor = new JEditorPane();
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setBackground(Color.WHITE);
        editor.setText("<html><body><i>Generando solución...</i></body></html>");
        dlg.add(new JScrollPane(editor), BorderLayout.CENTER);

        // Botón para exportar a PDF
        JButton btnPdf = new JButton("Exportar");
        btnPdf.setIcon(getIconUrl("/imagen/expediente.png", 25, 25));
        btnPdf.setEnabled(false);
        btnPdf.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnPdf.setIconTextGap(10);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(btnPdf);
        dlg.add(bottomPanel, BorderLayout.SOUTH);

        dlg.setSize(800, 600);
        dlg.setLocationRelativeTo(this);

        // Markdown parser
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        GroqClient ai = new GroqClient();
        String system = """
        Eres un experto en soporte técnico multi-plataforma. 
        Escribes guías paso a paso, concretas y verificables, pensadas para técnicos de campo.
        Respondes SIEMPRE en español y en formato Markdown.
    """;
        String user = ai.buildSolutionPrompt(problema);

        StringBuilder buffer = new StringBuilder();
        final StringBuilder lastHtml = new StringBuilder();
        long[] lastUpdate = {0}; // Para controlar frecuencia de actualización

        // Streaming del modelo Groq
        ai.chatStream(
                system,
                user,
                0.2,
                2500,
                delta -> {
                    buffer.append(delta);

                    // Evitar repintar en exceso (máx ~6 FPS)
                    long now = System.currentTimeMillis();
                    if (now - lastUpdate[0] < 160) {
                        return;
                    }
                    lastUpdate[0] = now;

                    SwingUtilities.invokeLater(() -> {
                        // 👇 Mostrar texto crudo mientras se genera
                        String partial = buffer.toString()
                                .replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\n", "<br>");
                        editor.setText("<html><body style='font-family:Arial; font-size:12px; white-space: pre-wrap;'>"
                                + partial + "</body></html>");
                        editor.setCaretPosition(editor.getDocument().getLength());
                    });
                },
                () -> SwingUtilities.invokeLater(() -> {
                    // ✅ Render Markdown completo al final
                    String html = renderer.render(parser.parse(buffer.toString()));
                    lastHtml.setLength(0);
                    lastHtml.append(html);
                    editor.setText("<html><body style='font-family:Arial; font-size:12px;'>"
                            + html + "</body></html>");
                    editor.setCaretPosition(0);
                    btnPdf.setEnabled(true);
                }),
                err -> SwingUtilities.invokeLater(() -> {
                    editor.setText("<html><body><b>Error:</b> " + err.getMessage() + "</body></html>");
                })
        );

        // Acción del botón Exportar a PDF
        btnPdf.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar guía como PDF");
            fileChooser.setSelectedFile(new File("guia.pdf"));

            if (fileChooser.showSaveDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Document document = new Document();
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();

                    // Fecha y hora
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String fechaHora = LocalDateTime.now().format(formatter);
                    Paragraph fecha = new Paragraph("Generado el: " + fechaHora);
                    fecha.setAlignment(Element.ALIGN_RIGHT);
                    document.add(fecha);
                    document.add(new Paragraph("\n"));

                    // Convertir HTML -> PDF
                    InputStream is = new ByteArrayInputStream(lastHtml.toString().getBytes("UTF-8"));
                    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is, null, java.nio.charset.StandardCharsets.UTF_8);

                    document.close();

                    // Abrir PDF automáticamente
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    }

                } catch (DocumentException | IOException ex) {
                    JOptionPane.showMessageDialog(dlg, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dlg.setVisible(true);
    }

    private void mostrarSugerencias() {
        String texto = jTextField1.getText().trim();
        popupMenu.setVisible(false);
        popupMenu.removeAll();

        // Consultar coincidencias en la BD
        List<String> resultados = buscarProblematicas(texto);

        if (!resultados.isEmpty()) {
            for (String problema : resultados) {
                JMenuItem item = new JMenuItem(problema);
                item.setFocusable(false); // <-- no roba foco
                item.addActionListener(e -> {
                    jTextField1.setText(problema);
                    popupMenu.setVisible(false);
                });
                popupMenu.add(item);
            }

            // Mostrar debajo del JTextField
            popupMenu.show(jTextField1, 0, jTextField1.getHeight());
        }
    }

    private List<String> buscarProblematicas(String texto) {
        List<String> lista = new ArrayList<>();
        try {
            conexion mysql = new conexion(); // tu clase de conexión
            Connection cn = mysql.conectar();

            String sql;
            PreparedStatement ps;

            if (texto.isEmpty()) {
                // Si no hay texto, mostrar todas las problemáticas
                sql = "SELECT Problematicas FROM problematicas";
                ps = cn.prepareStatement(sql);
            } else {
                // Si hay texto, filtrar con LIKE
                sql = "SELECT Problematicas FROM problematicas WHERE Problematicas LIKE ? LIMIT 10";
                ps = cn.prepareStatement(sql);
                ps.setString(1, "%" + texto + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString("Problematicas"));
            }

            rs.close();
            ps.close();
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private int generarNuevoIdAsistencia() {
        int nuevoId = 1; // Por defecto empezamos en 1

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(idproblematicas) FROM problematicas"); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                nuevoId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
        }
        return nuevoId;
    }

    public void guardarRegistro() {
        String problematica = jTextField1.getText().trim();
        try (Connection cn = new conexion().conectar()) {

            // 1️⃣ Verificar si ya existe la problemática
            String checkSql = "SELECT COUNT(*) FROM problematicas WHERE Problematicas = ?";
            try (PreparedStatement checkStmt = cn.prepareStatement(checkSql)) {
                checkStmt.setString(1, problematica);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return; // no insertar
                }
            }

            // 2️⃣ Insertar nueva problemática
            int idTicket = generarNuevoIdAsistencia(); // tu método para generar ID

            String insertSql = "INSERT INTO problematicas(idproblematicas, Problematicas) VALUES (?, ?)";
            try (PreparedStatement pst = cn.prepareStatement(insertSql)) {
                pst.setInt(1, idTicket);
                pst.setString(2, problematica);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar la problemática.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    private void configurarEnterGuardar() {
        JRootPane rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "guardar");

        rootPane.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarRegistro();
                String problema = jTextField1.getText().trim();
                mostrarDialogoSolucionMarkdown(problema);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        btnMenuSoporte = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("BIENVENIDO A LA IA DE ");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 20, 220, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField1.setText("Pregunta lo que quieras");
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, 630, 60));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Enviar Guia Tecnica");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 210, 310, 40));
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 10, 340, 100));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Comando de Voz");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 210, 260, 40));

        btnMenuSoporte.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSoporte.setText("Menu de Soporte Tecnico");
        btnMenuSoporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSoporteActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuSoporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 270, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 240, 40));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-3, 0, 930, 280));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        pedirEmpleadoYEnviar(null, jTextField1.getText());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            // 1. Grabar 5 segundos de audio
            File audioFile = grabarAudioConVentana();

            // 2. Transcribir con Groq
            String texto = transcribirConGroq(audioFile);

            if (texto != null && !texto.isEmpty()) {
                // 3. Poner texto en el JTextField
                jTextField1.setText(texto);

                guardarRegistro();
                String problema = jTextField1.getText().trim();
                mostrarDialogoSolucionMarkdown(problema);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo transcribir el audio",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error en la grabación o transcripción",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnMenuSoporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSoporteActionPerformed
        MenuSoporte menu = new MenuSoporte();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuSoporteActionPerformed

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
            java.util.logging.Logger.getLogger(AgenteIA.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AgenteIA.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AgenteIA.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AgenteIA.class
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
                new AgenteIA().setVisible(true);
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
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton btnMenuSoporte;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
