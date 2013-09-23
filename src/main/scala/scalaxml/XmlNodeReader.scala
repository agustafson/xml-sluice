package scalaxml

import scala.collection.mutable.ListBuffer
import scala.xml._
import scala.xml.pull._

class XmlNodeReader(reader: XMLEventReader, minimizeEmpty: Boolean = true) { self: ElementStartEventFilter =>

  def readNodes: Stream[Node] = {
    def buildElement(elem: Elem): Elem = {
      val nodes = ListBuffer[Node]()
      while (reader.hasNext) {
        reader.next() match {
          case event: EvElemStart =>
            val child = buildElement(startEventToElem(event))
            println(s"elem: $elem; nodes: $nodes; child: $child")
            nodes += child
          case EvText(text) =>
            println(s"elem: $elem; nodes: $nodes; text: $text")
            nodes += Text(text)
          case EvElemEnd(prefix, label) =>
            println(s"END $label; elem: $elem; nodes: $nodes")
            if (prefix != elem.prefix || label != elem.label)
              throw new IllegalStateException(s"Current element ${elem.prefix}:${elem.label} had closing element $prefix:$label")
            return elem.copy(child = elem.child ++ nodes.toSeq)
        }
      }
      throw new IllegalStateException(s"Found end of XML document without closing tag for ${elem.prefix}:${elem.label}")
    }

    if (!reader.hasNext) {
      Stream.empty[Node]
    } else {
      val nextEvent = reader.next()
      nextEvent match {
        case event: EvElemStart =>
          val elem = startEventToElem(event)
          buildElement(elem) #:: readNodes
      }
    }
  }

  private def startEventToElem(event: EvElemStart): Elem = {
    new Elem(event.pre, event.label, event.attrs, event.scope, minimizeEmpty)
  }

}
