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
package synthesis

import scala.collection.mutable

class Counter[K, V] extends mutable.HashMap[K, Set[V]] {
  def insert(key: K, value: V): Unit = {
    get(key) match {
      case Some(vs) => super.put(key, vs + value)
      case None => super.put(key, Set(value))
    }
  }

  def prettyPrint: String = keys.map {
    k =>
      val vs = this (k)
      s"$k -> ${vs.mkString("")}"
  }.mkString("\n")
}
