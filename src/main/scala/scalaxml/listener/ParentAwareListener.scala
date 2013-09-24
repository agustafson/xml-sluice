package scalaxml.listener

import scala.collection.mutable
import scala.xml.Node
import scala.xml.pull._

trait ParentAwareListener extends XmlEventListener with DepthAwareListener {
  private val parents = new mutable.Stack[String]

  def parent: Option[String] = {
    if (parents.isEmpty) None else Some(parents.top)
  }

  override def preProcessing: (XMLEvent) => Unit = { event =>
    super.preProcessing(event)
    event match {
      /*case EvElemEnd(_,label) =>
        if (currentDepth <= parents.length)
          parents.pop()
        println(s"END $label; parents: $parents")*/
      case EvElemStart(_,label,_,_) =>
        println(s"start stuff $label")
      case EvElemEnd(_,label) =>
        println(s"END $label; parents: $parents; currentDepth: $currentDepth")
        //if (currentDepth < parents.length)
          parents.pop()
      case _ =>
    }
  }

  override def postProcessing: (XMLEvent, Option[Node]) => Unit = { case (event,nodeResult) =>
    //super.postProcessing.apply(event,nodeResult)
    event match {
      case EvElemStart(_,label,_,_) =>
        currentDepth = currentDepth + 1
        println(s"START $label; parents: $parents; currentDepth: $currentDepth")
        if (currentDepth > parents.length)
          parents.push(label)
      /*case (EvElemStart(_,label,_,_), _) =>
        if (currentDepth == parents.length)
          parents.push(label)
        println(s"START $label; parents: $parents")*/
      case _ =>
    }
  }
}
