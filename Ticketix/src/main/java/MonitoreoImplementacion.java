
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.sql.Date;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author HP
 */
public class MonitoreoImplementacion extends javax.swing.JFrame {

    private boolean modoCapturaManual = true;
    /**
     * Creates new form Asistencia1
     */
    private ImageIcon imagen;
    private Icon icocno;
    private String empresaFiltrada;
    private String puesto;
    private boolean formularioCargado = false;

    public MonitoreoImplementacion() {
        initComponents();

        agregarValidacionUsuario(Empresa, this::validarClienteDisponible);

        agregarValidacionUsuario(Producto, this::validarProductoDisponible);

        agregarValidacionUsuario(Empleado, this::validarEmpleadoDisponible);

        formularioCargado = true;

        RellenarCombos re = new RellenarCombos();

        re.RellenarComboBox("clientes", "Empresa", Empresa);

        re.RellenarComboBox("productos", "Producto", Producto);

        re.RellenarComboBox("empleados", "Nombre", Empleado);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/imagen/panel-de-administrador.png"));
        setIconImage(logoIcon.getImage());

        jButton1.setIcon(getIconUrl("/imagen/diskette.png", 30, 30));

        jButton2.setIcon(getIconUrl("/imagen/actualizar-flecha.png", 30, 30));

        jButton3.setIcon(getIconUrl("/imagen/boton-eliminar.png", 30, 30));

        jButton6.setIcon(getIconUrl("/imagen/sobresalir.png", 30, 30));

        jButton5.setIcon(getIconUrl("/imagen/adjunto.png", 30, 30));

        btnMenuSupervisor.setIcon(getIconUrl("/imagen/menu1.png", 30, 30));

        btnMenuModulos.setIcon(getIconUrl("/imagen/menu.png", 30, 30));

        pintarImagen(jLabel28, "Imagen3.png");

        cargarTabla();

        aplicarRenderizados();

        ocultarIdDetalle();

        limpiarCamposDespuesDeGuardar();

        //centrarColumnas();
        this.setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Evita cierre automático

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        MonitoreoImplementacion.this,
                        "¿Estás seguro de que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    dispose(); // Cierra la ventana
                }
                // Si elige NO, no hace nada y se queda en la ventana
            }
        });

// -- Etapa seleccionada ➔ Responsable
        Producto.addActionListener(e -> {
            if (modoCapturaManual) {
                cbxEtapas.showPopup();
            }
        });

// -- Responsable seleccionado ➔ Empleado
        cbxEtapas.addActionListener(e -> {
            if (modoCapturaManual) {
                cbxActividades.showPopup();
            }
        });

// -- Empleado seleccionado ➔ Empresa
        cbxActividades.addActionListener(e -> {
            if (modoCapturaManual) {
                cbxResponsable.showPopup();
            }
        });

        cbxResponsable.addActionListener(e -> {
            if (modoCapturaManual) {
                Empleado.showPopup();
            }
        });

        Empleado.addActionListener(e -> {
            if (modoCapturaManual) {
                Empresa.showPopup();
            }
        });

        jdeFechaFinal.getDateEditor().addPropertyChangeListener("date", e -> {
            if (modoCapturaManual && jdeFechaFinal.getDate() != null) {
                jComboBox3.showPopup();
            }
        });

