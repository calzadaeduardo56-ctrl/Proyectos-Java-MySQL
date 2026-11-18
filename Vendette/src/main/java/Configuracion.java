
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class Configuracion extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    private ImageIcon imagen;
    private Icon icocno;

    public Configuracion() {
        initComponents();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/configuracion-de-sincronizacion.png"));
        setIconImage(logoIcon.getImage());

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/badge.png", 30, 30));

        this.setLocationRelativeTo(null);

        pintarImagen(jLabel1, "Imagen3.png");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        SwingUtilities.invokeLater(() -> {
            cargarLogoGuardado();
        });

        SwingUtilities.invokeLater(() -> {
            cargarConfiguracionTexto();
        });

        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                guardarConfiguracionTexto();
            }

            public void removeUpdate(DocumentEvent e) {
                guardarConfiguracionTexto();
            }

            public void changedUpdate(DocumentEvent e) {
                guardarConfiguracionTexto();
            }
        };

        jTextField1.getDocument().addDocumentListener(listener);
        jTextArea1.getDocument().addDocumentListener(listener);
        jTextField2.getDocument().addDocumentListener(listener);
        jTextField3.getDocument().addDocumentListener(listener);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        Configuracion.this,
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
    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    public static String
            getAppBasePath() {
        try {
            return new File(Configuracion.class
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

    public void seleccionarYGuardarImagen() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona el nuevo logo");

        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Imágenes PNG y JPG", "png", "jpg", "jpeg")
        );

        int resultado = fileChooser.showOpenDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File imagenSeleccionada = fileChooser.getSelectedFile();

        try {
            // 1. Obtener una carpeta donde el usuario tenga permisos.
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

            // 2. Crear las carpetas necesarias.
            File imageDir = new File(carpetaUsuario, "imagenes");
            File configDir = new File(carpetaUsuario, "config");

            if (!imageDir.exists() && !imageDir.mkdirs()) {
                throw new IOException(
                        "No se pudo crear la carpeta de imágenes:\n"
                        + imageDir.getAbsolutePath()
                );
            }

            if (!configDir.exists() && !configDir.mkdirs()) {
                throw new IOException(
                        "No se pudo crear la carpeta de configuración:\n"
                        + configDir.getAbsolutePath()
                );
            }

            // 3. Leer la imagen seleccionada.
            BufferedImage imagen = ImageIO.read(imagenSeleccionada);

            if (imagen == null) {
                throw new IOException(
                        "El archivo seleccionado no es una imagen válida."
                );
            }

            // 4. Guardar siempre el logo en formato PNG.
            File logoDestino = new File(imageDir, "logo.png");

            boolean guardado = ImageIO.write(imagen, "png", logoDestino);

            if (!guardado || !logoDestino.exists()
                    || logoDestino.length() == 0) {
                throw new IOException(
                        "No se pudo escribir el archivo del logo."
                );
            }

            // 5. Guardar la ruta del logo en las propiedades.
            Properties props = new Properties();
            props.setProperty("logo_path", logoDestino.getAbsolutePath());

            File propertiesFile = new File(
                    configDir, "logo.properties"
            );

            try (OutputStream out = new FileOutputStream(propertiesFile)) {
                props.store(out, "Configuración del logo");
            }

            // 6. Mostrar el logo inmediatamente.
            mostrarLogo(logoDestino.getAbsolutePath());

        } catch (IOException | SecurityException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar el logo.\n\n"
                    + "Detalle: " + ex.getMessage(),
                    "Error al guardar el logo",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    public void cargarLogoGuardado() {

        try {
            // 1. Obtener la carpeta de configuración del usuario.
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

            // 2. Buscar el archivo de configuración.
            File propertiesFile = new File(
                    carpetaUsuario,
                    "config/logo.properties"
            );

            if (!propertiesFile.exists()) {
                System.out.println(
                        "No existe la configuración del logo: "
                        + propertiesFile.getAbsolutePath()
                );
                return;
            }

            // 3. Leer las propiedades.
            Properties props = new Properties();

            try (InputStream in = new FileInputStream(propertiesFile)) {
                props.load(in);
            }

            String rutaLogo = props.getProperty("logo_path");

            if (rutaLogo == null || rutaLogo.trim().isEmpty()) {
                System.out.println(
                        "No se encontró la propiedad logo_path."
                );
                return;
            }

            // 4. Obtener el archivo del logo.
            File logoFile = new File(rutaLogo);

            // Compatibilidad con rutas relativas de configuraciones anteriores.
            if (!logoFile.isAbsolute()) {
                logoFile = new File(carpetaUsuario, rutaLogo);
            }

            // 5. Verificar y mostrar el logo.
            if (logoFile.exists() && logoFile.isFile()) {

                mostrarLogo(logoFile.getAbsolutePath());

            } else {
                System.out.println(
                        "No se encontró el archivo del logo: "
                        + logoFile.getAbsolutePath()
                );
            }

        } catch (IOException | SecurityException e) {
            System.out.println(
                    "No se pudo cargar el logo: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    private void mostrarLogo(String rutaCompleta) {

        File archivo = new File(rutaCompleta);

        if (!archivo.exists() || !archivo.isFile()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró el archivo del logo:\n"
                    + rutaCompleta,
                    "Error al mostrar logo",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            BufferedImage imagenOriginal = ImageIO.read(archivo);

            if (imagenOriginal == null) {
                throw new IOException(
                        "No se pudo interpretar la imagen."
                );
            }

            int ancho = jLabel3.getWidth();
            int alto = jLabel3.getHeight();

            if (ancho <= 0) {
                ancho = 200;
            }

            if (alto <= 0) {
                alto = 150;
            }

            Image imagenEscalada = imagenOriginal.getScaledInstance(
                    ancho,
                    alto,
                    Image.SCALE_SMOOTH
            );

            jLabel3.setIcon(new ImageIcon(imagenEscalada));
            jLabel3.setText("");

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cargar el logo.\n"
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    public void guardarConfiguracionTexto() {

        try {
            String basePath = getAppBasePath();

            File configDir = new File(basePath, "config");
            configDir.mkdirs();

            File propertiesFile = new File(configDir, "texto.properties");

            Properties props = new Properties();
            props.setProperty("direccion", jTextField1.getText());
            props.setProperty("descripcion", jTextArea1.getText());
            props.setProperty("empresa", jTextField2.getText());
            props.setProperty("telefono", jTextField3.getText());

            try (OutputStream out = new FileOutputStream(propertiesFile)) {
                props.store(out, "Configuración de textos");
            }

        } catch (Exception e) {
            System.out.println("Error al guardar texto");
        }
    }

    public void cargarConfiguracionTexto() {

        try {
            String basePath = getAppBasePath();
            File propertiesFile = new File(basePath, "config/texto.properties");

            if (!propertiesFile.exists()) {
                return;
            }

            Properties props = new Properties();

            try (InputStream in = new FileInputStream(propertiesFile)) {
                props.load(in);
            }

            jTextField1.setText(props.getProperty("direccion", ""));
            jTextArea1.setText(props.getProperty("descripcion", ""));
            jTextField2.setText(props.getProperty("empresa", ""));
            jTextField3.setText(props.getProperty("telefono", ""));

        } catch (Exception e) {
            System.out.println("Error al cargar texto");
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
        btnMenuModulos = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField3 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("CONFIGURACION DEL TICKET");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, -1));
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 560, 210));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, 250, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Cambiar Logo");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 280, 260, 40));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Empresa");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, -1, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 350, 310, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Direccion");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 350, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Telefono");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Descripcion");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 420, 80, 20));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 400, 320, 100));

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 420, 260, 30));

        jTextField2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 340, 280, 30));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 520));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuModulosActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuModulosActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        seleccionarYGuardarImagen();
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new Configuracion().setVisible(true);
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
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
