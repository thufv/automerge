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
package de.fosd.jdime.strdump.graphviz;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Representation of a DOT language root graph.
 *
 * @see <a href="http://www.graphviz.org/">http://www.graphviz.org/</a>
 */
public final class GraphvizGraph extends GraphvizGraphBase {

    private static final String ROOT_ID = "ROOT";

    private final AtomicLong nextId;

    private final boolean strict;
    private final GraphvizGraphType type;

    /**
     * Constructs a new <code>GraphvizGraph</code> of the given <code>type</code>.
     *
     * @param strict
     *         whether the graph is to be 'strict' (without multi-edges) (note: this only adds a keyword to the
     *         output, adding of edges is unrestricted)
     * @param type
     *         the type of graph to be created
     */
    public GraphvizGraph(boolean strict, GraphvizGraphType type) {
        super(ROOT_ID);
        this.nextId = new AtomicLong();
        this.type = type;
        this.strict = strict;
    }

    @Override
    public void dump(String indent, PrintWriter out) {

        if (!indent.isEmpty()) {
            out.write(indent);
        }

        out.printf("%s%s \"%s\"", strict ? "strict " : "", type.name().toLowerCase(), id);
        super.dump(indent, out);
    }

    /**
     * Returns the next unique ID to be used in this <code>GraphvizGraph</code> and its sub-graphs.
     *
     * @return the next ID
     */
    String nextId() {
        return String.valueOf(nextId.getAndIncrement());
    }

    /**
     * Returns the <code>GraphvizGraphType</code> of this <code>GraphvizGraph</code>.
     *
     * @return the type of this graph
     */
    GraphvizGraphType getType() {
        return type;
    }

    @Override
    GraphvizGraph getRootGraph() {
        return this;
    }
}
