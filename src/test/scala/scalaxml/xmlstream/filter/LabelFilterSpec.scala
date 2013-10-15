package scalaxml.xmlstream.filter

import scalaxml.xmlstream._
import org.specs2.mutable.Specification

class LabelFilterSpec extends Specification {
  val xml =
    <root>
      <parent>
        <name role="father">Gus</name>
        <child>
          <name role="daughter">Elsa</name>
        </child>
      </parent>
    </root>

  "LabelFilter" should {
    "filter nodes by their label" in {
      val reader = new XmlElementReader(createXMLEventReader(xml))
        with LabelFilter { val labels = Set("name") }

      val nodes = reader.streamElements.toList
      nodes must haveSize(2)

      val parentNode = nodes(0)
      parentNode.label === "name"
      (parentNode \ "@role").text === "father"
      parentNode.text === "Gus"

      val childNode = nodes(1)
      childNode.label === "name"
      (childNode \ "@role").text === "daughter"
      childNode.text === "Elsa"
    }
  }
}
