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
