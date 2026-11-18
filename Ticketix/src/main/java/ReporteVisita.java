
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.*;
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
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
public class ReporteVisita extends javax.swing.JFrame {

    private String puesto;

    public ReporteVisita() {
        initComponents();

        imprimir_22.inicializarLogicaDeCarga();

        imprimir_22.inicializarLogicaDeCarga1();

        imprimir_22.Tickets();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/reporte-de-negocios.png"));
        setIconImage(logoIcon.getImage());

        jButton2.setIcon(getIconUrl("/imagen/expediente.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/badge.png", 30, 30));

        jButton5.setIcon(getIconUrl("/imagen/tarea-completada.png", 30, 30));

        jButton1.setIcon(getIconUrl("/imagen/gerente-de-proyecto.png", 30, 30));

        btnMenuSupervisor.setIcon(getIconUrl("/imagen/menu1.png", 30, 30));

        btnMenuSoporte.setIcon(getIconUrl("/imagen/menu-abierto.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        setLocationRelativeTo(null); // Centra la ventana

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        ReporteVisita.this,
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
        // Establecer fondo blanco
        getContentPane().setBackground(Color.WHITE);

    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuSupervisor, btnMenuSoporte);
    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    private void pedirEmpresaYEnviar(ActionEvent e) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JTextField campoEmpresa = new JTextField(15);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Nombre del cliente:"));
        inputPanel.add(campoEmpresa);

        // Lista de sugerencias
        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        JList<String> listaSugerencias = new JList<>(modeloLista);
        JScrollPane scrollSugerencias = new JScrollPane(listaSugerencias);
        scrollSugerencias.setPreferredSize(new Dimension(300, 80));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollSugerencias, BorderLayout.CENTER);

        // Mapa para asociar nombre -> teléfono
        Map<String, String> mapaEmpresaTelefono = new HashMap<>();

        // Evento para actualizar sugerencias mientras se escribe
        campoEmpresa.getDocument().addDocumentListener(new DocumentListener() {
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
                mapaEmpresaTelefono.clear();

                String texto = campoEmpresa.getText().trim();
                if (texto.length() >= 2) {
                    try (Connection cn = new conexion().conectar()) {
                        PreparedStatement pst = cn.prepareStatement("SELECT Empresa, Telefono FROM clientes WHERE Empresa LIKE ?");
                        pst.setString(1, "%" + texto + "%");

                        ResultSet rs = pst.executeQuery();
                        while (rs.next()) {
                            String cliente = rs.getString("Empresa");
                            String telefono = rs.getString("Telefono");
                            String entrada = cliente + " - " + telefono;

                            modeloLista.addElement(entrada);
                            mapaEmpresaTelefono.put(cliente, telefono);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Al hacer clic en una sugerencia, llenar el campo automáticamente
        listaSugerencias.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String seleccionado = listaSugerencias.getSelectedValue();
                    if (seleccionado != null) {
                        String[] partes = seleccionado.split(" - ");
                        if (partes.length == 2) {
                            campoEmpresa.setText(partes[0]);  // Solo nombre
                        }
                        modeloLista.clear();
                    }
                }
            }
        });

        int resultado = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Ingrese el nombre del cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String nombreEmpresa = campoEmpresa.getText().trim();
        String telefonoCliente = mapaEmpresaTelefono.get(nombreEmpresa);

        if (telefonoCliente == null || telefonoCliente.isEmpty()) {
            // Consulta directa si no se encontró en el mapa
            try (Connection cn = new conexion().conectar()) {
                PreparedStatement pst = cn.prepareStatement("SELECT Telefono FROM clientes WHERE Empresa = ?");
                pst.setString(1, nombreEmpresa);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    telefonoCliente = rs.getString("Telefono");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró un número asociado a este cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }

        // Saludo dinámico
        String saludo;
        int hora = LocalTime.now().getHour();
        if (hora >= 5 && hora < 12) {
            saludo = "Buenos días";
        } else if (hora >= 12 && hora < 19) {
            saludo = "Buenas tardes";
        } else {
            saludo = "Buenas noches";
        }

        String numeroCompleto = "52" + telefonoCliente;
        String enlaceEncuesta = "https://forms.office.com/r/LjLVQYjmXJ";
        String mensaje = saludo + ", agradecemos que se haya puesto en contacto con nuestro equipo de soporte técnico. "
                + "Su opinión es muy importante para ayudarnos a mejorar nuestros servicios. "
                + "Por favor, tómese un momento para responder esta breve encuesta:\n" + enlaceEncuesta;

        String url = "https://wa.me/" + numeroCompleto + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void pedirEmpleadoYEnviar(ActionEvent e) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JTextField campoEmpleado = new JTextField(15);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Nombre del supervisor:"));
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
        String mensaje = saludo + " " + nombreEmpleado + ", este es el informe de actividades del ticket que me asignó.";

        String url = "https://wa.me/" + numeroCompleto + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        imprimir_22 = new Imprimir_2();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnMenuSupervisor = new javax.swing.JButton();
        btnMenuSoporte = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(imprimir_22);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Exportacion");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Cambiar Logo");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setText("Encuesta de Satisfaccion");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Reporte Supervisor");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnMenuSupervisor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSupervisor.setText("Menu del Supervisor");
        btnMenuSupervisor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSupervisorActionPerformed(evt);
            }
        });

        btnMenuSoporte.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSoporte.setText("Menu de Soporte Tecnico");
        btnMenuSoporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSoporteActionPerformed(evt);
            }
        });

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 797, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMenuSupervisor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMenuModulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnMenuSoporte, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 749, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(144, 144, 144)
                .addComponent(btnMenuModulos, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnMenuSupervisor, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnMenuSoporte, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        imprimir_22.Imprimir(rootPane);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        imprimir_22.seleccionarYGuardarImagen(evt);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        pedirEmpresaYEnviar(null);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        pedirEmpleadoYEnviar(null);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnMenuSupervisorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSupervisorActionPerformed
        MenuSupervisor menu1 = new MenuSupervisor();
        menu1.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuSupervisorActionPerformed

    private void btnMenuSoporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSoporteActionPerformed
        MenuSoporte menu2 = new MenuSoporte();
        menu2.setVisible(true);
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
            java.util.logging.Logger.getLogger(ReporteVisita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReporteVisita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReporteVisita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReporteVisita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            @Override
            public void run() {
                new ReporteVisita().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton btnMenuSoporte;
    private javax.swing.JButton btnMenuSupervisor;
    private Imprimir_2 imprimir_22;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
