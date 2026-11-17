
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author HP
 */
public class RenderOpcionColor extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            String opcion = value != null ? value.toString() : "";

            if (opcion.equalsIgnoreCase("Aplica")) {
                c.setBackground(new Color(144, 238, 144)); // Verde claro
                c.setForeground(Color.WHITE);
            } else if (opcion.equalsIgnoreCase("No aplica")) {
                c.setBackground(new Color(255, 200, 100)); // Naranja claro
                c.setForeground(Color.WHITE);
            } else {
                c.setForeground(Color.WHITE); // Otro valor: sin color
            }
        } else {
            c.setBackground(table.getSelectionBackground()); // Mantener selección
        }

        return c;
    }
}
