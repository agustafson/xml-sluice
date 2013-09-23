package scalaxml.filter

import scala.xml.pull.EvElemStart

trait LabelElementStartEventFilter extends ElementStartEventFilter {
  def labels: Seq[String]

  def includeNode: (EvElemStart) => Boolean = event => labels.contains(event.label)
}
