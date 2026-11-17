
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
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
public class ExportarExcel4 {

    private String empresaFiltrada;

    public ExportarExcel4(String empresaFiltrada) {
        this.empresaFiltrada = empresaFiltrada;
    }

    // Obtiene la suma total de costos por empresa desde la base de datos
    private double obtenerSumaCostosPorEmpresa() {
        double totalCostos = 0.0;

        if (empresaFiltrada == null || empresaFiltrada.trim().isEmpty()) {
            return 0.0;
        }

        try (Connection cn = new conexion().conectar()) {
            String sql = "SELECT SUM(Total) AS TotalCostos FROM gastos2 WHERE Empresa LIKE ?";
            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                pst.setString(1, "%" + empresaFiltrada.trim() + "%");
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    totalCostos = rs.getDouble("TotalCostos");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener la suma de costos: " + e.getMessage());
        }

        return totalCostos;
    }

    public void exportarExcel4(JTable t) throws IOException {
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
                Sheet hoja = libro.createSheet("Finanzas");

                int columnas = t.getColumnCount() - 1;

                // ======== FUENTES Y ESTILOS ========
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

                // Fuente de fecha
                Font fuenteFecha = libro.createFont();
                fuenteFecha.setFontHeightInPoints((short) 10);
                fuenteFecha.setColor(IndexedColors.DARK_BLUE.getIndex());

                CellStyle estiloFecha = libro.createCellStyle();
                estiloFecha.setFont(fuenteFecha);
                estiloFecha.setAlignment(CellStyle.ALIGN_CENTER);

                // Fuente encabezado
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

                // Estilo celda normal
                CellStyle estiloCelda = libro.createCellStyle();
                estiloCelda.setAlignment(CellStyle.ALIGN_CENTER);
                estiloCelda.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloCelda.setBorderTop(CellStyle.BORDER_THIN);
                estiloCelda.setBorderBottom(CellStyle.BORDER_THIN);
                estiloCelda.setBorderLeft(CellStyle.BORDER_THIN);
                estiloCelda.setBorderRight(CellStyle.BORDER_THIN);
                estiloCelda.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                estiloCelda.setFillPattern(CellStyle.SOLID_FOREGROUND);

                // ======== TÍTULO ========
                Row filaTitulo = hoja.createRow(0);
                Cell celdaTitulo = filaTitulo.createCell(0);
                celdaTitulo.setCellValue("Reporte de Gastos a Detalle");
                celdaTitulo.setCellStyle(estiloTitulo);

                hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        0, 0, 0, columnas - 1));

                // ======== FECHA Y HORA ========
                String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                Row filaFecha = hoja.createRow(1);
                Cell celdaFecha = filaFecha.createCell(0);
                celdaFecha.setCellValue("Fecha de generación: " + fechaHora);
                celdaFecha.setCellStyle(estiloFecha);
                hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        1, 1, 0, columnas - 1));

                // ======== ENCABEZADOS ========
                Row filaEncabezado = hoja.createRow(3);
                for (int c = 1; c < t.getColumnCount(); c++) {
                    Cell celda = filaEncabezado.createCell(c - 1);
                    celda.setCellValue(t.getColumnName(c));
                    celda.setCellStyle(estiloEncabezado);
                }

                // ======== DATOS ========
                DataFormat formato = libro.createDataFormat();

                CellStyle estiloNumero = libro.createCellStyle();
                estiloNumero.cloneStyleFrom(estiloCelda); // copia color, bordes, etc.
                estiloNumero.setDataFormat(formato.getFormat("0.00"));
                for (int f = 0; f < t.getRowCount(); f++) {
                    Row fila = hoja.createRow(f + 4);
                    for (int c = 1; c < t.getColumnCount(); c++) {
                        Cell celda = fila.createCell(c - 1);
                        Object valor = t.getValueAt(f, c);
                        if (valor instanceof Number) {
                            celda.setCellValue(((Number) valor).doubleValue());
                            celda.setCellStyle(estiloNumero); // 👈 IMPORTANTE
                        } else {
                            celda.setCellValue(valor != null ? valor.toString() : "");
                            celda.setCellStyle(estiloCelda);
                        }
                    }
                }

                // ======== TOTAL ========
                double totalCostos = obtenerSumaCostosPorEmpresa();
                int filaTotal = t.getRowCount() + 6;

                Row filaFinal = hoja.createRow(filaTotal);
                Cell celdaFinal = filaFinal.createCell(0);
                celdaFinal.setCellValue("Suma de costos de " + empresaFiltrada + " = $"
                        + String.format("%,.2f", totalCostos) + " pesos.");

                CellStyle estiloTotal = libro.createCellStyle();
                Font fuenteTotal = libro.createFont();
                fuenteTotal.setBoldweight(Font.BOLDWEIGHT_BOLD);
                estiloTotal.setFont(fuenteTotal);
                estiloTotal.setAlignment(CellStyle.ALIGN_LEFT);
                estiloTotal.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                estiloTotal.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                estiloTotal.setFillPattern(CellStyle.SOLID_FOREGROUND);
                estiloTotal.setBorderTop(CellStyle.BORDER_THIN);
                estiloTotal.setBorderBottom(CellStyle.BORDER_THIN);
                estiloTotal.setBorderLeft(CellStyle.BORDER_THIN);
                estiloTotal.setBorderRight(CellStyle.BORDER_THIN);

                celdaFinal.setCellStyle(estiloTotal);

                hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        filaTotal, filaTotal, 0, columnas - 1));

                // Ajustar tamaño de columnas
                for (int c = 0; c < columnas; c++) {
                    hoja.autoSizeColumn(c);
                }

                libro.write(archivo);
                Desktop.getDesktop().open(archivoXLSX);
            }
        }
    }

}
