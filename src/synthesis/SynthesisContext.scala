package synthesis

import java.util.logging.{Level, Logger}

import de.fosd.jdime.artifact.ArtifactList
import de.fosd.jdime.artifact.ast.ASTNodeArtifact
import de.fosd.jdime.config.merge.Revision

import scala.collection.{JavaConverters, mutable}
import scala.util.{Left => Found, Right => NotFound}

class SynthesisContext(val left: ASTNodeArtifact, val right: ASTNodeArtifact,
                       val base: ASTNodeArtifact, val LOG: Logger, val maxDepth: Int = 2) {
  left.setRoot()
  right.setRoot()

  abstract class Symbol {
    def programs: Stream[ASTNodeArtifact]

    def uniqueID: String

    override def toString: String = uniqueID

    override def hashCode(): Int = uniqueID.hashCode

    override def equals(obj: scala.Any): Boolean = obj match {
      case that: Symbol => uniqueID == that.uniqueID
      case _ => false
    }
  }

  case class ASTNodeArtifactList(list: List[ASTNodeArtifact]) extends ASTNodeArtifact(new Revision("list"))

  def ASTNodeArtifactList(elem: ASTNodeArtifact): ASTNodeArtifactList = ASTNodeArtifactList(List(elem))

  def createArtifactList(list: List[ASTNodeArtifact]): ArtifactList[ASTNodeArtifact] =
    new ArtifactList[ASTNodeArtifact](JavaConverters.asJavaCollection(list))

  case class NonTerm(name: String) extends Symbol with Ordered[NonTerm] {
    lazy val programs: Stream[ASTNodeArtifact] = {

      val right = grammar(this)
      val symbols = right.symbols.toList
      val subPrograms = symbols.map(_.programs)

      if (!right.isList) subPrograms.reduceLeft(_ #::: _)
      else symbols.length match {
        case 1 => subPrograms.head.map(ASTNodeArtifactList)
        case 2 =>
          val p12 = for {
            s1 <- subPrograms.head
            s2 <- subPrograms(1)
          } yield ASTNodeArtifactList(List(s1, s2))
          val p0 = subPrograms.reduceLeft(_ #::: _)
          p12 #::: p0
        case 3 =>
          val p123 = for {
            s1 <- subPrograms.head
            s2 <- subPrograms(1)
            s3 <- subPrograms(2)
          } yield ASTNodeArtifactList(List(s1, s2, s3))
          val p12 = for {
            s1 <- subPrograms.head
            s2 <- subPrograms(1)
          } yield ASTNodeArtifactList(List(s1, s2))
          val p13 = for {
            s1 <- subPrograms.head
            s3 <- subPrograms(2)
          } yield ASTNodeArtifactList(List(s1, s3))
          val p0 = subPrograms.reduceLeft(_ #::: _)
          p12 #::: p13 #::: p123 #::: p0
        case k =>
          throw new RuntimeException(s"List length too long: $k")
      }
    }

    override def compare(that: NonTerm): Int = name compare that.name

    override def uniqueID: String = s"<$name>"
  }

  abstract class Term extends Symbol

  case class Concrete(tree: ASTNodeArtifact) extends Term {
    lazy val programs: Stream[ASTNodeArtifact] = {
      Stream(tree)
    }

    override def uniqueID: String = s"${tree.getId}:${tree.dumpString}"
  }

  case class Abstract(constructor: ArtifactList[ASTNodeArtifact] => ASTNodeArtifact,
                      name: String, children: Array[NonTerm]) extends Term {
    lazy val programs: Stream[ASTNodeArtifact] = {
      val programsOfArgs = children.map(_.programs).toList
      val argGroups = permutation(programsOfArgs)
      argGroups
        .map {
          case ASTNodeArtifactList(list) :: Nil => createArtifactList(list)
          case group => createArtifactList(group)
        }
        .map(constructor)
    }

    override def uniqueID: String = s"$name(${children.mkString(", ")})"
  }

  class Grammar {

    class RightHand(val symbols: mutable.ListBuffer[Symbol] = new mutable.ListBuffer[Symbol]) {
      private var listMode: Boolean = false

      def isList: Boolean = listMode

      def insert(listModeOn: Boolean = false, symbol: Symbol): Unit = {
        if (listModeOn) symbols += symbol
        else if (!symbols.contains(symbol)) symbols += symbol

        if (listModeOn && !isList) listMode = true
      }

      def sortWith(lt: (Symbol, Symbol) => Boolean): RightHand = {
        if (isList) this
        else new RightHand(symbols.sortWith(lt))
      }
    }

    private val map: mutable.HashMap[NonTerm, RightHand] = new mutable.HashMap[NonTerm, RightHand]

    def get(nt: NonTerm): Option[RightHand] = map.get(nt)

    def apply(nt: NonTerm): RightHand = map.apply(nt)

    def insert(listMode: Boolean = false, nt: NonTerm, symbol: Symbol): Unit = {
      map.get(nt) match {
        case Some(right) => right.insert(listMode, symbol)
        case None =>
          val right = new RightHand
          map.put(nt, right)
          right.insert(listMode, symbol)
      }
    }

    def sortWith(lt: (Symbol, Symbol) => Boolean): Unit = map.keys.foreach {
      k =>
        val sorted = map(k).sortWith(lt)
        map(k) = sorted
    }

    def prettyPrint: String = map.keys.toList.sorted.map {
      k =>
        val symbols = map(k).symbols
        val count = if (map(k).isList) s"[${symbols.size}]" else s"(${symbols.size})"

        s"$k -> $count\n${symbols.map("  " + _).mkString("\n")}"
    }.mkString("\n")
  }

  private val grammar: Grammar = new Grammar
  private val labels: Counter[Symbol, Label] = new Counter[Symbol, Label]
  private val on: Boolean = true

  def visitTree(tree: ASTNodeArtifact, parent: NonTerm, label: Label,
                depth: Int = 0, listMode: Boolean = false): Unit = {
    if (tree.isLeafNode || depth >= maxDepth) {
      val node = Concrete(tree)
      if (label != Base) {
        grammar.insert(listMode, parent, node)
      }
      labels.insert(node, label)
    } else {
      val args = tree.abstractArgumentNames(parent.name)
      val children = args.map(NonTerm)
      val node = Abstract(list => {
        val t = tree.copy()
        t.clearChildren()
        t.setChildren(list)
        t
      }, tree.kind(parent.name), children)

      if (label != Base) {
        grammar.insert(listMode, parent, node)
      }
      labels.insert(node, label)

      if (tree.isList) tree.getChildren.forEach {
        t => visitTree(t, children(0), label, depth, on)
      } else {
        JavaConverters.asScalaIterator(tree.getChildren.iterator).zip(children.iterator).foreach {
          case (t, p) =>
            visitTree(t, p, label, depth + 1)
        }
      }
    }
  }

  /**
    * Find all permutations for List[ Stream[T] ] = List(xs1, xs2, ...)
    * i.e. for {
    * x1 <- xs1
    * x2 <- xs2
    * ...
    * } yield Stream(List(x1, x2, ...))
    *
    * @tparam T element type.
    * @return permutations.
    */
  def permutation[T]: List[Stream[T]] => Stream[List[T]] = {
    case Nil => Stream(Nil)
    case xs :: Nil => xs.map(List(_))
    case xs :: ys :: Nil => for {
      x <- xs
      y <- ys
    } yield List(x, y)
    case xs :: yss => for {
      x <- xs
      ys <- permutation(yss)
    } yield x :: ys
  }

  lazy val programs: Stream[ASTNodeArtifact] = {
    val start = NonTerm("start")

    visitTree(base, start, Base)
    visitTree(left, start, Left)
    visitTree(right, start, Right)

    def lt(s1: Set[Label], s2: Set[Label]): Boolean = {
      val swap = (s2 == Set(Left) && s1.contains(Base) && s1.contains(Right)) ||
        (s2 == Set(Right) && s1.contains(Base) && s1.contains(Left)) ||
        (s2 == Set(Left, Right) && s1.contains(Base))
      !swap
    }

    grammar.sortWith {
      case (l, r) =>
        val label1 = labels(l)
        val label2 = labels(r)
        lt(label1, label2)
    }

    LOG.finer("Grammar:")
    if (LOG.isLoggable(Level.FINER)) println(grammar.prettyPrint)
    LOG.finer("Labels:")
    if (LOG.isLoggable(Level.FINER)) println(labels.prettyPrint)

    start.programs
  }

  def take(k: Int): Unit = {
    programs.take(k).zipWithIndex.foreach {
      case (x, i) =>
        LOG.fine(s"Synthesis: Candidate ${i + 1}:\n${x.prettyPrint}")
    }
  }

  def check(expected: ASTNodeArtifact, maxk: Int = 32): javafx.util.Pair[java.lang.Boolean, Integer] = {
    val exp = expected

    def loop(ps: Stream[ASTNodeArtifact], k: Int = 1): Either[Int, Int] = {
      if (ps.isEmpty || k > maxk) NotFound(Math.min(k, maxk))
      else {
        val p = ps.head
        LOG.fine(s"Synthesis: Check $k:\n${p.prettyPrint}")
        if (p.eq(exp)) Found(k)
        else loop(ps.tail, k + 1)
      }
    }

    loop(programs) match {
      case Found(k) => new javafx.util.Pair[java.lang.Boolean, Integer](true, k)
      case NotFound(k) => new javafx.util.Pair[java.lang.Boolean, Integer](false, k)
    }
  }
}
