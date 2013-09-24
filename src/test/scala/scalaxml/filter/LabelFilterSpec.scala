package scalaxml.filter

import scalaxml.{XmlNodeReader, XmlNodeReaderCreator}
import org.specs2.mutable.Specification

class LabelFilterSpec extends Specification with XmlNodeReaderCreator {
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
      val reader = new XmlNodeReader(createXMLEventReader(xml)) with LabelFilter {
        def labels: Seq[String] = Seq("name")
      }

      val nodes = reader.readNodes.toList
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
