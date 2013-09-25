package scalaxml.xmlstream.listener

import scala.xml.Node
import scala.xml.pull.XMLEvent

trait XmlEventListener {
  def preProcessing: XMLEvent => Unit = _ => ()

  def postProcessing: (XMLEvent,Option[Node]) => Unit = (_,_) => ()
}
