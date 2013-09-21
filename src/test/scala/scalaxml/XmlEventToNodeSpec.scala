package scalaxml

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.xml._
import scala.xml.pull._
import org.specs2.mutable.Specification

class XmlEventToNodeSpec extends Specification {
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
      val node = convertEventToNode.head
      node.label === "test"
    }

    "convert a simple node" in {
      implicit val reader = createReader(singleElementNode)
      val node = convertEventToNode.head
      node.label === "test"
      node.text === "hi"
    }

    "convert a single nested node" in {
      implicit val reader = createReader(singleNested)

      val nodes = convertEventToNode
      val parentNode = nodes \ "parent"
      (parentNode \ "@role").text === "father"
      (parentNode \ "name").text === "Gus"
      val childNode = parentNode \ "child"
      (childNode \ "@role").text === "daughter"
      childNode.text === "Elsa"

      true === true
    }
  }

  def createReader(xml: Elem): XMLEventReader = {
    new XMLEventReader(Source.fromString(xml.toString))
  }


  def convertEventToNode(implicit reader: XMLEventReader): Seq[Node] = {
    def convert(parent: Option[Elem]): Seq[Node] = {
      val nodes = ListBuffer[Node]()
      while (reader.hasNext) {
        reader.next match {
          case EvElemStart(prefix, label, attrs, scope) =>
            nodes ++= convert(Some(new Elem(prefix, label, attrs, scope, true)))
          case EvText(text) =>
            println(s"parent: $parent; text: $text")
            nodes += Text(text)
          case EvElemEnd(prefix, label) =>
            parent map { parentElem =>
              println(s"parent: $parent; nodes: $nodes")
              if (prefix != parentElem.prefix || label != parentElem.label)
                throw new IllegalStateException(s"Current element ${parentElem.prefix}:${parentElem.label} had closing element $prefix:$label")
              return parentElem.copy(child = parentElem.child ++ nodes.toSeq)
            } getOrElse { throw new IllegalStateException("Current parent is not set") }
        }
      }
      nodes.toSeq
    }
    convert(None)
  }
}
