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

/**
 * Representation of a DOT language edge of the form "ID (-- | -&gt;) ID".
 */
public final class GraphvizEdge extends GraphvizStatement {

    private final GraphvizGraphType type;
    private final GraphvizNode from;
    private final GraphvizNode to;

    /**
     * Constructs a new <code>GraphvizEdge</code> between the two given <code>GraphvizNode</code>s.
     *
     * @param graph
     *         the Graphviz graph containing this <code>GraphvizStatement</code>
     * @param type
     *         the type of the <code>GraphvizGraph</code> containing the edge.
     * @param from
     *         the starting node
     * @param to
     *         the destination node
     */
    GraphvizEdge(GraphvizGraphBase graph, GraphvizGraphType type, GraphvizNode from, GraphvizNode to) {
        super(graph);
        this.type = type;
        this.from = from;
        this.to = to;
    }

    @Override
    public void dump(String indent, PrintWriter out) {

        if (!indent.isEmpty()) {
            out.write(indent);
        }

        out.printf("%s %s %s", from.getId(), type.edgeOp, to.getId());
        super.dump("", out);
    }

    @Override
    public GraphvizEdge attribute(String lhs, String rhs) {
        super.attribute(lhs, rhs);
        return this;
    }
}
