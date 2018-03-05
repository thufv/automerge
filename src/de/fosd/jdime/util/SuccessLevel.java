package de.fosd.jdime.util;

import java.util.logging.Level;

public class SuccessLevel extends Level {
    public static final Level SUCCESS = new SuccessLevel("SUCCESS", 1100);

    protected SuccessLevel(String name, int value) {
        super(name, value);
    }
}
