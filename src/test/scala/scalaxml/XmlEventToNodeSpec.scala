package scalaxml

import scala.io.Source
import scala.xml._
import scala.xml.pull._
import org.specs2.mutable.Specification

class XmlEventToNodeSpec extends Specification {
  val simpleFlatNode = <test/>
  val singleElementNode = <test>hi</test>

  "XmlEventToNode" should {
    "convert a flat node" in {
      implicit val reader = createReader(simpleFlatNode)
      val Some(node) = convertEventToNode
      node.label === "test"
    }

    "convert a simple node" in {
      implicit val reader = createReader(singleElementNode)
      val Some(node) = convertEventToNode
      node.label === "test"
      node.text === "hi"
    }
  }

  def createReader(xml: Elem): XMLEventReader = {
    new XMLEventReader(Source.fromString(xml.toString))
  }

  def convertEventToNode(implicit reader: XMLEventReader): Option[Node] = {
    var node: Option[Node] = None
    while (reader.hasNext) {
      reader.next match {
        case EvElemStart(prefix, label, attrs, scope) =>
          node = Some(new Elem(prefix, label, attrs, scope, true))
        case EvText(text) =>
          val textNode = Text(text)
          node = node.map(elem => new Elem(elem.prefix, elem.label, elem.attributes, elem.scope, true, (elem.child :+ textNode):_*))
        case EvElemEnd(prefix, label) =>
          node match {
            case None =>
              throw new IllegalStateException(s"Attempting to close element $prefix:$label but current element has not been set")
            case Some(elem) if prefix != elem.prefix || label != elem.label =>
              throw new IllegalStateException(s"Current element ${elem.prefix}:${elem.label} had closing element $prefix:$label")
            case _ =>
              return node
          }
      }
    }
    node
  }
}
