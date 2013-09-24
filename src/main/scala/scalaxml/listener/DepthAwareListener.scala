package scalaxml.listener

import scala.xml.Node
import scala.xml.pull._

trait DepthAwareListener extends XmlEventListener {
  var currentDepth = 0

  override def preProcessing: PartialFunction[XMLEvent, Unit] = {
    case event: EvElemEnd =>
      currentDepth = currentDepth - 1
  }

  override def postProcessing: PartialFunction[(XMLEvent,Option[Node]), Unit] = {
    case (event: EvElemStart,_) =>
      currentDepth = currentDepth + 1
  }
}
