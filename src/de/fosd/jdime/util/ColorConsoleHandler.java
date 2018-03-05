package de.fosd.jdime.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static de.fosd.jdime.util.SuccessLevel.SUCCESS;
import static java.util.logging.Level.*;

public class ColorConsoleHandler extends ConsoleHandler {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private String getColor(Level level) {
        if (level.equals(SEVERE)) {
            return ANSI_RED;
        }

        if (level.equals(WARNING)) {
            return ANSI_YELLOW;
        }

        if (level.equals(INFO)) {
            return ANSI_CYAN;
        }

        if (level.equals(CONFIG)) {
            return ANSI_PURPLE;
        }

        if (level.equals(SUCCESS)) {
            return ANSI_GREEN;
        }

        return ANSI_WHITE;
    }

    @Override
    public void publish(LogRecord record) {
        Level level = record.getLevel();
        String color = getColor(level);

        System.err.print(color);
        super.publish(record);
        System.err.print(ANSI_RESET);
        flush();
    }
}
