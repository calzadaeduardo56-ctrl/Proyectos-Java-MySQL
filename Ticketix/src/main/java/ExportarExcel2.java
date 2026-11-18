
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportarExcel2 {

    private String empresaFiltrada;

    public ExportarExcel2(String empresaFiltrada) {
        this.empresaFiltrada = empresaFiltrada;
    }

    public void exportarExcel2(JTable t) throws IOException {
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
            Workbook libro = new XSSFWorkbook(); // Compatible con POI 3.7

            try (FileOutputStream archivo = new FileOutputStream(archivoXLSX)) {
                Sheet hoja = libro.createSheet("Etapas");

                int columnas = t.getColumnCount() - 1;

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

                // ======== CREAR FILA DE TÍTULO ========
                Row filaTitulo = hoja.createRow(0);
                Cell celdaTitulo = filaTitulo.createCell(0);
                celdaTitulo.setCellValue("Reporte de Etapas");
                celdaTitulo.setCellStyle(estiloTitulo);
                hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        0, 0, 0, columnas - 1));

                // ======== FECHA Y HORA ========
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
                        1, 1, 0, columnas - 1));

                // ======== ENCABEZADOS ========
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

                Row filaEncabezado = hoja.createRow(3);
                for (int c = 1; c < t.getColumnCount(); c++) {
                    Cell celda = filaEncabezado.createCell(c - 1);
                    celda.setCellValue(t.getColumnName(c));
                    celda.setCellStyle(estiloEncabezado);
                }

                // ======== ESTILO DE CELDAS ========
                CellStyle estiloCelda = libro.createCellStyle();
                estiloCelda.setAlignment(CellStyle.ALIGN_CENTER);
                estiloCelda.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloCelda.setBorderTop(CellStyle.BORDER_THIN);
                estiloCelda.setBorderBottom(CellStyle.BORDER_THIN);
                estiloCelda.setBorderLeft(CellStyle.BORDER_THIN);
                estiloCelda.setBorderRight(CellStyle.BORDER_THIN);
                estiloCelda.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                estiloCelda.setFillPattern(CellStyle.SOLID_FOREGROUND);

                // ======== DATOS DE LA TABLA ========
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

                // ======== SUMA DE DÍAS DE RETRASO (desde la BD asistencia) ========
                int totalDias = calcularSumaDiasRetrasoDesdeAsistencia(empresaFiltrada);

                Font fuenteTotal = libro.createFont();
                fuenteTotal.setBoldweight(Font.BOLDWEIGHT_BOLD);

                CellStyle estiloTotal = libro.createCellStyle();
                estiloTotal.setFont(fuenteTotal);
                estiloTotal.setAlignment(CellStyle.ALIGN_CENTER);
                estiloTotal.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloTotal.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                estiloTotal.setFillPattern(CellStyle.SOLID_FOREGROUND);

                int filaTotal = t.getRowCount() + 5;
                Row filaSuma = hoja.createRow(filaTotal);
                Cell celdaSuma = filaSuma.createCell(0);
                celdaSuma.setCellValue("La suma de días de retraso de " + empresaFiltrada
                        + " es: " + totalDias + ((totalDias == 1) ? " día" : " días"));
                celdaSuma.setCellStyle(estiloTotal);

                hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        filaTotal, filaTotal, 0, columnas - 1));

                // ======== AJUSTE AUTOMÁTICO ========
                for (int c = 0; c < columnas; c++) {
                    hoja.autoSizeColumn(c);
                }

                libro.write(archivo);
                Desktop.getDesktop().open(archivoXLSX);
            }
        }
    }

    // ======== SUMA DE DÍAS DESDE LA TABLA ASISTENCIA ========
    private int calcularSumaDiasRetrasoDesdeAsistencia(String empresa) {
        int total = 0;
        try (Connection cn = new conexion().conectar()) {
            String sql = "SELECT SUM(Dias_Retraso) AS TotalDias FROM asistencia WHERE Empresa LIKE ?";
            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                pst.setString(1, empresa.trim());
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    total = rs.getInt("TotalDias");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al calcular días de retraso: " + e.getMessage());
        }
        return total;
    }
}
