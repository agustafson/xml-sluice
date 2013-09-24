package scalaxml.filter

import scalaxml.{XmlNodeReader, XmlNodeReaderCreator}
import org.specs2.mutable.Specification

class ParentNameFilterSpec extends Specification with XmlNodeReaderCreator {
  val xml =
    <root>
      <parent>
        <children>
          <child>child 1</child>
          <child>child 2</child>
        </children>
        <nested>
          <children>
            <child>nested 3</child>
          </children>
        </nested>
      </parent>
    </root>

  "ParentNameFilter" should {
    "find nodes by parent name" in {
      val reader = new XmlNodeReader(createXMLEventReader(xml))
        with ParentNameFilter { val parentNames = Set("children") }
      val nodes = reader.readNodes.toList
      nodes must haveSize(3)
      nodes.map(_.text.trim) === Seq("child 1", "child 2", "nested 3")
    }
  }
}
