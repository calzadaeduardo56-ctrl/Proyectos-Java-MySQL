import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Configurar FlatLaf ANTES de crear cualquier ventana
        try {
            FlatIntelliJLaf.setup();
            UIManager.put("TextComponent.arc", 30);
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.arc", 30);
            UIManager.put("Component.borderColor", new Color(80, 80, 80));
            UIManager.put("Button.borderColor", new Color(80, 80, 80));
            UIManager.put("defaultFont", new Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        if (!Licencia.verificarLicencia()) {

            System.exit(0);

        }

       // Abrir Login
        java.awt.EventQueue.invokeLater(() -> {
            new IniciarSesion().setVisible(true);
        });
    }
}
