package scalaxml.xmlstream.listener

import scala.xml.Node
import scala.xml.pull.XMLEvent

trait XmlEventListener {
  def preProcessing: PartialFunction[XMLEvent, Unit] = PartialFunction.empty

  def postProcessing: PartialFunction[(XMLEvent,Option[Node]), Unit] = PartialFunction.empty
}
