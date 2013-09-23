package scalaxml.filter

import scala.xml.pull.EvElemStart

trait ElementStartEventFilter {
  def includeNode: EvElemStart => Boolean
}