// -- Gastos llenado ➔ Observaciones
        jComboBox3.addActionListener(e -> {
            if (modoCapturaManual) {
                txtObservaciones.requestFocus();
            }
        });

        tblImplementacion2.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt
            ) {
                modoCapturaManual = false;
                int filaSeleccionada = tblImplementacion2.rowAtPoint(evt.getPoint());

                if (evt.getClickCount() == 1 && filaSeleccionada != -1) {
                    actualizarFormularioDesdeTabla(filaSeleccionada);
                }
            }
        }
        );
    }

    private void actualizarFormularioDesdeTabla(int fila) {
        Producto.setSelectedItem(obtenerValorDeTabla(fila, 1));
        cbxEtapas.setSelectedItem(obtenerValorDeTabla(fila, 2));
        cbxActividades.setSelectedItem(obtenerValorDeTabla(fila, 3));
        cbxResponsable.setSelectedItem(obtenerValorDeTabla(fila, 4));
        Empleado.setSelectedItem(obtenerValorDeTabla(fila, 5));
        Empresa.setSelectedItem(obtenerValorDeTabla(fila, 6));
        jComboBox3.setSelectedItem(obtenerValorDeTabla(fila, 13));
        txtObservaciones.setText(obtenerValorDeTabla(fila, 12));
        String fechaRealStr = obtenerValorDeTabla(fila, 9);  // Ej: "2025-07-23"
        String fechaFinalStr = obtenerValorDeTabla(fila, 8); // Ej: "2025-08-01"

        // Asignar a JDateChooser si no están vacías
        if (!fechaRealStr.isEmpty()) {
            try {
                java.util.Date fechaReal = java.sql.Date.valueOf(fechaRealStr);
                jdeFechaIReal.setDate(fechaReal);
            } catch (IllegalArgumentException e) {
                jdeFechaIReal.setDate(null);
            }
        } else {
            jdeFechaIReal.setDate(null);
        }

        if (!fechaFinalStr.isEmpty()) {
            try {
                java.util.Date fechaFinal = java.sql.Date.valueOf(fechaFinalStr);
                jdeFechaFinal.setDate(fechaFinal);
            } catch (IllegalArgumentException e) {
                jdeFechaFinal.setDate(null);
            }
        } else {
            jdeFechaFinal.setDate(null);
        }

        modoCapturaManual = true;
    }

    private String obtenerValorDeTabla(int fila, int columna) {
        Object valor = tblImplementacion2.getValueAt(fila, columna);
        return (valor != null) ? valor.toString() : "";
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
        ControlMenus.aplicarControlMenus(puesto, btnMenuModulos, btnMenuSupervisor);
    }

    public void setEmpresaFiltrada(String empresaUsuario) {
        this.empresaFiltrada = empresaUsuario;
        cargarRegistrosAsistenciaPorEmpresa(); // 🔹 carga al asignar
        recalcularTotalDiasPorEmpresa();      // calcula solo para esa empresa
    }

    public void cargarRegistrosAsistenciaPorEmpresa() {

        String sql = "SELECT idasistencia, Producto, Num_Etapas, Etapas, Responsable, "
                + "Empleado, Empresa, Fecha_Inicial, Fecha_Final, Fecha_Real, "
                + "Dias_Retraso, Monitoreo, Observaciones, Opcion "
                + "FROM asistencia WHERE Empresa LIKE ?";

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{
                    "idasistencia", "Producto", "Num_Etapas", "Etapas", "Responsable",
                    "Empleado", "Empresa", "Fecha_Inicial", "Fecha_Final", "Fecha_Real",
                    "Días_Retraso", "Monitoreo", "Observaciones", "Opcion"
                }, 0
        );

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + empresaFiltrada.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsMd = rs.getMetaData();
                int columnas = rsMd.getColumnCount();

                while (rs.next()) {
                    Object[] fila = new Object[columnas];

                    for (int i = 0; i < columnas; i++) {
                        Object valor = rs.getObject(i + 1);

                        // Para fechas, podemos dar formato si quieres
                        if (valor instanceof java.sql.Date) {
                            fila[i] = new java.text.SimpleDateFormat("yyyy-MM-dd").format((java.sql.Date) valor);
                        } else {
                            fila[i] = valor;
                        }
                    }
                    modelo.addRow(fila);
                }
            }

            tblImplementacion2.setModel(modelo);
            aplicarRenderizados();
            ocultarIdDetalle();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar registros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int recalcularTotalDiasPorEmpresa() {

        DefaultTableModel model = (DefaultTableModel) tblImplementacion2.getModel();

        int columnaEmpresa = 6;      // ⚠ Ajusta al índice real
        int columnaDias = 10;        // ⚠ Ajusta al índice real (Dias_Retraso)

        int totalDias = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object empresa = model.getValueAt(i, columnaEmpresa);
            Object dias = model.getValueAt(i, columnaDias);

            if (empresa != null && dias != null) {

                if (empresaFiltrada.trim().equalsIgnoreCase(empresa.toString().trim())) {

                    try {
                        totalDias += Integer.parseInt(dias.toString());
                    } catch (NumberFormatException e) {
                        // Ignorar valores inválidos
                    }
                }
            }
        }

        if (totalDias == 0) {
            jLabel3.setText("No hay costos registrados para " + empresaFiltrada);
        } else {

            String textoDias = (totalDias == 1)
                    ? totalDias + " día"
                    : totalDias + " días";

            jLabel3.setText(empresaFiltrada + ": " + textoDias);
        }

        return totalDias;
    }

    public void eliminarRegistro() {
        int filaSeleccionada = tblImplementacion2.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            String idasistencia = tblImplementacion2.getValueAt(filaSeleccionada, 0).toString();

            int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                PreparedStatement pst = cn.prepareStatement("DELETE FROM asistencia WHERE idasistencia=?");
                pst.setString(1, idasistencia);
                pst.executeUpdate();
                limpiarCampos();
                limpiarCamposDespuesDeGuardar();

                // Eliminar del modelo asociado a jTable2
                DefaultTableModel modelo = (DefaultTableModel) tblImplementacion2.getModel();
                modelo.removeRow(filaSeleccionada);
                recalcularTotalDiasPorEmpresa();
            }
        } catch (SQLException ex) {
        }
    }

    private int generarNuevoIdAsistencia() {
        int nuevoId = 1; // Por defecto empezamos en 1

        try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement("SELECT MAX(idasistencia) FROM asistencia"); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                nuevoId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
        }
        return nuevoId;
    }

    public void actualizarRegistro() {

        int filaSeleccionada = tblImplementacion2.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(tblImplementacion2.getValueAt(filaSeleccionada, 0).toString());

            // 🔹 Confirmación antes de actualizar
            int confirmacion = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de actualizar este registro?",
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            // Recoger valores de la interfaz
            String Producto1 = Producto.getSelectedItem().toString();
            String Num_Etapas = cbxEtapas.getSelectedItem().toString();
            String Actividades = cbxActividades.getSelectedItem().toString();
            String Responsable = cbxResponsable.getSelectedItem().toString();
            String Empleado1 = Empleado.getSelectedItem().toString();
            String Empresa1 = Empresa.getSelectedItem().toString();
            String Observaciones = txtObservaciones.getText();
            String Opcion = jComboBox3.getSelectedItem().toString();

            // Fechas del formulario
            java.util.Date fechaFinalDate = jdeFechaFinal.getDate();
            java.util.Date fechaRealDate = jdeFechaIReal.getDate();

            java.sql.Date fechaFinal = (fechaFinalDate != null) ? new java.sql.Date(fechaFinalDate.getTime()) : null;
            java.sql.Date fechaReal = (fechaRealDate != null) ? new java.sql.Date(fechaRealDate.getTime()) : null;

            // Obtener fecha inicial y monitoreo actual desde la BD
            java.sql.Date fechaInicial;
            String monitoreoActual;
            try (Connection conn = new conexion().conectar(); PreparedStatement pstFecha = conn.prepareStatement("SELECT Fecha_Inicial, Monitoreo FROM asistencia WHERE idasistencia = ?")) {
                pstFecha.setInt(1, id);
                try (ResultSet rs = pstFecha.executeQuery()) {
                    if (rs.next()) {
                        fechaInicial = rs.getDate("Fecha_Inicial");
                        monitoreoActual = rs.getString("Monitoreo");
                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontró el registro para calcular fechas.");
                        return;
                    }
                }
            }

            // Calcular días de retraso solo si hay fecha final
            int diasRetraso = 0;
            String monitoreo = monitoreoActual; // mantener el actual por defecto

            if (fechaInicial != null && fechaFinal != null) {
                LocalDate inicio = fechaInicial.toLocalDate();
                LocalDate fin = fechaFinal.toLocalDate();
                LocalDate hoy = LocalDate.now();

                long diasTotales = ChronoUnit.DAYS.between(inicio, fin);
                long diasTranscurridos = ChronoUnit.DAYS.between(inicio, hoy);
                diasRetraso = (int) Math.min(Math.max(diasTranscurridos, 0), diasTotales);

                // 🔹 Solo recalcula "Atrasado" si se modificó la fecha real
                if (fechaReal != null) {
                    if (hoy.isAfter(fin)) {
                        monitoreo = "Atrasado";
                    } else {
                        monitoreo = "En Proceso";
                    }
                }
            }

            // Actualizar todo en la base de datos
            try (Connection conn = new conexion().conectar()) {
                String sql = "UPDATE asistencia SET Producto = ?, Num_Etapas = ?, Etapas = ?, Responsable = ?, Empleado = ?, Empresa = ?, "
                        + "Fecha_Final = ?, Fecha_Real = ?, Observaciones = ?, Opcion = ?, Dias_Retraso = ?, Monitoreo = ? "
                        + "WHERE idasistencia = ?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, Producto1);
                    pst.setString(2, Num_Etapas);
                    pst.setString(3, Actividades);
                    pst.setString(4, Responsable);
                    pst.setString(5, Empleado1);
                    pst.setString(6, Empresa1);
                    pst.setDate(7, fechaFinal);
                    pst.setDate(8, fechaReal);
                    pst.setString(9, Observaciones);
                    pst.setString(10, Opcion);
                    pst.setInt(11, diasRetraso);
                    pst.setString(12, monitoreo);
                    pst.setInt(13, id);
                    pst.executeUpdate();
                }

                cargarTabla();
            }

        } catch (SQLException | NumberFormatException | NullPointerException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar el registro: " + ex.getMessage());
        }
    }

    public void cargarTabla() {
        final String sql = "SELECT * FROM asistencia";
        final String[] columnas = {"idasistencia", "Producto", "Num_Etapas", "Etapas", "Responsable",
            "Empleado", "Empresa", "Fecha_Inicial", "Fecha_Final", "Fecha_Real", "Dias_Retraso", "Monitoreo", "Observaciones", "Opcion"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        conexion conexion = new conexion();
        try (Connection conn = conexion.conectar(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(sql)) {

            LocalDateTime ahora = LocalDateTime.now();

            while (rs.next()) {
                int id = rs.getInt("idasistencia");
                Date fechaInicioSQL = rs.getDate("Fecha_Inicial");
                Date fechaFinalSQL = rs.getDate("Fecha_Final");

                int diasRetraso = rs.getInt("Dias_Retraso");
                String monitoreoOriginal = rs.getString("Monitoreo"); // <- NO lo toques

                String monitoreoCalculado = monitoreoOriginal; // <- lo vamos a recalcular, pero respetando "Atrasado"

                if (fechaInicioSQL != null && fechaFinalSQL != null) {
                    LocalDate fechaInicio = fechaInicioSQL.toLocalDate();
                    LocalDate fechaFinal = fechaFinalSQL.toLocalDate();

                    long diasTotales = ChronoUnit.DAYS.between(fechaInicio, fechaFinal);
                    long diasTranscurridos = ChronoUnit.DAYS.between(fechaInicio, LocalDate.now());
                    diasRetraso = (int) Math.min(Math.max(diasTranscurridos, 0), diasTotales);

                    if (!"Atrasado".equalsIgnoreCase(monitoreoOriginal)) {
                        // Solo recalcula si NO era ya "Atrasado"
                        if (diasTranscurridos < 0) {
                            monitoreoCalculado = "Atrasado";
                        } else {
                            LocalDateTime fechaHoraFinal = LocalDateTime.of(fechaFinal, LocalTime.of(23, 59));
                            monitoreoCalculado = ahora.isBefore(fechaHoraFinal) ? "En Proceso" : "Se ha Realizado";
                        }
                    }

                    // Ahora sí actualizamos lo que corresponde
                    String sqlUpdate;
                    if (!monitoreoCalculado.equalsIgnoreCase(monitoreoOriginal)) {
                        sqlUpdate = "UPDATE asistencia SET Dias_Retraso = ?, Monitoreo = ? WHERE idasistencia = ?";
                        try (PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate)) {
                            pstUpdate.setInt(1, diasRetraso);
                            pstUpdate.setString(2, monitoreoCalculado);
                            pstUpdate.setInt(3, id);
                            pstUpdate.executeUpdate();
                        }
                    } else {
                        // Solo actualiza días de retraso
                        sqlUpdate = "UPDATE asistencia SET Dias_Retraso = ? WHERE idasistencia = ?";
                        try (PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate)) {
                            pstUpdate.setInt(1, diasRetraso);
                            pstUpdate.setInt(2, id);
                            pstUpdate.executeUpdate();
                        }
                    }
                }

                String[] fila = new String[columnas.length];
                for (int i = 0; i < columnas.length; i++) {
                    fila[i] = rs.getString(columnas[i]);
                }
                modelo.addRow(fila);
            }

            tblImplementacion2.setModel(modelo);
            aplicarRenderizados();
            ocultarIdDetalle();
            limpiarCamposDespuesDeGuardar();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void aplicarRenderizados() {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tblImplementacion2.getColumnCount(); i++) {
            // Evita sobrescribir el renderer personalizado de las columnas 3 y 11
            if (i != 3 && i != 11) {
                tblImplementacion2.getColumnModel().getColumn(i).setCellRenderer(centrado);
            }
        }

        // Columna de Actividades
        //tblImplementacion2.getColumnModel().getColumn(3).setCellRenderer(new RenderActividadVerde());
        // Columna de Monitoreo (Status)
        tblImplementacion2.getColumnModel().getColumn(11).setCellRenderer(new Colorear_Status());

        // Aplicar renderer a la columna 13 (índice 12)
        tblImplementacion2.getColumnModel().getColumn(13).setCellRenderer(new RenderOpcionColor());

    }

    private Icon getIconUrl(String url, int w, int h) {
        return new ImageIcon(new ImageIcon(getClass().getResource(url))
                .getImage().getScaledInstance(w, h, 0));
    }

    public void guardarRegistro() {
        if (validarCamposVacios()) {
            return;
        }

        int idasistencia = generarNuevoIdAsistencia();
        String Producto1 = Producto.getSelectedItem().toString();
        String Num_Etapas = cbxEtapas.getSelectedItem().toString();
        String Actividades = cbxActividades.getSelectedItem().toString();
        String Responsable = cbxResponsable.getSelectedItem().toString();
        String Empleado1 = Empleado.getSelectedItem().toString();
        String Empresa1 = Empresa.getSelectedItem().toString();
        String Observaciones = txtObservaciones.getText();
        String Opcion = jComboBox3.getSelectedItem().toString();

        if (Empresa1 == null || Actividades == null) {
            return;
        }

        if (etapaYaExiste(Empresa1, Actividades)) {

            JOptionPane.showMessageDialog(this,
                    "La etapa \"" + Actividades + "\" ya fue registrada para el cliente \""
                    + Empresa1 + "\".",
                    "Etapa repetida",
                    JOptionPane.WARNING_MESSAGE);

            abrirMenuEtapas();

            return;
        }

// Aquí continúa el INSERT
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFinal = jdeFechaFinal.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int diasRetraso = 0;
        String monitoreo = "Desconocido";

// Calcular días de retraso como en actualizarRegistro()
        if (fechaInicio != null && fechaFinal != null) {
            LocalDate hoy = LocalDate.now();
            long diasTotales = ChronoUnit.DAYS.between(fechaInicio, fechaFinal);
            long diasTranscurridos = ChronoUnit.DAYS.between(fechaInicio, hoy);
            diasRetraso = (int) Math.min(Math.max(diasTranscurridos, 0), diasTotales);

            if (hoy.isAfter(fechaFinal)) {
                monitoreo = "Atrasado";
            } else {
                monitoreo = "En Proceso";
            }
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                null, "¿Desea guardar este registro?", "Confirmar Registro", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // NOTA: Fecha_Real se eliminará de la inserción
            String sSQL = "INSERT INTO asistencia (idasistencia, Producto, Num_Etapas, Etapas, Responsable, Empleado, Empresa, "
                    + "Fecha_Inicial, Fecha_Final, Fecha_Real, Dias_Retraso, Monitoreo, Observaciones, Opcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection cn = new conexion().conectar(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
                pst.setInt(1, idasistencia);
                pst.setString(2, Producto1);
                pst.setString(3, Num_Etapas);
                pst.setString(4, Actividades);
                pst.setString(5, Responsable);
                pst.setString(6, Empleado1);
                pst.setString(7, Empresa1);
                pst.setDate(8, java.sql.Date.valueOf(fechaInicio));
                pst.setDate(9, java.sql.Date.valueOf(fechaFinal));
                pst.setNull(10, Types.DATE); // ← Aquí ponemos NULL en Fecha_Real
                pst.setInt(11, diasRetraso);
                pst.setString(12, monitoreo);
                pst.setString(13, Observaciones);
                pst.setString(14, Opcion);

                int filasInsertadas = pst.executeUpdate();
                if (filasInsertadas > 0) {
                    cargarTabla();
                    limpiarCampos();
                    recalcularTotalDiasPorEmpresa();

                    // Buscar y seleccionar el nuevo registro en la tabla
                    DefaultTableModel model = (DefaultTableModel) tblImplementacion2.getModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        int idTabla = Integer.parseInt(model.getValueAt(i, 0).toString());
                        if (idTabla == idasistencia) {
                            tblImplementacion2.setRowSelectionInterval(i, i);
                            tblImplementacion2.scrollRectToVisible(tblImplementacion2.getCellRect(i, 0, true));
                            break;
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void pedirEmpleadoYEnviar(ActionEvent e) {
        Object[] opciones = {"Reporte de Entrada", "Reporte de Salida"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "¿Qué tipo de mensaje deseas enviar?",
                "Seleccionar Reporte",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (seleccion == -1) {
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JTextField campoNombre = new JTextField(15);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Nombre del empleado:"));
        inputPanel.add(campoNombre);

        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        JList<String> listaSugerencias = new JList<>(modeloLista);
        JScrollPane scrollSugerencias = new JScrollPane(listaSugerencias);
        scrollSugerencias.setPreferredSize(new Dimension(300, 80));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollSugerencias, BorderLayout.CENTER);

        Map<String, String> mapaNombreTelefono = new HashMap<>();

        campoNombre.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                actualizar();
            }

            public void removeUpdate(DocumentEvent e) {
                actualizar();
            }

            public void changedUpdate(DocumentEvent e) {
                actualizar();
            }

            private void actualizar() {
                modeloLista.clear();
                mapaNombreTelefono.clear();
                String texto = campoNombre.getText().trim();
                if (texto.length() >= 2) {
                    try (Connection cn = new conexion().conectar()) {
                        PreparedStatement pst = cn.prepareStatement("SELECT Nombre, Telefono FROM empleados WHERE Nombre LIKE ?");
                        pst.setString(1, "%" + texto + "%");
                        ResultSet rs = pst.executeQuery();
                        while (rs.next()) {
                            String nombre = rs.getString("Nombre");
                            String tel = rs.getString("Telefono");
                            modeloLista.addElement(nombre + " - " + tel);
                            mapaNombreTelefono.put(nombre, tel);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        listaSugerencias.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String seleccionado = listaSugerencias.getSelectedValue();
                    if (seleccionado != null) {
                        String[] partes = seleccionado.split(" - ");
                        if (partes.length == 2) {
                            campoNombre.setText(partes[0]);
                        }
                        modeloLista.clear();
                    }
                }
            }
        });

        int resultado = JOptionPane.showConfirmDialog(null, panel, "Ingrese el nombre del empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String nombreEmpleado = campoNombre.getText().trim();
        String telefono = mapaNombreTelefono.get(nombreEmpleado);
        if (telefono == null || telefono.isEmpty()) {
            try (Connection cn = new conexion().conectar()) {
                PreparedStatement pst = cn.prepareStatement("SELECT Telefono FROM empleados WHERE Nombre = ?");
                pst.setString(1, nombreEmpleado);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    telefono = rs.getString("Telefono");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró ese empleado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }

        String numeroCompleto = "52" + telefono;
        String mensaje;
        List<String> listaTickets = new ArrayList<>();

        try (Connection cn = new conexion().conectar()) {
            PreparedStatement pst = cn.prepareStatement("SELECT idasistencia FROM asistencia WHERE Empleado = ?");
            pst.setString(1, nombreEmpleado);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                listaTickets.add(rs.getString("idasistencia"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (listaTickets.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontró ningún ticket asignado a este empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (seleccion == 0) {
            if (listaTickets.size() == 1) {
                mensaje = "El ticket " + listaTickets.get(0) + " se te asignó, para que lo atiendas conforme a los plazos establecidos. Te mando el reporte.";
            } else {
                mensaje = "Los tickets " + String.join(", ", listaTickets) + " se te asignaron, para que los atiendas conforme a los plazos establecidos. Te mando el reporte.";
            }
        } else {
            if (listaTickets.size() == 1) {
                mensaje = "Felicidades " + nombreEmpleado + ", el ticket " + listaTickets.get(0) + " se ha resuelto satisfactoriamente. ¡Gracias por tu paciencia!";
            } else {
                mensaje = "Felicidades " + nombreEmpleado + ", los tickets " + String.join(", ", listaTickets) + " se han resuelto satisfactoriamente. ¡Gracias por tu paciencia!";
            }
        }

        String url = "https://wa.me/" + numeroCompleto + "?text=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private boolean validarCamposVacios() {
        JTextField[] campos = {txtObservaciones};
        boolean hayVacios = false;

        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) {
                campo.putClientProperty("JComponent.outline", "error");
                campo.repaint();
                campo.revalidate();
                hayVacios = true;
            }
        }

        if (hayVacios) {
            mostrarMensaje("Para poder guardar, no debe dejar campos en blanco.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            new Timer(2000, evt -> {
                for (JTextField campo : campos) {
                    campo.putClientProperty("JComponent.outline", null);
                    campo.repaint();
                    campo.revalidate();
                }
            }).start();
        }

        return hayVacios;
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipo);
    }

    private void validarClienteDisponible() {

        String proveedorSeleccionado = Empresa.getSelectedItem().toString();

        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM Clientes WHERE Empresa = ?";

        try {
            Connection cn = new conexion().conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, proveedorSeleccionado);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("Status");

                // 🚨 SI NO ESTA DISPONIBLE
                if (status.equalsIgnoreCase("No esta Disponible")) {

                    JOptionPane.showMessageDialog(
                            null,
                            proveedorSeleccionado + " no está disponible",
                            "Cliente no disponible",
                            JOptionPane.WARNING_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> {
                        Empresa.requestFocusInWindow();
                        Empresa.showPopup();
                    });
                }
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar proveedor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validarProductoDisponible() {

        String proveedorSeleccionado = Producto.getSelectedItem().toString();

        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM productos WHERE Producto = ?";

        try {
            Connection cn = new conexion().conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, proveedorSeleccionado);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("Status");

                // 🚨 SI NO ESTA DISPONIBLE
                if (status.equalsIgnoreCase("No esta Disponible")) {

                    JOptionPane.showMessageDialog(
                            null,
                            proveedorSeleccionado + " no está disponible",
                            "Producto no disponible",
                            JOptionPane.WARNING_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> {
                        Producto.requestFocusInWindow();
                        Producto.showPopup();
                    });
                }
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar proveedor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validarEmpleadoDisponible() {

        String proveedorSeleccionado = Empleado.getSelectedItem().toString();

        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            return;
        }

        String sql = "SELECT Status FROM empleados WHERE Nombre = ?";

        try {
            Connection cn = new conexion().conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, proveedorSeleccionado);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("Status");

                // 🚨 SI NO ESTA DISPONIBLE
                if (status.equalsIgnoreCase("No esta Disponible")) {

                    JOptionPane.showMessageDialog(
                            null,
                            proveedorSeleccionado + " no está disponible",
                            "Empleado no disponible",
                            JOptionPane.WARNING_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> {
                        Empleado.requestFocusInWindow();
                        Empleado.showPopup();
                    });
                }
            }

            rs.close();
            ps.close();
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar proveedor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void agregarValidacionUsuario(JComboBox<?> combo, Runnable validacion) {

        combo.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (formularioCargado) {
                    validacion.run();
                }
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
    }

    private void ocultarIdDetalle() {
        tblImplementacion2.getColumnModel().getColumn(0).setMinWidth(0);
        tblImplementacion2.getColumnModel().getColumn(0).setMaxWidth(0);
        tblImplementacion2.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private void limpiarCampos() {
        txtObservaciones.setText("");
    }

    private void limpiarCamposDespuesDeGuardar() {

        // ==========================================================
        // LIMPIAR JCOMBOBOX SIN ELIMINAR SUS OPCIONES
        // ==========================================================
        Producto.setSelectedIndex(-1);
        cbxResponsable.setSelectedIndex(-1);
        cbxEtapas.setSelectedIndex(-1);
        Empleado.setSelectedIndex(-1);
        cbxActividades.setSelectedIndex(-1);
        Empresa.setSelectedIndex(-1);
        jComboBox3.setSelectedIndex(-1);

        // ==========================================================
        // LIMPIAR JDATECHOOSER
        // ==========================================================
        jdeFechaFinal.setDate(null);
        jdeFechaIReal.setDate(null);

    }

    private boolean etapaYaExiste(String cliente, String etapa) {

        boolean existe = false;

        String sql = "SELECT COUNT(*) FROM asistencia "
                + "WHERE Empresa = ? AND Etapas = ?";

        try {

            conexion mysql = new conexion();
            Connection cn = mysql.conectar();

            PreparedStatement pst = cn.prepareStatement(sql);

            pst.setString(1, cliente);
            pst.setString(2, etapa);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }

            rs.close();
            pst.close();
            cn.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Error al verificar la etapa:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return existe;
    }

    private void abrirMenuEtapas() {

        SwingUtilities.invokeLater(() -> {
            Empresa.requestFocusInWindow();
            Empresa.showPopup();
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Producto = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cbxEtapas = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cbxActividades = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cbxResponsable = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        Empleado = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtObservaciones = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblImplementacion2 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        Empresa = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jdeFechaIReal = new com.toedter.calendar.JDateChooser();
        jdeFechaFinal = new com.toedter.calendar.JDateChooser();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnMenuSupervisor = new javax.swing.JButton();
        btnMenuModulos = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("MONITOREO DE ETAPAS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Producto");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, 20));

        Producto.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Producto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ProductoItemStateChanged(evt);
            }
        });
        Producto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ProductoKeyReleased(evt);
            }
        });
        getContentPane().add(Producto, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 210, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Num_Etapas");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        cbxEtapas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbxEtapas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", " " }));
        getContentPane().add(cbxEtapas, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 80, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Etapas");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, -1, -1));

        cbxActividades.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbxActividades.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Junta inicial", "Instalacion", "Layout", "Turnos Horarios", "Periodos Catalogos", "Uso lector", "Instala Lector", "Comunicacion", "Reportes", "Web", "Desarrollo", "Exporta Nomina", "Capacitacion Final", "Perfiles de acceso", "Login", "Apertura Mesero", "Asignacion Comida", "Autorizado Cocina y Bebidas", "Monitoreo de Preparacion", "Cierre de Pedido (gral o dispersada)", "Cobro de Punto de Venta", "Resportes de Gastos, Ventas, Cortes del dia", "Ventana de Menus QR" }));
        getContentPane().add(cbxActividades, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, 170, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Responsable");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, -1, -1));

        cbxResponsable.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbxResponsable.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Conjunto", "Digisoft" }));
        getContentPane().add(cbxResponsable, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 150, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Empleado");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 120, -1, -1));

        Empleado.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(Empleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, 190, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Fecha Final");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 60, 112, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Fecha_Real");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 110, 112, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Observaciones");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 60, -1, -1));

        txtObservaciones.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtObservaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 50, 320, 40));

        tblImplementacion2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblImplementacion2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "idasistencia", "Producto", "Num_Etapas", "Etapas", "Responsable", "Empleado", "Empresa", "Fecha_Inicial", "Fecha_Final", "Fecha_Real", "Dias_Retraso", "Monitoreo", "Observaciones", "Opcion"
            }
        ));
        tblImplementacion2.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblImplementacion2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblImplementacion2MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblImplementacion2);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 296, 1320, 290));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 170, 40));

        Empresa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(Empresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 180, 190, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Cliente");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, -1, -1));

        jComboBox3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aplica", "No aplica" }));
        getContentPane().add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 170, 130, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Opcion");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 170, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Buscador");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 120, -1, -1));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 120, 110, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Editar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 230, 170, 40));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 230, 170, 40));

        jdeFechaIReal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jdeFechaIReal, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 110, 160, -1));

        jdeFechaFinal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(jdeFechaFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 60, 160, -1));

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setText("Enviar Reporte");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 230, 260, 40));

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setText("Exportacion");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 230, 260, 40));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 110, 230, 40));

        btnMenuSupervisor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuSupervisor.setText("Menu del Supervisor");
        btnMenuSupervisor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuSupervisorActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuSupervisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 170, 230, 40));

        btnMenuModulos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMenuModulos.setText("Menu de Modulos");
        btnMenuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuModulosActionPerformed(evt);
            }
        });
        getContentPane().add(btnMenuModulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 170, 230, 40));
        getContentPane().add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblImplementacion2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblImplementacion2MouseClicked

        // TODO add your handling code here:
    }//GEN-LAST:event_tblImplementacion2MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        guardarRegistro();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void ProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProductoKeyReleased

    }//GEN-LAST:event_ProductoKeyReleased

    private void ProductoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ProductoItemStateChanged

    }//GEN-LAST:event_ProductoItemStateChanged

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String textoFiltro = jTextField1.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT idasistencia, Producto, Num_Etapas, Etapas, "
                + "Responsable, Empleado, Empresa, Fecha_Inicial, Fecha_Final, "
                + "Fecha_Real, Dias_Retraso, Monitoreo, Observaciones, Opcion FROM asistencia");
        boolean tieneTexto = !textoFiltro.isEmpty();

        if (tieneTexto) {
            sql.append(" WHERE Empleado LIKE ? OR Empresa LIKE ?");
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"idasistencia", "Producto", "Num_Etapas", "Etapas", "Responsable",
                    "Empleado", "Empresa", "Fecha_Inicial", "Fecha_Final", "Fecha_Real", "Dias_Retraso", "Monitoreo", "Observaciones", "Opcion"}, 0);

        try (Connection cn = new conexion().conectar(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            if (tieneTexto) {
                String filtro = "%" + textoFiltro + "%";
                ps.setString(1, filtro); // Empresa
                ps.setString(2, filtro); // Empleado
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData rsMd = rs.getMetaData();
                int columnas = rsMd.getColumnCount();

                while (rs.next()) {
                    Object[] fila = new Object[columnas];
                    for (int i = 0; i < columnas; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    modelo.addRow(fila);
                }
            }

            tblImplementacion2.setModel(modelo);
            aplicarRenderizados();
            ocultarIdDetalle();
            recalcularTotalDiasPorEmpresa();

        } catch (SQLException ex) {
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        actualizarRegistro();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        eliminarRegistro();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        try {
            // Suponiendo que tienes una variable llamada empresaFiltrada
            // (por ejemplo, establecida al seleccionar la empresa en tu sistema)
            ExportarExcel2 obj = new ExportarExcel2(empresaFiltrada);
            obj.exportarExcel2(tblImplementacion2);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar el archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        pedirEmpleadoYEnviar(null);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnMenuSupervisorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuSupervisorActionPerformed
        MenuSupervisor menu = new MenuSupervisor();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuSupervisorActionPerformed

    private void btnMenuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuModulosActionPerformed
        MenuModulos menu = new MenuModulos();
        menu.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnMenuModulosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MonitoreoImplementacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MonitoreoImplementacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitoreoImplementacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MonitoreoImplementacion.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        try {

            FlatIntelliJLaf.setup();
            UIManager.put("TextComponent.arc", 30);
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.arc", 30);
            UIManager.put("Component.borderColor", new Color(80, 80, 80));
            UIManager.put("Button.borderColor", new Color(80, 80, 80));
            UIManager.put("defaultFont", new Font("Segoe UI", Font.BOLD, 14));

        } catch (Exception ex) {
            System.err.println("No se pudo aplicar FlatLaf");
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MonitoreoImplementacion().setVisible(true);
            }
        });
    }

    private void pintarImagen(JLabel lbl, String ruta) {
        try {
            // Carga de imagen como recurso (dentro del .jar)
            java.net.URL imgURL = getClass().getResource("/imagen/Imagen3.png");
            if (imgURL != null) {
                this.imagen = new ImageIcon(imgURL);
                this.icocno = new ImageIcon(
                        this.imagen.getImage().getScaledInstance(
                                lbl.getWidth(), lbl.getHeight(), Image.SCALE_SMOOTH));
                lbl.setIcon(this.icocno);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Empleado;
    private javax.swing.JComboBox<String> Empresa;
    private javax.swing.JComboBox<String> Producto;
    private javax.swing.JButton btnMenuModulos;
    private javax.swing.JButton btnMenuSupervisor;
    private javax.swing.JComboBox<String> cbxActividades;
    private javax.swing.JComboBox<String> cbxEtapas;
    private javax.swing.JComboBox<String> cbxResponsable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private com.toedter.calendar.JDateChooser jdeFechaFinal;
    private com.toedter.calendar.JDateChooser jdeFechaIReal;
    private javax.swing.JTable tblImplementacion2;
    private javax.swing.JTextField txtObservaciones;
    // End of variables declaration//GEN-END:variables

}
