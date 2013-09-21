package scalaxml

import scala.collection.mutable.ListBuffer
import scala.xml._
import scala.xml.pull._

class XmlNodeReader(reader: XMLEventReader) {

  def readNodes: Seq[Node] = {
    def convertXmlEventsToNodes(parent: Option[Elem]): Seq[Node] = {
      val nodes = ListBuffer[Node]()
      while (reader.hasNext) {
        reader.next match {
          case EvElemStart(prefix, label, attrs, scope) =>
            nodes ++= convertXmlEventsToNodes(Some(new Elem(prefix, label, attrs, scope, true)))
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
    convertXmlEventsToNodes(None)
  }

}
