import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Lee la hora de la alarma desde un archivo de texto plano.
 * Primera línea no vacía que no empiece por {@code #} debe ser {@code HH:mm}.
 */
public final class ScheduleFile {

    public static final Path DEFAULT_PATH = Path.of("schedule.txt");

    private ScheduleFile() {
    }

    public static LocalTime readTime(Path path) throws IOException {
        Path absolute = path.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute)) {
            throw new IOException(
                    "No se encontró " + absolute
                            + ". Crea el archivo con una línea HH:mm (ej. 07:30).");
        }

        List<String> lines = Files.readAllLines(absolute);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            try {
                return LocalTime.parse(line);
            } catch (DateTimeParseException e) {
                throw new IOException(
                        "Hora inválida en " + absolute + " (línea " + (i + 1) + "): '"
                                + line + "'. Usa HH:mm, por ejemplo 07:30.");
            }
        }

        throw new IOException(
                absolute + " no contiene una hora. Añade una línea con formato HH:mm.");
    }
}
