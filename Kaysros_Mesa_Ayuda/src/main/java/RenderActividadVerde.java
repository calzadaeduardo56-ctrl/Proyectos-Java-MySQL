
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
public class RenderActividadVerde extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int colActividad = 3; // Índice de la columna "Actividades"
        int colFechaReal = 9; // Índice de la columna "Fecha_Real"

        Object fechaReal = table.getValueAt(row, colFechaReal);

        // Solo aplicar fondo verde si estamos en la columna Actividades y Fecha_Real ya tiene valor
        if (column == colActividad && fechaReal != null && !fechaReal.toString().trim().isEmpty()) {
            c.setBackground(new Color(144, 238, 144)); // Verde claro
            c.setForeground(Color.WHITE);
        } else {
            // Fondo blanco si no hay fecha real o no estamos en columna Actividades
            c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            c.setForeground(isSelected ? table.getSelectionForeground() : Color.WHITE);
        }

        return c;
    }
}
