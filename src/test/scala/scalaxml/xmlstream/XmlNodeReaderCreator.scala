package scalaxml.xmlstream

import scala.io.Source
import scala.xml.Elem
import scala.xml.pull.XMLEventReader

trait XmlNodeReaderCreator {
  def createXMLEventReader(xml: Elem): XMLEventReader = {
    new XMLEventReader(Source.fromString(xml.toString))
  }
}
