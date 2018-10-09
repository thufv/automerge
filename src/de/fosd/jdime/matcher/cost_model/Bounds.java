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
package de.fosd.jdime.matcher.cost_model;

import java.util.Comparator;

import static java.util.Comparator.comparing;

/**
 * An interval bounded by two floats [lower, upper].
 */
final class Bounds {

    static final Comparator<Bounds> BY_MIDDLE = comparing(Bounds::middle);
    static final Comparator<Bounds> BY_LOWER_UPPER = comparing(Bounds::getLower).thenComparing(Bounds::getUpper);

    private float lower;
    private float upper;

    /**
     * Constructs new instance with the given bounds.
     *
     * @param lower
     *         the lower bound
     * @param upper
     *         the upper bound
     */
    Bounds(float lower, float upper) {
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * Returns the lower bound.
     *
     * @return the lower bound
     */
    public float getLower() {
        return lower;
    }

    /**
     * Sets the lower bound.
     *
     * @param lower
     *         the new lower bound
     */
    public void setLower(float lower) {
        this.lower = lower;
    }

    /**
     * Returns the upper bound.
     *
     * @return the upper bound
     */
    public float getUpper() {
        return upper;
    }

    /**
     * Sets the upper bound.
     *
     * @param upper
     *         the new upper bound
     */
    public void setUpper(float upper) {
        this.upper = upper;
    }

    /**
     * Returns the middle of the interval.
     *
     * @return the middle of the interval
     */
    private float middle() {
        return lower + (upper - lower) / 2;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", lower, upper);
    }
}
