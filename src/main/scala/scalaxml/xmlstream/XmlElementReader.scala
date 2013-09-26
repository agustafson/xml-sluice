package scalaxml.xmlstream

import scala.collection.mutable.ListBuffer
import scala.xml._
import scala.xml.pull._
import scalaxml.xmlstream.filter.ElementStartEventFilter
import scalaxml.xmlstream.listener.XmlEventListener

class XmlElementReader(reader: XMLEventReader, minimizeEmpty: Boolean = true) extends XmlEventListener { self: ElementStartEventFilter =>

  def readElements: Stream[Elem] = {
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

  private def buildElement(startEvent: EvElemStart): Elem = {
    def convertToChildNode(event: XMLEvent): Node = {
      processEvent(event) {
        case event: EvElemStart =>
          buildElement(event)
        case EvText(text) =>
          Text(text)
        case EvComment(commentText) =>
          if (commentText contains "apos")
            Text("'")
          else
            Comment(commentText)
        case EvEntityRef(entityName) =>
          EntityRef(entityName)
        case EvProcInstr(target, text) =>
          ProcInstr(target, text)
      }
    }

    val elem = startEventToElem(startEvent)
    val nodes = ListBuffer[Node]()
    while (reader.hasNext) {
      val event = reader.next()
      event match {
        case EvElemEnd(prefix, label) =>
          val updatedElem = processEvent[Elem](event) { _ =>
            if (prefix != elem.prefix || label != elem.label)
              throw new IllegalStateException(s"Current element ${elem.prefix}:${elem.label} had closing element $prefix:$label")
            elem.copy(child = elem.child ++ nodes.toSeq)
          }
          return updatedElem
        case _ =>
          nodes += convertToChildNode(event)
      }
    }
    throw new IllegalStateException(s"Found end of XML document without closing tag for ${elem.prefix}:${elem.label}")
  }

  private def processEvent[N <: Node](event: XMLEvent)(eventHandler: XMLEvent => N): N = {
    preProcessing(event)
    val result = eventHandler(event)
    postProcessing(event, Some(result))
    result
  }

  private def startEventToElem(event: EvElemStart): Elem = {
    new Elem(event.pre, event.label, event.attrs, event.scope, minimizeEmpty)
  }

}
