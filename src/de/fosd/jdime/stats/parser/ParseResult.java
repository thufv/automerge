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
package de.fosd.jdime.stats.parser;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A <code>List</code> of <code>Content</code> instances resulting from a run of {@link Parser#parse(String)}.
 * In addition to the pieces of content the lines of code, the number of lines of code in conflicts and the number
 * of conflicts are stored. The {@link #toString()} and {@link #toString(String, String...)} methods produce the
 * concatenation of the <code>String</code> representations of the <code>Content</code> pieces.
 */
public class ParseResult extends ArrayList<Content> {

    private static final long serialVersionUID = 1L;

    private int linesOfCode;
    private int conflicts;
    private int conflictingLinesOfCode;

    /**
     * Returns the lines of code.
     *
     * @return the lines of code
     */
    public int getLinesOfCode() {
        return linesOfCode;
    }

    /**
     * Sets the lines of code to the new value.
     *
     * @param linesOfCode
     *         the new lines of code
     */
    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    /**
     * Returns the conflicts.
     *
     * @return the conflicts
     */
    public int getConflicts() {
        return conflicts;
    }

    /**
     * Sets the conflicts to the new value.
     *
     * @param conflicts
     *         the new conflicts
     */
    public void setConflicts(int conflicts) {
        this.conflicts = conflicts;
    }

    /**
     * Returns the conflicting lines of code.
     *
     * @return the conflicting lines of code
     */
    public int getConflictingLinesOfCode() {
        return conflictingLinesOfCode;
    }

    /**
     * Sets the conflicting lines of code to the new value.
     *
     * @param conflictingLinesOfCode
     *         the new conflicting lines of code
     */
    public void setConflictingLinesOfCode(int conflictingLinesOfCode) {
        this.conflictingLinesOfCode = conflictingLinesOfCode;
    }

    /**
     * Adds a merged line (one that is not in a conflict) to the <code>ParseResult</code>.
     *
     * @param line
     *         the line to add
     */
    public void addMergedLine(String line) {
        Content.Merged lines;

        if (isEmpty()) {
            lines = new Content.Merged();
            add(lines);
        } else {
            Content content = get(size() - 1);

            if (!content.isConflict) {
                lines = (Content.Merged) content;
            } else {
                lines = new Content.Merged();
                add(lines);
            }
        }

        lines.add(line);
    }

    /**
     * Adds a line that is part of a conflict to the <code>ParseResult</code>.
     *
     * @param line
     *         the line to add
     * @param left
     *         true iff the line is part of the left side of the conflict (otherwise it is part of the right side)
     */
    public void addConflictingLine(String line, boolean left) {
        Content.Conflict conflict;

        if (isEmpty()) {
            conflict = new Content.Conflict();
            add(conflict);
        } else {
            Content content = get(size() - 1);

            if (content.isConflict) {
                conflict = (Content.Conflict) content;
            } else {
                conflict = new Content.Conflict();
                add(conflict);
            }
        }

        if (left) {
            conflict.addLeft(line);
        } else {
            conflict.addRight(line);
        }
    }

    @Override
    public String toString() {
        return String.join(System.lineSeparator(), stream().map(Content::toString).collect(Collectors.toList()));
    }

    /**
     * Returns a <code>String</code> representation of this <code>ParseResult</code>. The identifiers will
     * be used by the <code>Content</code> implementations to identify their parts (for example <code>Conflict</code>
     * will use the first two identifiers for marking the two sides of the conflict).
     *
     * @param fstId
     *         the first identifier to use
     * @param ids
     *         the other identifiers to use
     * @return a <code>String</code> representing this <code>ParseResult</code>
     */
    public String toString(String fstId, String... ids) {
        return String.join(System.lineSeparator(), stream().map(c -> c.toString(fstId, ids)).collect(Collectors.toList()));
    }
}
