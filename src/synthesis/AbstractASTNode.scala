package synthesis

import de.fosd.jdime.artifact.ast.ASTNodeArtifact
import de.fosd.jdime.config.merge.Revision

abstract class AbstractASTNode(rev: String) extends ASTNodeArtifact(new Revision(rev)) {
  def dumpTree(sb: StringBuffer, indent: String): Unit = {
    sb.append(indent + toString + "\n")
  }
}

case class AbstractNode(name: String) extends AbstractASTNode("abstract.node") {

}

case class AbstractListNode(name: String) extends AbstractASTNode("abstract.list") {

}
