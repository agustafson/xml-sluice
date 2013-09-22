package scalaxml

import scala.collection.mutable.ListBuffer
import scala.xml._
import scala.xml.pull._

class XmlNodeReader(reader: XMLEventReader) {
  private val parents = collection.mutable.Stack[Elem]()
  private[scalaxml] def currentDepth: Int = parents.size

  def readNodes: Seq[Node] = {
    def convertXmlEventsToNodes: Seq[Node] = {
      val nodes = ListBuffer[Node]()
      while (reader.hasNext) {
        reader.next match {
          case EvElemStart(prefix, label, attrs, scope) =>
            val elem = new Elem(prefix, label, attrs, scope, true)
            parents.push(elem)
            nodes ++= convertXmlEventsToNodes
          case EvText(text) =>
            println(s"parents: $parents; text: $text")
            nodes += Text(text)
          case EvElemEnd(prefix, label) =>
            val parent = if (parents.isEmpty) None else Some(parents.pop())
            parent map { parentElem =>
              println(s"parents: $parents; nodes: $nodes")
              if (prefix != parentElem.prefix || label != parentElem.label)
                throw new IllegalStateException(s"Current element ${parentElem.prefix}:${parentElem.label} had closing element $prefix:$label")
              return parentElem.copy(child = parentElem.child ++ nodes.toSeq)
            } getOrElse { throw new IllegalStateException("Current parent is not set") }
        }
      }
      nodes.toSeq
    }
    convertXmlEventsToNodes
  }

}
