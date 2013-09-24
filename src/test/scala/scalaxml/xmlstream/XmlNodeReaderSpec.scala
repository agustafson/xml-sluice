package scalaxml.xmlstream

import scalaxml.xmlstream.filter.IncludeAllFilter
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

  "XmlEventToNode" should {
    "convert a flat node" in {
      val reader = new XmlElementReader(createXMLEventReader(simpleFlatNode)) with IncludeAllFilter
      val node = reader.readElements.head
      node.label === "test"
    }

    "convert a simple node" in {
      val reader = new XmlElementReader(createXMLEventReader(singleElementNode)) with IncludeAllFilter
      val node = reader.readElements.head
      node.label === "test"
      node.text === "hi"
    }

    "convert a single nested node" in {
      val reader = new XmlElementReader(createXMLEventReader(singleNested)) with IncludeAllFilter
      val nodes = reader.readElements
      val parentNode = nodes \ "parent"
      (parentNode \ "@role").text === "father"
      (parentNode \ "name").text === "Gus"
      val childNode = parentNode \ "child"
      (childNode \ "@role").text === "daughter"
      childNode.text === "Elsa"
    }
  }
}
