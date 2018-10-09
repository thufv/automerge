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
package de.fosd.jdime.matcher.matching;

/**
 * @author Olaf Lessenich
 *
 */
public enum Color {

    /**
     *
     */
    GREEN {
        @Override
        public String toShell() {
            return "\033[32m";
        }

        @Override
        public String toGraphViz() {
            return "green";
        }
    },
    /**
     *
     */
    BLUE {
        @Override
        public String toShell() {
            return "\033[34m";
        }

        @Override
        public String toGraphViz() {
            return "blue";
        }
    },
    /**
     *
     */
    YELLOW {
        @Override
        public String toShell() {
            return "\033[33m";
        }

        @Override
        public String toGraphViz() {
            return "yellow";
        }
    },
    /**
     *
     */
    RED {
        @Override
        public String toShell() {
            return "\033[31m";
        }

        @Override
        public String toGraphViz() {
            return "red";
        }
    },
    /**
     *
     */
    DEFAULT {
        @Override
        public String toShell() {
            return "\033[0m";
        }

        @Override
        public String toGraphViz() {
            return "white";
        }
    };

    /**
     * Returns a String representation that can be interpreted by terminals.
     *
     * @return String representation to be used in terminals
     */
    public abstract String toShell();

    /**
     * Returns a String representation that can be interpreted by GraphViz.
     *
     * @return String representation that can be interpreted by GraphViz
     */
    public abstract String toGraphViz();
}
