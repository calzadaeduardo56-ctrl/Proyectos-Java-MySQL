
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author HP
 */
public class ColorearStatus3 extends DefaultTableCellRenderer{
    
     private static final Color COMPLETED_TEXT_COLOR = Color.WHITE;
    private static final Color COMPLETED_BACKGROUND_COLOR = new Color(144, 238, 144); // Forest Green

    private static final Color NOT_COMPLETED_TEXT_COLOR = Color.WHITE;
    private static final Color NOT_COMPLETED_BACKGROUND_COLOR = new Color(255, 0, 0);

    @Override
    public Component getTableCellRendererComponent(
            final JTable table,
            final Object value,
            final boolean isSelected,
            final boolean hasFocus,
            final int row,
            final int column) {

        // Use the default rendering behavior
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setHorizontalAlignment(SwingConstants.CENTER);

        if (value != null) {
            final String status = value.toString().trim();

            if (status.equals("Esta Disponible")) {
                cell.setForeground(COMPLETED_TEXT_COLOR);
                cell.setBackground(COMPLETED_BACKGROUND_COLOR);
            } else if (status.equals("No esta Disponible")) {
                cell.setForeground(NOT_COMPLETED_TEXT_COLOR);
                cell.setBackground(NOT_COMPLETED_BACKGROUND_COLOR);
            } else {
                // Default colors for unknown status
                cell.setForeground(Color.BLACK);
                cell.setBackground(Color.WHITE);
            }
        } else {
            // Handle null values gracefully
            cell.setForeground(Color.BLACK);
            cell.setBackground(Color.WHITE);
        }

        return cell;
    }
    
}
