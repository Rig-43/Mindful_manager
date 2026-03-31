package mindfulmanager.reflection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves a daily reflection to a local file.
 */
public class ReflectionJournal {
    private static final String FILE_PREFIX = "reflection_";
    private static final String FILE_SUFFIX = ".txt";

    private static final String ACCOMPLISHED_MARKER = "===ACCOMPLISHED===";
    private static final String FEELINGS_MARKER = "===FEELINGS===";

    private final File journalDir;

    public ReflectionJournal() {
        // Store in the user's home folder for predictable write permissions.
        journalDir = new File(System.getProperty("user.home"), ".mindful-manager");
    }

    private File getTodayFile(LocalDate date) {
        String name = FILE_PREFIX + date + FILE_SUFFIX;
        return new File(journalDir, name);
    }

    /**
     * Saves the given reflection for the provided date.
     */
    public void save(LocalDate date, String accomplished, String feelings) throws IOException {
        if (date == null) date = LocalDate.now();
        if (!journalDir.exists()) {
            Files.createDirectories(journalDir.toPath());
        }

        File file = getTodayFile(date);
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(ACCOMPLISHED_MARKER);
            writer.newLine();
            writer.write(accomplished == null ? "" : accomplished);
            writer.newLine();
            writer.write(FEELINGS_MARKER);
            writer.newLine();
            writer.write(feelings == null ? "" : feelings);
            writer.newLine();
        }
    }

    public void saveToday(String accomplished, String feelings) throws IOException {
        save(LocalDate.now(), accomplished, feelings);
    }

    /**
     * Loads today's reflection if it exists; otherwise returns null.
     */
    public ReflectionEntry loadToday() throws IOException {
        LocalDate today = LocalDate.now();
        File file = getTodayFile(today);
        if (!file.exists()) return null;

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        String accomplished = "";
        String feelings = "";

        int idxAcc = indexOf(lines, ACCOMPLISHED_MARKER);
        int idxFeel = indexOf(lines, FEELINGS_MARKER);

        if (idxAcc >= 0) {
            int start = idxAcc + 1;
            int end = (idxFeel >= 0) ? idxFeel : lines.size();
            accomplished = joinLines(lines, start, end);
        }
        if (idxFeel >= 0) {
            int start = idxFeel + 1;
            int end = lines.size();
            feelings = joinLines(lines, start, end);
        }

        return new ReflectionEntry(today, stripTrailingNewline(accomplished), stripTrailingNewline(feelings));
    }

    private static int indexOf(List<String> lines, String marker) {
        for (int i = 0; i < lines.size(); i++) {
            if (marker.equals(lines.get(i))) return i;
        }
        return -1;
    }

    private static String joinLines(List<String> lines, int startInclusive, int endExclusive) {
        if (startInclusive >= endExclusive) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = startInclusive; i < endExclusive; i++) {
            sb.append(lines.get(i));
            if (i < endExclusive - 1) sb.append('\n');
        }
        return sb.toString();
    }

    private static String stripTrailingNewline(String s) {
        if (s == null) return "";
        // normalize potential trailing whitespace differences
        return s.replaceAll("\\s+$", "");
    }
}

