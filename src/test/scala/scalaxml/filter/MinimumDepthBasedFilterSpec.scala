package scalaxml.filter

import scala.xml.Node
import scalaxml.{XmlNodeReader, XmlNodeReaderCreator}
import org.specs2.mutable.Specification

class MinimumDepthBasedFilterSpec extends Specification with XmlNodeReaderCreator {
  val multipleNodesUnderRoot =
    <root>
      <one/>
      <two/>
      <three/>
    </root>

  "MinimumDepthBasedFilter" should {
    "read nodes at level 1 minimumDepth" in {
      val reader = new XmlNodeReader(createXMLEventReader(multipleNodesUnderRoot)) with MinimumDepthBasedFilter {
        val minimumDepth = 1
      }
      val nodes = reader.readNodes
      val names: Seq[String] = nodes map ((node: Node) => node.label)
      names === Seq("one", "two", "three")
    }
  }
}
