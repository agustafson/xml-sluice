package scalaxml.xmlstream.filter

import scala.xml.pull.EvElemStart
import scalaxml.xmlstream.listener.ParentAwareListener

trait ParentNameFilter extends ElementStartEventFilter with ParentAwareListener {
  def parentNames: Set[String]

  def includeNode: (EvElemStart) => Boolean = { event =>
    parent exists (parentNames contains _)
  }
}
