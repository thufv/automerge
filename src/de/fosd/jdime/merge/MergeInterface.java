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
package de.fosd.jdime.merge;

import de.fosd.jdime.artifact.Artifact;
import de.fosd.jdime.config.merge.MergeContext;
import de.fosd.jdime.operations.MergeOperation;

/**
 * Interface for merge algorithms.
 *
 * @param <T>
 *         the type of the artifact
 * @author Olaf Lessenich
 */
public interface MergeInterface<T extends Artifact<T>> {

    /**
     * Executes a merge based on a <code>MergeOperation</code>.
     * <p>
     * The source and target <code>Artifacts</code> are extracted from the <code>MergeOperation</code>. It is determined
     * what kind of merge (e.g., two-way or three-way) has to be done. The source <code>Artifacts</code> are compared to
     * each other using the <code>Matcher</code>. Finally, a unified <code>Artifact</code> is
     * created, the target <code>Artifact</code>. Therefore, it should be considered by the merge implementation whether
     * the order of elements is significant or not.
     *
     * @param operation
     *         merge operation
     * @param context
     *         merge context
     */
    void merge(MergeOperation<T> operation, MergeContext context);
}
