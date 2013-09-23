package scalaxml.filter

import scala.xml.pull.EvElemStart

trait IncludeAllElementStartEventFilter extends ElementStartEventFilter {
  def includeNode: EvElemStart => Boolean = _ => true
}
