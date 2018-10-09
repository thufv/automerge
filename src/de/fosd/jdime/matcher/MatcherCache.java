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
package de.fosd.jdime.matcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.fosd.jdime.artifact.Artifact;

/**
 * Caches various properties of {@link Artifact} trees used by the {@link Matcher}.
 *
 * @param <T>
 *         the type of the <code>Artifact</code>s
 */
class MatcherCache<T extends Artifact<T>> {

    private Map<Artifact<T>, Boolean> orderedChildren;
    private Map<Artifact<T>, Boolean> uniquelyLabeledChildren;
    private Map<Artifact<T>, Boolean> fullyOrdered;

    /**
     * Constructs a new empty {@link MatcherCache}.
     */
    MatcherCache() {
        this.orderedChildren = new HashMap<>();
        this.uniquelyLabeledChildren = new HashMap<>();
        this.fullyOrdered = new HashMap<>();
    }

    /**
     * Returns whether the given {@code artifact} has only uniquely labeled children.
     *
     * @param artifact
     *         the {@link Artifact} to check for uniquely labeled children
     * @return true iff all children of the given {@code artifact} have a unique label
     * @see Artifact#getUniqueLabel()
     */
    boolean uniquelyLabeledChildren(T artifact) {
        return uniquelyLabeledChildren.computeIfAbsent(artifact, a ->
                a.getChildren().stream().map(T::getUniqueLabel).allMatch(Optional::isPresent));
    }

    /**
     * Returns whether any child of the given {@code artifact} is ordered.
     *
     * @param artifact
     *         the {@link Artifact} to check for ordered children
     * @return true iff any child of the given {@code artifact} is ordered
     * @see Artifact#isOrdered()
     */
    boolean orderedChildren(T artifact) {
        return orderedChildren.computeIfAbsent(artifact, a ->
                a.getChildren().stream().anyMatch(T::isOrdered));
    }

    /**
     * Returns whether the tree rooted in {@code artifact} is fully ordered.
     *
     * @param artifact
     *         the root of the {@link Artifact} tree to check for full ordering
     * @return true iff the tree rooted in {@code artifact} is fully ordered
     * @see Artifact#isOrdered()
     */
    boolean fullyOrdered(T artifact) {
        return fullyOrdered.computeIfAbsent(artifact, a ->
                a.isOrdered() && a.getChildren().stream().allMatch(this::fullyOrdered));
    }
}
