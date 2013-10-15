package scala.xml.sluice.listener

import scala.xml.Node
import scala.xml.pull._

trait DepthAwareListener extends XmlEventListener {
  var currentDepth = 0

  abstract override def preProcessing: XMLEvent => Unit = { event =>
    super.preProcessing(event)

    event match {
      case endEvent: EvElemEnd =>
        currentDepth = currentDepth - 1
      case _ =>
    }
  }

  abstract override def postProcessing: (XMLEvent,Option[Node]) => Unit = { case (event, nodeResult) =>
    super.postProcessing(event, nodeResult)

    event match {
      case startEvent: EvElemStart =>
        currentDepth = currentDepth + 1
      case _ =>
    }
  }
}
