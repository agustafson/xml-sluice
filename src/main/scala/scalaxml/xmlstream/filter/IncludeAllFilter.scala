package scalaxml.xmlstream.filter

import scala.xml.pull.EvElemStart

trait IncludeAllFilter extends ElementStartEventFilter {
  def includeNode: EvElemStart => Boolean = _ => true
}
