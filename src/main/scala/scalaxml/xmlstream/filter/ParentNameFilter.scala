package scalaxml.xmlstream.filter

import scala.xml.pull.EvElemStart
import scalaxml.xmlstream.listener.ParentAwareListener

trait ParentNameFilter extends ElementStartEventFilter with ParentAwareListener {
  def parentNames: Set[String]

  abstract override def includeNode: (EvElemStart) => Boolean = { event =>
    super.includeNode(event) && parent.exists(parentNames contains _)
  }
}
