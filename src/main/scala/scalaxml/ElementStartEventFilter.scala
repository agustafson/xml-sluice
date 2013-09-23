package scalaxml

import scala.xml.pull._

trait ElementStartEventFilter { self: XmlNodeReader =>
  def includeNode: EvElemStart => Boolean
}

trait IncludeAllElementStartEventFilter extends ElementStartEventFilter { self: XmlNodeReader =>
  def includeNode: (EvElemStart) => Boolean = _ => true
}

trait DepthBasedElementStartEventFilter extends ElementStartEventFilter { self: XmlNodeReader =>
  def depth: Int
  def includeNode: (EvElemStart) => Boolean = _ => depth > 0
}
