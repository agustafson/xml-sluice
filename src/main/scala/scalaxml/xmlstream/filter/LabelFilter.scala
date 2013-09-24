package scalaxml.xmlstream.filter

import scala.xml.pull.EvElemStart

trait LabelFilter extends ElementStartEventFilter {
  def labels: Seq[String]

  abstract override def includeNode: (EvElemStart) => Boolean = { event =>
    super.includeNode(event) && labels.contains(event.label)
  }
}