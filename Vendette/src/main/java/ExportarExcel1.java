
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportarExcel1 {

    public void exportarExcel(JTable ventas, JTable gastos) throws IOException {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar archivo");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de Excel (.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String ruta = chooser.getSelectedFile().toString();
        if (!ruta.toLowerCase().endsWith(".xlsx")) {
            ruta += ".xlsx";
        }

        Workbook libro = new XSSFWorkbook(); // POI 3.7
        Sheet hoja = libro.createSheet("CONCENTRADO DE UTILIDADES");

        /* ================= ESTILOS (LOS MISMOS QUE TU CÓDIGO) ================= */
        // ---- TÍTULO ----
        Font fuenteTitulo = libro.createFont();
        fuenteTitulo.setFontHeightInPoints((short) 14);
        fuenteTitulo.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());

        CellStyle estiloTitulo = libro.createCellStyle();
        estiloTitulo.setFont(fuenteTitulo);
        estiloTitulo.setAlignment(CellStyle.ALIGN_CENTER);
        estiloTitulo.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        estiloTitulo.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        estiloTitulo.setFillPattern(CellStyle.SOLID_FOREGROUND);

        // ---- FECHA ----
        Font fuenteFecha = libro.createFont();
        fuenteFecha.setFontHeightInPoints((short) 10);
        fuenteFecha.setColor(IndexedColors.DARK_BLUE.getIndex());

        CellStyle estiloFecha = libro.createCellStyle();
        estiloFecha.setFont(fuenteFecha);
        estiloFecha.setAlignment(CellStyle.ALIGN_CENTER);

        // ---- ENCABEZADO ----
        Font fuenteEncabezado = libro.createFont();
        fuenteEncabezado.setBoldweight(Font.BOLDWEIGHT_BOLD);

        CellStyle estiloEncabezado = libro.createCellStyle();
        estiloEncabezado.setFont(fuenteEncabezado);
        estiloEncabezado.setAlignment(CellStyle.ALIGN_CENTER);
        estiloEncabezado.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        aplicarBordes(estiloEncabezado);
        estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estiloEncabezado.setFillPattern(CellStyle.SOLID_FOREGROUND);

        // ---- CELDAS ----
        CellStyle estiloCelda = libro.createCellStyle();
        estiloCelda.setAlignment(CellStyle.ALIGN_CENTER);
        estiloCelda.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        aplicarBordes(estiloCelda);
        estiloCelda.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        estiloCelda.setFillPattern(CellStyle.SOLID_FOREGROUND);

        // ---- TOTALES VERDE / ROJO ----
        Font fontVerde = libro.createFont();
        fontVerde.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontVerde.setColor(IndexedColors.GREEN.getIndex());

        CellStyle estiloTotalVerde = libro.createCellStyle();
        estiloTotalVerde.setFont(fontVerde);

        Font fontRojo = libro.createFont();
        fontRojo.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontRojo.setColor(IndexedColors.RED.getIndex());

        CellStyle estiloTotalRojo = libro.createCellStyle();
        estiloTotalRojo.setFont(fontRojo);

        /* ================= CONTENIDO ================= */
        int fila = 0;

        // ---- TÍTULO ----
        Row filaTitulo = hoja.createRow(fila++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("CONCENTRADO DE UTILIDADES");
        celdaTitulo.setCellStyle(estiloTitulo);
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0,
                Math.max(ventas.getColumnCount(), gastos.getColumnCount()) - 1));

        // ---- FECHA ----
        Row filaFecha = hoja.createRow(fila++);
        Cell celdaFecha = filaFecha.createCell(0);
        celdaFecha.setCellValue("Fecha de generación: "
                + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        celdaFecha.setCellStyle(estiloFecha);

        fila++;

        // ================== TABLA VENTAS ==================
        fila = escribirTabla(hoja, ventas, fila, "VENTAS",
                estiloEncabezado, estiloCelda);

        fila += 2;

        // ================== TABLA GASTOS ==================
        fila = escribirTabla(hoja, gastos, fila, "GASTOS",
                estiloEncabezado, estiloCelda);

        fila += 2;

        // ================== CÁLCULOS ==================
        double totalVentas = calcularTotal(ventas, "TotalPagar");
        double totalGastos = calcularTotal(gastos, "PrecioGastos");
        double granTotal = totalVentas + totalGastos;

        fila = escribirTotal(hoja, fila, "Total Ventas = $"
                + String.format("%.2f", totalVentas) + " pesos",
                totalVentas >= 0 ? estiloTotalVerde : estiloTotalRojo);

        fila = escribirTotal(hoja, fila, "Total Gastos = $"
                + String.format("%.2f", totalGastos) + " pesos",
                estiloTotalRojo);

        escribirTotal(hoja, fila, "Gran Total = $"
                + String.format("%.2f", granTotal) + " pesos",
                granTotal >= 0 ? estiloTotalVerde : estiloTotalRojo);

        // ---- AJUSTAR COLUMNAS ----
        // ---- ANCHO DE COLUMNAS (LEGIBLE) ----
        for (int c = 0; c < 10; c++) {
            hoja.setColumnWidth(c, 18 * 256); // ancho cómodo
        }

        try (FileOutputStream out = new FileOutputStream(ruta)) {
            libro.write(out);
        }

        Desktop.getDesktop().open(new File(ruta));
    }

    /* ================= MÉTODOS AUXILIARES ================= */
    private int escribirTabla(Sheet hoja, JTable tabla, int filaInicio,
            String titulo, CellStyle encabezado, CellStyle celda) {

        Row rTitulo = hoja.createRow(filaInicio++);
        rTitulo.createCell(0).setCellValue(titulo);

        Row rEnc = hoja.createRow(filaInicio++);
        for (int c = 0; c < tabla.getColumnCount(); c++) {
            Cell cell = rEnc.createCell(c);
            cell.setCellValue(tabla.getColumnName(c));
            cell.setCellStyle(encabezado);
        }

        for (int f = 0; f < tabla.getRowCount(); f++) {
            Row row = hoja.createRow(filaInicio++);
            for (int c = 0; c < tabla.getColumnCount(); c++) {
                Cell cell = row.createCell(c);
                Object v = tabla.getValueAt(f, c);
                if (v instanceof Number) {
                    cell.setCellValue(Double.parseDouble(v.toString()));
                } else {
                    cell.setCellValue(v != null ? v.toString() : "");
                }
                cell.setCellStyle(celda);
            }
        }
        return filaInicio;
    }

   private double calcularTotal(JTable tabla, String nombreColumna) {

    double total = 0;
    DefaultTableModel model = (DefaultTableModel) tabla.getModel();

    int col = -1;

    // Buscar índice REAL en el modelo
    for (int i = 0; i < model.getColumnCount(); i++) {
        if (model.getColumnName(i).equalsIgnoreCase(nombreColumna)) {
            col = i;
            break;
        }
    }

    if (col == -1) {
        System.out.println("No se encontró la columna: " + nombreColumna);
        return 0;
    }

    for (int i = 0; i < model.getRowCount(); i++) {

        Object v = model.getValueAt(i, col);

        if (v != null) {
            try {
                total += Double.parseDouble(v.toString().replace(",", ""));
            } catch (NumberFormatException e) {
                System.out.println("Valor ignorado: " + v);
            }
        }
    }

    return Math.round(total * 100.0) / 100.0;
}

    private int escribirTotal(Sheet hoja, int fila, String texto, CellStyle estilo) {

        int columnaDerecha = 7; // Columna H (ajústala si quieres)

        Row r = hoja.getRow(fila);
        if (r == null) {
            r = hoja.createRow(fila);
        }

        Cell c = r.createCell(columnaDerecha);
        c.setCellValue(texto);
        c.setCellStyle(estilo);

        return fila + 1;
    }

    private void aplicarBordes(CellStyle cs) {
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);
    }
}
