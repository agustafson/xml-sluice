package scalaxml.xmlstream

import scalaxml.xmlstream.filter.{MinimumDepthFilter, IncludeAllFilter}
import org.specs2.mutable.Specification

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
  val multipleRows =
    <root>
      <row>
        <id>1</id>
        <name>a</name>
      </row>
      <row>
        <id>2</id>
        <name>b</name>
      </row>
    </root>

  "XmlEventToNode" should {
    "convert a flat node" in {
      val reader = new XmlElementReader(createXMLEventReader(simpleFlatNode)) with IncludeAllFilter
      val node = reader.streamElements.head
      node.label === "test"
    }

    "convert a simple node" in {
      val reader = new XmlElementReader(createXMLEventReader(singleElementNode)) with IncludeAllFilter
      val node = reader.streamElements.head
      node.label === "test"
      node.text === "hi"
    }

    "convert a single nested node" in {
      val reader = new XmlElementReader(createXMLEventReader(singleNested)) with IncludeAllFilter
      val nodes = reader.streamElements
      val parentNode = nodes \ "parent"
      (parentNode \ "@role").text === "father"
      (parentNode \ "name").text === "Gus"
      val childNode = parentNode \ "child"
      (childNode \ "@role").text === "daughter"
      childNode.text === "Elsa"
    }

    "read all nodes" in {
      val reader1 = new XmlElementReader(createXMLEventReader(singleNested)) with IncludeAllFilter
      val reader2 = new XmlElementReader(createXMLEventReader(singleNested)) with IncludeAllFilter
      reader1.streamElements.toList === reader2.readElements
    }

    "read multiple rows" in {
      val reader = new XmlElementReader(createXMLEventReader(multipleRows)) with MinimumDepthFilter {
        val minimumDepth = 1
      }
      reader.readElements.size === 2
    }
  }
}
