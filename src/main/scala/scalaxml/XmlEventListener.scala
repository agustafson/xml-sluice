package scalaxml

import scala.xml.pull.XMLEvent

trait XmlEventListener {
  def preProcessing: XMLEvent => Unit = _ => ()

  def postProcessing[T]: (XMLEvent,Option[T]) => Unit = (_,_) => ()
}
