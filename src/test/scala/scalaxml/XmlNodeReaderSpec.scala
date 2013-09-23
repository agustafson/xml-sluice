package scalaxml

import scala.io.Source
import scala.xml._
import scala.xml.pull._
import org.specs2.mutable.Specification
import scalaxml.filter.{IncludeAllElementStartEventFilter, DepthBasedElementStartEventFilter}

class XmlNodeReaderSpec extends Specification {
  val simpleFlatNode = <test/>
  val singleElementNode = <test>hi</test>
  val singleNested =
    <root>
      <parent role="father">
        <name>Gus</name>
        <child role="daughter">Elsa</child>
      </parent>
    </root>
  val multipleNodesUnderRoot =
    <root>
      <one/>
      <two/>
      <three/>
    </root>

  "XmlEventToNode" should {
    "convert a flat node" in {
      val reader = new XmlNodeReader(createXMLEventReader(simpleFlatNode)) with IncludeAllElementStartEventFilter
      val node = reader.readNodes.head
      node.label === "test"
    }

    "convert a simple node" in {
      val reader = new XmlNodeReader(createXMLEventReader(singleElementNode)) with IncludeAllElementStartEventFilter
      val node = reader.readNodes.head
      node.label === "test"
      node.text === "hi"
    }

    "convert a single nested node" in {
      val reader = new XmlNodeReader(createXMLEventReader(singleNested)) with IncludeAllElementStartEventFilter
      val nodes = reader.readNodes
      val parentNode = nodes \ "parent"
      (parentNode \ "@role").text === "father"
      (parentNode \ "name").text === "Gus"
      val childNode = parentNode \ "child"
      (childNode \ "@role").text === "daughter"
      childNode.text === "Elsa"
    }
    
    "read nodes at level 1 depth" in {
      val reader = new XmlNodeReader(createXMLEventReader(multipleNodesUnderRoot)) with DepthBasedElementStartEventFilter {
        val depth = 1
      }
      val nodes = reader.readNodes
      val names: Seq[String] = nodes map ((node: Node) => node.label)
      names === Seq("one", "two", "three")
    }
  }

  def createXMLEventReader(xml: Elem): XMLEventReader = {
    new XMLEventReader(Source.fromString(xml.toString))
  }
}
