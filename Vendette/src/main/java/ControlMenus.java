
import javax.swing.JButton;

public class ControlMenus {

    public static void aplicarControlMenus(String puesto, JButton... botones) {
        // 🔹 Ocultamos todos primero
        for (JButton btn : botones) {
            if (btn != null) {
                btn.setVisible(false);
            }
        }

        for (JButton btn : botones) {
            if (btn == null) {
                continue;
            }

            String texto = btn.getText().toLowerCase(); // usamos el texto del botón

            if ("Admin".equalsIgnoreCase(puesto) && texto.contains("modulo")) {
                btn.setVisible(true);
            } else if ("Supervisor".equalsIgnoreCase(puesto) && texto.contains("supervisor")) {
                btn.setVisible(true);
            } else if ("Cajero".equalsIgnoreCase(puesto) && texto.contains("cajero")) {
                btn.setVisible(true);
            }
        }
    }
}
