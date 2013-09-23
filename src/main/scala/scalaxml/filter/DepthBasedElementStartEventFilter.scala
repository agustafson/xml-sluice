package scalaxml.filter

import scala.xml.pull._
import scalaxml.listener.XmlEventListener

trait DepthBasedElementStartEventFilter extends ElementStartEventFilter with XmlEventListener {
  def depth: Int
  def includeNode: (EvElemStart) => Boolean = _ => currentDepth >= depth

  var currentDepth = 0

  override def preProcessing: (XMLEvent) => Unit = {
    case event: EvElemEnd =>
      currentDepth = currentDepth - 1
    case _ =>
  }

  override def postProcessing[T]: (XMLEvent,Option[T]) => Unit = {
    case (event: EvElemStart,_) =>
      currentDepth = currentDepth + 1
    case _ =>
  }
}
