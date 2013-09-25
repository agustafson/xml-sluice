package scalaxml.xmlstream

import scala.collection.mutable.ListBuffer
import scala.xml._
import scala.xml.pull._
import scalaxml.xmlstream.filter.ElementStartEventFilter
import scalaxml.xmlstream.listener.XmlEventListener

class XmlElementReader(reader: XMLEventReader, minimizeEmpty: Boolean = true) extends XmlEventListener { self: ElementStartEventFilter =>

  def readElements: Stream[Elem] = {
    def buildElement(startEvent: EvElemStart): Elem = {
      val elem = startEventToElem(startEvent)
      val nodes = ListBuffer[Node]()
      while (reader.hasNext) {
        val event = reader.next()
        preProcessing(event)
        event match {
          case event: EvElemStart =>
            val child = buildElement(event)
            postProcessing(event, Some(child))
            nodes += child
          case EvText(text) =>
            val child = Text(text)
            postProcessing(event, Some(child))
            nodes += child
          case EvElemEnd(prefix, label) =>
            if (prefix != elem.prefix || label != elem.label)
              throw new IllegalStateException(s"Current element ${elem.prefix}:${elem.label} had closing element $prefix:$label")
            val updatedElem = elem.copy(child = elem.child ++ nodes.toSeq)
            postProcessing(event, Some(updatedElem))
            return updatedElem
        }
      }
      throw new IllegalStateException(s"Found end of XML document without closing tag for ${elem.prefix}:${elem.label}")
    }

    if (!reader.hasNext) {
      Stream.empty
    } else {
      val nextEvent = reader.next()
      preProcessing(nextEvent)
      nextEvent match {
        case event: EvElemStart if includeNode(event) =>
          postProcessing(nextEvent, None)
          buildElement(event) #:: readElements
        case _ =>
          postProcessing(nextEvent, None)
          readElements
      }
    }
  }

  def includeNode: (EvElemStart) => Boolean = _ => true

  private def startEventToElem(event: EvElemStart): Elem = {
    new Elem(event.pre, event.label, event.attrs, event.scope, minimizeEmpty)
  }

}
