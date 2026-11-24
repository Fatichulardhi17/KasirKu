package kasir.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/** Pembacaan dan penulisan file CSV sederhana (pemisah titik koma). */
public final class Csv {

    private static final char SEPARATOR = ';';

    private Csv() {
    }

    public static List<String[]> read(Path path) throws IOException {
        if (!Files.exists(path)) {
            return List.of();
        }
        List<String[]> rows = new ArrayList<>();
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            rows.add(parse(line));
        }
        return rows;
    }

    public static void write(Path path, List<String[]> rows) throws IOException {
        List<String> lines = new ArrayList<>(rows.size());
        for (String[] row : rows) {
            lines.add(join(row));
        }
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temp = path.resolveSibling(path.getFileName() + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        try {
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void append(Path path, String[] header, List<String[]> rows) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        boolean newFile;
        try {
            newFile = !Files.exists(path) || Files.size(path) == 0;
        } catch (IOException ex) {
            // Jika gagal cek size, anggap file baru
            newFile = !Files.exists(path);
        }
        
        StringBuilder sb = new StringBuilder();
        if (newFile) {
            sb.append(join(header)).append('\n');
        }
        for (String[] row : rows) {
            sb.append(join(row)).append('\n');
        }
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static String join(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(SEPARATOR);
            }
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    public static String[] parse(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                quoted = true;
            } else if (c == SEPARATOR) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        
        // Peringatan: jika masih quoted di akhir, berarti CSV tidak valid
        // tapi tetap tambahkan field terakhir agar tidak hilang data
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuotes = value.indexOf(SEPARATOR) >= 0
                || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0
                || value.indexOf('\r') >= 0;
        if (!needsQuotes) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
