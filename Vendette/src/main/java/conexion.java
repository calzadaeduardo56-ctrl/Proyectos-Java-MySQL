import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.Connection;

public class conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/vendette";
    private static final String USUARIO = "root";
    private static final String CLAVE = "1234";
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    public Connection conectar() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: No se encontró el driver de MySQL.\n" + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.\n" + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        return null; // Retorna null solo si hay error 
    }
}
