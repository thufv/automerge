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
 * Representation of a DOT language sub-graph.
 *
 * @see <a href="http://www.graphviz.org/">http://www.graphviz.org/</a>
 */
public final class GraphvizSubGraph extends GraphvizGraphBase {

    private final GraphvizGraph rootGraph;

    GraphvizSubGraph(GraphvizGraph rootGraph, String id) {
        super(id);
        this.rootGraph = rootGraph;
    }

    @Override
    public void dump(String indent, PrintWriter out) {

        if (!indent.isEmpty()) {
            out.write(indent);
        }

        out.printf("subgraph \"%s\"", id);
        super.dump(indent, out);
    }

    @Override
    GraphvizGraph getRootGraph() {
        return rootGraph;
    }
}
