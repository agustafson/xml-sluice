package scala.xml.sluice

import scala.io.Source
import scala.xml.Elem
import scala.xml.pull.XMLEventReader

object `package` {
  def createXMLEventReader(xml: Elem): XMLEventReader = {
    new XMLEventReader(Source.fromString(xml.toString))
  }
}
