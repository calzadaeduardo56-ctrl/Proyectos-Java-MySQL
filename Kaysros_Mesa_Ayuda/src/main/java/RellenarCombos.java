
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author HP
 */
public class RellenarCombos {

    public void RellenarComboBox(String tabla, String columna, JComboBox combo) {
        String sql = "SELECT * FROM " + tabla;
        conexion mysql = new conexion();
        Connection cn = mysql.conectar();
        try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                combo.addItem(rs.getString(columna));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar el combo: " + e.getMessage());
        }
    }

    public void RellenarTextField(String tabla, String columna, JTextField textField) {
        String sql = "SELECT * FROM " + tabla + " LIMIT 1";

        conexion mysql = new conexion();
        Connection cn = mysql.conectar();

        try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                textField.setText(rs.getString(columna));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar el campo de texto: " + e.getMessage());
        }
    }
}
