package scalaxml

import scalaxml.filter.IncludeAllElementStartEventFilter
import org.specs2.mutable.Specification

class XmlNodeReaderSpec extends Specification with XmlNodeReaderCreator {
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
  }
}
