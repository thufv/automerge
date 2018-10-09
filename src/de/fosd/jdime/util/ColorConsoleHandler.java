/**
 * AutoMerge
 * Copyright (C) 2013-2014 Olaf Lessenich
 * Copyright (C) 2014-2017 University of Passau, Germany
 * Copyright (C) 2018-2019 Fengmin Zhu
 * Copyright (C) 2019-2020 Tsinghua University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 * Olaf Lessenich <lessenic@fim.uni-passau.de>
 * Georg Seibt <seibt@fim.uni-passau.de>
 * Fengmin Zhu <zfm17@mails.tsinghua.edu.cn>
 */
package de.fosd.jdime.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static de.fosd.jdime.util.SuccessLevel.SUCCESS;
import static java.util.logging.Level.*;

/**
 * @author Fengmin Zhu
 */
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
