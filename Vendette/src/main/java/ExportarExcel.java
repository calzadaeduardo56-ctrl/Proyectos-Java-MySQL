
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author HP
 */
public class ExportarExcel {

    public void exportarExcel(JTable t) throws IOException {

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de Excel (.xlsx)", "xlsx");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Guardar archivo");
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            String ruta = chooser.getSelectedFile().toString();
            if (!ruta.toLowerCase().endsWith(".xlsx")) {
                ruta += ".xlsx";
            }

            File archivoXLSX = new File(ruta);
            Workbook libro = new XSSFWorkbook();

            try (FileOutputStream archivo = new FileOutputStream(archivoXLSX)) {

                Sheet hoja = libro.createSheet("Mi hoja de trabajo 1");

                // 🔴 Restamos 1 porque no queremos la columna ID (columna 0)
                int columnasVisibles = t.getColumnCount() - 1;

                // ======== FUENTE Y ESTILO DEL TÍTULO ========
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

                // ======== TÍTULO ========
                Row filaTitulo = hoja.createRow(0);
                Cell celdaTitulo = filaTitulo.createCell(0);
                celdaTitulo.setCellValue("Reporte del Surtido de Mercancias");
                celdaTitulo.setCellStyle(estiloTitulo);

                // 🔴 Ajustamos región combinada sin contar ID
                hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        0, 0, 0, columnasVisibles - 1));

                // ======== FECHA ========
                String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                Font fuenteFecha = libro.createFont();
                fuenteFecha.setFontHeightInPoints((short) 10);
                fuenteFecha.setColor(IndexedColors.DARK_BLUE.getIndex());

                CellStyle estiloFecha = libro.createCellStyle();
                estiloFecha.setFont(fuenteFecha);
                estiloFecha.setAlignment(CellStyle.ALIGN_CENTER);

                Row filaFecha = hoja.createRow(1);
                Cell celdaFecha = filaFecha.createCell(0);
                celdaFecha.setCellValue("Fecha de generación: " + fechaHora);
                celdaFecha.setCellStyle(estiloFecha);

                hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        1, 1, 0, columnasVisibles - 1));

                // ======== ESTILO ENCABEZADO ========
                Font fuenteEncabezado = libro.createFont();
                fuenteEncabezado.setBoldweight(Font.BOLDWEIGHT_BOLD);

                CellStyle estiloEncabezado = libro.createCellStyle();
                estiloEncabezado.setFont(fuenteEncabezado);
                estiloEncabezado.setAlignment(CellStyle.ALIGN_CENTER);
                estiloEncabezado.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloEncabezado.setBorderTop(CellStyle.BORDER_THIN);
                estiloEncabezado.setBorderBottom(CellStyle.BORDER_THIN);
                estiloEncabezado.setBorderLeft(CellStyle.BORDER_THIN);
                estiloEncabezado.setBorderRight(CellStyle.BORDER_THIN);
                estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                estiloEncabezado.setFillPattern(CellStyle.SOLID_FOREGROUND);

                // ======== FILA ENCABEZADO ========
                Row filaEncabezado = hoja.createRow(3);

                // 🔴 Empezamos desde columna 1 (ignoramos ID)
                for (int c = 1; c < t.getColumnCount(); c++) {
                    Cell celda = filaEncabezado.createCell(c - 1);
                    celda.setCellValue(t.getColumnName(c));
                    celda.setCellStyle(estiloEncabezado);
                }

                // ======== ESTILO CELDA ========
                CellStyle estiloCelda = libro.createCellStyle();
                estiloCelda.setAlignment(CellStyle.ALIGN_CENTER);
                estiloCelda.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloCelda.setBorderTop(CellStyle.BORDER_THIN);
                estiloCelda.setBorderBottom(CellStyle.BORDER_THIN);
                estiloCelda.setBorderLeft(CellStyle.BORDER_THIN);
                estiloCelda.setBorderRight(CellStyle.BORDER_THIN);
                estiloCelda.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                estiloCelda.setFillPattern(CellStyle.SOLID_FOREGROUND);

                // ======== DATOS ========
                for (int f = 0; f < t.getRowCount(); f++) {
                    Row fila = hoja.createRow(f + 4);

                    for (int c = 1; c < t.getColumnCount(); c++) {
                        Cell celda = fila.createCell(c - 1);

                        Object valor = t.getValueAt(f, c);

                        if (valor instanceof Number) {
                            celda.setCellValue(Double.parseDouble(valor.toString()));
                        } else {
                            celda.setCellValue(valor != null ? valor.toString() : "");
                        }

                        celda.setCellStyle(estiloCelda);
                    }
                }

                // ======== AUTO SIZE ========
                for (int c = 0; c < columnasVisibles; c++) {
                    hoja.autoSizeColumn(c);
                }

                libro.write(archivo);
                Desktop.getDesktop().open(archivoXLSX);
            }
        }
    }
}
