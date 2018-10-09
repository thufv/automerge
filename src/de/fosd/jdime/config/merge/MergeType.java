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
package de.fosd.jdime.config.merge;

/**
 * @author Olaf Lessenich
 *
 */
public enum MergeType {

    /**
     * Two-way merge.
     */
    TWOWAY,

    /**
     * Three-way merge.
     */
    THREEWAY,

    /**
     * N-way merge.
     */
    NWAY;

    /**
     * At least two input files are needed.
     */
    public static final int TWOWAY_FILES = 2;
    public static final int THREEWAY_FILES = 3;
    public static final int MINFILES = TWOWAY_FILES;

    @Override
    public final String toString() {
        return this.name();
    }
}
