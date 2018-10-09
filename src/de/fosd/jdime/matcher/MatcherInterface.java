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

import de.fosd.jdime.artifact.Artifact;
import de.fosd.jdime.config.merge.MergeContext;
import de.fosd.jdime.matcher.matching.Matchings;
import de.fosd.jdime.merge.MergeInterface;

/**
 * This interface should be implemented by <code>Matcher</code> classes that compare <code>Artifact</code>s and compute
 * <code>Matching</code>s.
 * <p>
 * Based on the computed <code>Matchings</code>, the <code>Merge</code> implementation (see {@link MergeInterface})
 * amalgamates a new, unified <code>Artifact</code>.
 *
 * @param <T>
 *         type of <code>Artifact</code>
 * @author Olaf Lessenich
 */
public interface MatcherInterface<T extends Artifact<T>> {

    /**
     * Returns a <code>Set</code> of <code>Matching</code>s for the provided <code>Artifact</code>s.
     *
     * @param context
     *         the <code>MergeContext</code> of the merge operation
     * @param left
     *         the left <code>Artifact</code> to compare
     * @param right
     *         the right <code>Artifact</code> to compare
     * @return a <code>Set</code> of <code>Matching</code>s
     */
    Matchings<T> match(MergeContext context, T left, T right);
}
