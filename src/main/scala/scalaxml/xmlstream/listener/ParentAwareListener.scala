package scalaxml.xmlstream.listener

import scala.collection.mutable
import scala.xml.Node
import scala.xml.pull._

trait ParentAwareListener extends XmlEventListener with DepthAwareListener {
  private val parents = new mutable.Stack[String]

  def parent: Option[String] = {
    if (parents.isEmpty) None else Some(parents.top)
  }

  abstract override def preProcessing: XMLEvent => Unit = { event =>
    super.preProcessing(event)

    event match {
      case endEvent: EvElemEnd =>
        if (!parents.isEmpty)
          parents.pop()
      case _ =>
    }
  }

  abstract override def postProcessing: (XMLEvent,Option[Node]) => Unit = { case (event, nodeResult) =>
    super.postProcessing(event, nodeResult)

    event match {
      case EvElemStart(_,label,_,_) =>
        parents.push(label)
      case _ =>
    }
  }
}
