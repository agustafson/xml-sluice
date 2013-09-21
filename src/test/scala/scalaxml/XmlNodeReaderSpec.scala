package scalaxml

import scala.io.Source
import scala.xml._
import scala.xml.pull._
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
      implicit val reader = createReader(simpleFlatNode)
      val node = reader.readNodes.head
      node.label === "test"
    }

    "convert a simple node" in {
      implicit val reader = createReader(singleElementNode)
      val node = reader.readNodes.head
      node.label === "test"
      node.text === "hi"
    }

    "convert a single nested node" in {
      implicit val reader = createReader(singleNested)

      val nodes = reader.readNodes
      val parentNode = nodes \ "parent"
      (parentNode \ "@role").text === "father"
      (parentNode \ "name").text === "Gus"
      val childNode = parentNode \ "child"
      (childNode \ "@role").text === "daughter"
      childNode.text === "Elsa"
    }
  }

  def createReader(xml: Elem): XmlNodeReader = {
    new XmlNodeReader(new XMLEventReader(Source.fromString(xml.toString)))
  }
}
