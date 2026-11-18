
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom cell renderer that colors both text and background based on task
 * completion status.
 */
public class Colorear_Status extends DefaultTableCellRenderer {

    private static final Color COMPLETED_TEXT_COLOR = Color.WHITE;
    private static final Color COMPLETED_BACKGROUND_COLOR = new Color(144, 238, 144); // Verde claro

    private static final Color NOT_COMPLETED_TEXT_COLOR = Color.BLACK;
    private static final Color NOT_COMPLETED_BACKGROUND_COLOR = new Color(255, 255, 0); // Amarillo

    private static final Color NO_GESTION_TEXT_COLOR = Color.WHITE;
    private static final Color NO_GESTION_BACKGROUND_COLOR = Color.RED; // Rojo para "Sin gestión"

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
            final String Status = value.toString().trim();

            if (Status.equals("En Proceso")) {
                cell.setForeground(NOT_COMPLETED_TEXT_COLOR);
                cell.setBackground(NOT_COMPLETED_BACKGROUND_COLOR);
            } else if (Status.equals("Se ha Realizado")) {
                cell.setForeground(COMPLETED_TEXT_COLOR);
                cell.setBackground(COMPLETED_BACKGROUND_COLOR);
            } else if (Status.equals("Atrasado")) {
                cell.setForeground(NO_GESTION_TEXT_COLOR);
                cell.setBackground(NO_GESTION_BACKGROUND_COLOR);
            } else {
                cell.setForeground(Color.BLACK);
                cell.setBackground(Color.WHITE);
            }
        } else {
            cell.setForeground(Color.BLACK);
            cell.setBackground(Color.WHITE);
        }

        return cell;
    }

}
