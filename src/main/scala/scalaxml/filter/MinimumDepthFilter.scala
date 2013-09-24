package scalaxml.filter

import scala.xml.Node
import scala.xml.pull._
import scalaxml.listener.XmlEventListener

trait MinimumDepthFilter extends ElementStartEventFilter with XmlEventListener {
  def minimumDepth: Int
  def includeNode: (EvElemStart) => Boolean = _ => currentDepth >= minimumDepth

  var currentDepth = 0

  override def preProcessing: (XMLEvent) => Unit = {
    case event: EvElemEnd =>
      currentDepth = currentDepth - 1
    case _ =>
  }

  override def postProcessing: (XMLEvent,Option[Node]) => Unit = {
    case (event: EvElemStart,_) =>
      currentDepth = currentDepth + 1
    case _ =>
  }
}
