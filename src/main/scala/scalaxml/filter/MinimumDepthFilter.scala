package scalaxml.filter

import scala.xml.pull._
import scalaxml.listener.DepthAwareListener

trait MinimumDepthFilter extends ElementStartEventFilter with DepthAwareListener {
  def minimumDepth: Int
  def includeNode: (EvElemStart) => Boolean = _ => currentDepth >= minimumDepth
}
