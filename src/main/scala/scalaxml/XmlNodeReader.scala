package scalaxml

import scala.xml._
import scala.xml.pull._

class XmlNodeReader(reader: XMLEventReader) { self: ElementStartEventFilter =>
  private[scalaxml] var currentDepth = 0
  private var startingNodeDepth: Option[Int] = None

  def readNodes: Seq[Node] = {
    def convertXmlEventsToNodes(parent: Option[Elem], children: Seq[Node]): Stream[Node] = {
      while (reader.hasNext) {
        reader.next match {

          case event @ EvElemStart(prefix, label, attrs, scope) =>
            val shouldIncludeNode = includeNode(event)
            startingNodeDepth = startingNodeDepth orElse { if (shouldIncludeNode) Some(currentDepth) else None }
            println(s"START $label currentDepth: $currentDepth; startingNodeDepth: $startingNodeDepth; shouldIncludeNode: $shouldIncludeNode")
            currentDepth = currentDepth + 1
            val (newParent,newChildren) = startingNodeDepth match {
              case Some(startingDepth) if currentDepth >= startingDepth =>
                (Some(new Elem(prefix, label, attrs, scope, true)), Nil)
              case _ =>
                (parent, children)
            }
            return convertXmlEventsToNodes(parent = newParent, children = newChildren)

          case EvText(text) =>
            val newChildren = startingNodeDepth match {
              case Some(startingDepth) if currentDepth >= startingDepth =>
                children :+ Text(text)
              case _ =>
                children
            }
            return convertXmlEventsToNodes(parent, newChildren)

          case EvElemEnd(prefix, label) =>
            currentDepth = currentDepth - 1
            println(s"END $label currentDepth: $currentDepth; startingNodeDepth: $startingNodeDepth")
            startingNodeDepth match {
              case Some(startingDepth) if currentDepth == startingDepth =>
                startingNodeDepth = None
                parent map { parentElem =>
                  println(s"parent: $parent; children: $children")
                  if (prefix != parentElem.prefix || label != parentElem.label)
                    throw new IllegalStateException(s"Current element ${parentElem.prefix}:${parentElem.label} had closing element $prefix:$label")
                  val newElem = parentElem.copy(child = parentElem.child ++ children)
                  println(s"Returning new element as head of stream: $newElem")
                  return newElem #:: convertXmlEventsToNodes(parent = None, children = Nil)
                } getOrElse { throw new IllegalStateException("No parent set") }
              case _ =>
                return convertXmlEventsToNodes(parent, children)
            }

          case _ =>
            return convertXmlEventsToNodes(parent, children)

        }
      }
      println(s"Returning result: empty stream with parent: $parent; children: $children")
      Stream.empty
    }
    convertXmlEventsToNodes(parent = None, children = Nil)
  }

}
