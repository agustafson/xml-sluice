package scalaxml.xmlstream

import scala.annotation.tailrec
import scala.collection.immutable.Stack
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
      val startElemEvent = nextEvent match {
        case event: EvElemStart if includeNode(event) =>
          Some(event)
        case _ =>
          None
      }
      postProcessing(nextEvent, None)

      startElemEvent map { event =>
        buildElement(Stack(startEventToElem(event)), Seq.empty) #:: readElements
      } getOrElse {
        readElements
      }
    }
  }

  def includeNode: (EvElemStart) => Boolean = _ => true

  @tailrec
  private def buildElement(parents: Stack[Elem], nodes: Seq[Node]): Elem = {
    def currentParent: Option[Elem] = if (parents.isEmpty) None else Some(parents.top)

    def convertToChildNode(event: XMLEvent): Node = {
      processEvent(event) {
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

    if (reader.hasNext) {
      val event = reader.next()
      event match {
        case startEvent: EvElemStart =>
          val (currentParent,remainingParents) = parents.pop2
          val newParents = remainingParents.push(currentParent.copy(child = currentParent.child ++ nodes))
          val elem = processEvent(startEvent)(startEventToElem)
          buildElement(newParents.push(elem), Seq.empty)
        case EvElemEnd(prefix, label) =>
          val (currentParent,remainingParents) = parents.pop2
          val updatedElem = processEvent(event) { _ =>
            if (prefix != currentParent.prefix || label != currentParent.label)
              throw new IllegalStateException(s"Current element ${currentParent.prefix}:${currentParent.label} had closing element $prefix:$label")
            currentParent.copy(child = currentParent.child ++ nodes)
          }
          if (remainingParents.isEmpty)
            updatedElem
          else
            buildElement(remainingParents, Seq(updatedElem))
        case _ =>
          buildElement(parents, nodes :+ convertToChildNode(event))
      }
    } else if (parents.size == 1) {
      parents.top
    } else {
      val elemDetails = currentParent map(elem => s"${elem.prefix}:${elem.label}") getOrElse "<no parent found>"
      throw new IllegalStateException(s"Found end of XML document without closing tag for $elemDetails")
    }
  }

  private def processEvent[N <: Node, E <: XMLEvent](event: E)(eventHandler: E => N): N = {
    preProcessing(event)
    val result = eventHandler(event)
    postProcessing(event, Some(result))
    result
  }

  private def startEventToElem(event: EvElemStart): Elem = {
    new Elem(event.pre, event.label, event.attrs, event.scope, minimizeEmpty)
  }

}
