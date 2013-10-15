package scala.xml.sluice.filter

import scala.xml.pull.EvElemStart

trait LabelFilter extends ElementStartEventFilter {
  def labels: Set[String]

  abstract override def includeNode: (EvElemStart) => Boolean = { event =>
    super.includeNode(event) && labels.contains(event.label)
  }
}
