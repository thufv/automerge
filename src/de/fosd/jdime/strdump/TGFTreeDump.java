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
package de.fosd.jdime.strdump;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import de.fosd.jdime.artifact.Artifact;

/**
 * Dumps the given <code>Artifact</code> tree using the TGF format.
 *
 * @see <a href="http://docs.yworks.com/yfiles/doc/developers-guide/tgf.html">TGF</a>
 */
public class TGFTreeDump implements StringDumper {

    @Override
    public <T extends Artifact<T>> String dump(Artifact<T> artifact, Function<Artifact<T>, String> getLabel) {
        AtomicInteger nextId = new AtomicInteger(); // an easy way to encapsulate an Integer for the lambdas
        Map<Artifact<T>, Integer> ids = new HashMap<>();
        List<String> nodeIDs = new ArrayList<>();
        List<String> connections = new ArrayList<>();
        Deque<Artifact<T>> q = new ArrayDeque<>(Collections.singleton(artifact));

        while (!q.isEmpty()) {
            Artifact<T> current = q.removeFirst();

            Integer fromId = ids.computeIfAbsent(current, a -> nextId.getAndIncrement());
            nodeIDs.add(String.format("%d %s", fromId, current.toString()));

            for (T t : current.getChildren()) {
                Integer toId = ids.computeIfAbsent(t, a -> nextId.getAndIncrement());
                connections.add(String.format("%d %d", fromId, toId));
            }

            current.getChildren().forEach(q::addFirst);
        }

        String ls = System.lineSeparator();
        return String.format("%s%n#%n%s", String.join(ls, nodeIDs), String.join(ls, connections));
    }
}
