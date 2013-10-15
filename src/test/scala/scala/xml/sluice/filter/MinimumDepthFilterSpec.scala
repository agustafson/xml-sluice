package scala.xml.sluice.filter

import scala.xml.Node
import scala.xml.sluice._
import org.specs2.mutable.Specification

class MinimumDepthFilterSpec extends Specification {
  val multipleNodesUnderRoot =
    <root>
      <one/>
      <two/>
      <three/>
    </root>

  "MinimumDepthFilter" should {
    "read nodes at level 1 minimumDepth" in {
      val reader = new XmlElementReader(createXMLEventReader(multipleNodesUnderRoot))
        with MinimumDepthFilter { val minimumDepth = 1 }
      val nodes = reader.streamElements
      val names: Seq[String] = nodes map ((node: Node) => node.label)
      names.toList === Seq("one", "two", "three")
    }
  }
}
