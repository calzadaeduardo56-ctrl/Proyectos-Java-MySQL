/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom cell renderer that colors both text and background based on priority
 * status.
 */
public class ColorearStatus extends DefaultTableCellRenderer {

    private static final Color HIGH_PRIORITY_TEXT = Color.WHITE;
    private static final Color HIGH_PRIORITY_BG = new Color(220, 53, 69); // Strong red

    private static final Color MEDIUM_PRIORITY_TEXT = Color.BLACK;
    private static final Color MEDIUM_PRIORITY_BG = new Color(255, 223, 128); // Light orange

    private static final Color LOW_PRIORITY_TEXT = Color.BLACK;
    private static final Color LOW_PRIORITY_BG = new Color(144, 238, 144); // Light green

    private static final Color NO_PRIORITY_TEXT = Color.BLACK;
    private static final Color NO_PRIORITY_BG = Color.WHITE;

    @Override
    public Component getTableCellRendererComponent(
            final JTable table,
            final Object value,
            final boolean isSelected,
            final boolean hasFocus,
            final int row,
            final int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setHorizontalAlignment(SwingConstants.CENTER);

        if (value != null) {
            final String status = value.toString().trim();

            switch (status) {
                case "Alta":
                    cell.setForeground(HIGH_PRIORITY_TEXT);
                    cell.setBackground(HIGH_PRIORITY_BG);
                    break;
                case "Media":
                    cell.setForeground(MEDIUM_PRIORITY_TEXT);
                    cell.setBackground(MEDIUM_PRIORITY_BG);
                    break;
                case "Baja":
                    cell.setForeground(LOW_PRIORITY_TEXT);
                    cell.setBackground(LOW_PRIORITY_BG);
                    break;
                case "Ninguna":
                default:
                    cell.setForeground(NO_PRIORITY_TEXT);
                    cell.setBackground(NO_PRIORITY_BG);
                    break;
            }
        } else {
            cell.setForeground(NO_PRIORITY_TEXT);
            cell.setBackground(NO_PRIORITY_BG);
        }

        return cell;
    }
}
