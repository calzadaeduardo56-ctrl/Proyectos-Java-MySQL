
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.JOptionPane;

public class Licencia {

    private static final int DIAS_LICENCIA = 30;

    private static final Path CARPETA
            = Paths.get(System.getenv("APPDATA"), "Ticketix");

    private static final Path ARCHIVO
            = CARPETA.resolve("licencia.dat");

    private static void crearLicencia() {

        try {

            if (!Files.exists(CARPETA)) {
                Files.createDirectories(CARPETA);
            }

            LocalDate hoy = LocalDate.now();

            String contenido
                    = "INSTALACION=" + hoy + System.lineSeparator()
                    + "ULTIMO_USO=" + hoy;

            Files.writeString(ARCHIVO, contenido);

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(null,
                    "No se pudo crear la licencia.");

        }
    }

    private static LocalDate[] leerFechas() {

        try {

            var lineas = Files.readAllLines(ARCHIVO);

            LocalDate instalacion
                    = LocalDate.parse(lineas.get(0).split("=")[1]);

            LocalDate ultimoUso
                    = LocalDate.parse(lineas.get(1).split("=")[1]);

            return new LocalDate[]{instalacion, ultimoUso};

        } catch (Exception ex) {

            return null;

        }

    }

    private static void actualizarUltimoUso(LocalDate instalacion) {

        try {

            LocalDate hoy = LocalDate.now();

            String contenido
                    = "INSTALACION=" + instalacion + System.lineSeparator()
                    + "ULTIMO_USO=" + hoy;

            Files.writeString(ARCHIVO, contenido);

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    null,
                    "No fue posible actualizar la licencia.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

        }

    }

    private static boolean detectarManipulacionFecha() {

        LocalDate[] fechas = leerFechas();

        if (fechas == null) {
            return true;
        }

        LocalDate ultimoUso = fechas[1];

        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(ultimoUso)) {

            JOptionPane.showMessageDialog(
                    null,
                    """
                Se detectó un cambio en la fecha del sistema.

                No es posible validar la licencia.

                Contacte al desarrollador.
                """,
                    "Error de licencia",
                    JOptionPane.ERROR_MESSAGE);

            return true;

        }

        return false;

    }

    private static long diasRestantes(LocalDate fechaInstalacion) {

        long diasUsados = ChronoUnit.DAYS.between(
                fechaInstalacion,
                LocalDate.now());

        return DIAS_LICENCIA - diasUsados;
    }

    public static boolean verificarLicencia() {

        // Si es la primera ejecución, crea la licencia.
        if (!Files.exists(ARCHIVO)) {
            crearLicencia();
        }

        // Si detecta que el usuario atrasó el reloj, bloquea el programa.
        if (detectarManipulacionFecha()) {
            return false;
        }

        // Lee la fecha de instalación y la última fecha de uso.
        LocalDate[] fechas = leerFechas();

        if (fechas == null) {
            return false;
        }

        LocalDate fechaInstalacion = fechas[0];

        long restantes = diasRestantes(fechaInstalacion);

        // Licencia expirada
        if (restantes <= 0) {

            JOptionPane.showMessageDialog(
                    null,
                    """
                Ticketix

                La licencia de evaluación ha expirado.

                Este software estuvo disponible
                durante un periodo de prueba
                de 30 días.

                Para continuar utilizando Ticketix,
                solicite una nueva licencia.

                Gracias por utilizar Ticketix.
                """,
                    "Licencia vencida",
                    JOptionPane.ERROR_MESSAGE);

            return false;
        }

        // Aviso cuando faltan 7 días o menos
        if (restantes <= 7) {

            JOptionPane.showMessageDialog(
                    null,
                    "Su licencia expirará en "
                    + restantes
                    + " día(s).\n\n"
                    + "Contacte al desarrollador para renovar la licencia.",
                    "Aviso de licencia",
                    JOptionPane.WARNING_MESSAGE);
        }

        // Actualiza la última fecha de uso
        actualizarUltimoUso(fechaInstalacion);

        return true;
    }

}
