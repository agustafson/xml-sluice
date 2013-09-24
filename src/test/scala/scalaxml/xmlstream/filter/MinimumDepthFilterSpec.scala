package scalaxml.xmlstream.filter

import scala.xml.Node
import scalaxml.xmlstream.{XmlNodeReader, XmlNodeReaderCreator}
import org.specs2.mutable.Specification

class MinimumDepthFilterSpec extends Specification with XmlNodeReaderCreator {
  val multipleNodesUnderRoot =
    <root>
      <one/>
      <two/>
      <three/>
    </root>

  "MinimumDepthFilter" should {
    "read nodes at level 1 minimumDepth" in {
      val reader = new XmlNodeReader(createXMLEventReader(multipleNodesUnderRoot))
        with MinimumDepthFilter { val minimumDepth = 1 }
      val nodes = reader.readNodes
      val names: Seq[String] = nodes map ((node: Node) => node.label)
      names === Seq("one", "two", "three")
    }
  }
}
