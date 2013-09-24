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
        preProcessEvent(event)
        event match {
          case event: EvElemStart =>
            val child = buildElement(event)
            postProcessEvent(event, Some(child))
            nodes += child
          case EvText(text) =>
            val child = Text(text)
            postProcessEvent(event, Some(child))
            nodes += child
          case EvElemEnd(prefix, label) =>
            if (prefix != elem.prefix || label != elem.label)
              throw new IllegalStateException(s"Current element ${elem.prefix}:${elem.label} had closing element $prefix:$label")
            val updatedElem = elem.copy(child = elem.child ++ nodes.toSeq)
            postProcessEvent(event, Some(updatedElem))
            return updatedElem
        }
      }
      throw new IllegalStateException(s"Found end of XML document without closing tag for ${elem.prefix}:${elem.label}")
    }

    if (!reader.hasNext) {
      Stream.empty
    } else {
      val nextEvent = reader.next()
      preProcessEvent(nextEvent)
      nextEvent match {
        case event: EvElemStart if includeNode(event) =>
          postProcessEvent(nextEvent, None)
          buildElement(event) #:: readElements
        case _ =>
          postProcessEvent(nextEvent, None)
          readElements
      }
    }
  }
  
  private def preProcessEvent(event: XMLEvent) {
    preProcessing.applyOrElse(event, (_:XMLEvent) => ())
  }

  private def postProcessEvent(event: XMLEvent, nodeResult: Option[Node]) {
    postProcessing.applyOrElse((event,nodeResult), (_:(XMLEvent,Option[Node])) => ())
  }

  private def startEventToElem(event: EvElemStart): Elem = {
    new Elem(event.pre, event.label, event.attrs, event.scope, minimizeEmpty)
  }

}
