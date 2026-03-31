package mindfulmanager.reflection;

import java.time.LocalDate;

/**
 * Stores one day's daily reflection.
 */
public class ReflectionEntry {
    private final LocalDate date;
    private final String accomplished;
    private final String feelings;

    public ReflectionEntry(LocalDate date, String accomplished, String feelings) {
        this.date = date;
        this.accomplished = accomplished == null ? "" : accomplished;
        this.feelings = feelings == null ? "" : feelings;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getAccomplished() {
        return accomplished;
    }

    public String getFeelings() {
        return feelings;
    }
}

