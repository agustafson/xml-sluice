package scala.xml.sluice.filter

import scala.xml.pull.EvElemStart

trait ElementStartEventFilter {
  def includeNode: EvElemStart => Boolean
}
