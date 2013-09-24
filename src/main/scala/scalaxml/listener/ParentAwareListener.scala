package scalaxml.listener

import scala.collection.mutable
import scala.xml.Node
import scala.xml.pull._

trait ParentAwareListener extends XmlEventListener with DepthAwareListener {
  private val parents = new mutable.Stack[String]

  def parent: Option[String] = {
    if (parents.isEmpty) None else Some(parents.top)
  }

  override def preProcessing: PartialFunction[XMLEvent, Unit] = {
    case event @ EvElemEnd(_,label) =>
      super.preProcessing(event)
      parents.pop()
  }

  override def postProcessing: PartialFunction[(XMLEvent, Option[Node]), Unit] = {
    case (event @ EvElemStart(_,label,_,_), nodeResult) =>
      super.postProcessing(event, nodeResult)
      parents.push(label)
  }
}
