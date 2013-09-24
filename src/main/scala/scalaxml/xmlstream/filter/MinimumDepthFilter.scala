package scalaxml.xmlstream.filter

import scala.xml.pull._
import scalaxml.xmlstream.listener.DepthAwareListener

trait MinimumDepthFilter extends ElementStartEventFilter with DepthAwareListener {
  def minimumDepth: Int

  abstract override def includeNode: (EvElemStart) => Boolean = { event =>
    super.includeNode(event) && currentDepth >= minimumDepth
  }
}
