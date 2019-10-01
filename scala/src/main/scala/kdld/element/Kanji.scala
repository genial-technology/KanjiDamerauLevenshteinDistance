package kdld.element

case class Kanji(steps: Array[Step]) {

  override def equals(o: Any): Boolean = {
    o match {
      case k: Kanji =>
        this.toString == k.toString
      case _ => false
    }
  }

  def originalExpression: (String, Int) = {
    if (steps.nonEmpty) {
      val codePoint: Int = steps.head.listOfHBKKAndNumOfStrokes.head.asInstanceOf[AHBKK].codePoint
      (new String(
        Array[Int](codePoint),
        0 ,
        1
      ), codePoint)
    } else {
      ("", -1)
    }
  }

  override def toString: String = {
    if (steps.nonEmpty) {
      val (expression: String, codePoint: Int) = originalExpression
      s"Kanji($expression: $codePoint)"
    } else {
      "Kanji()"
    }
  }

  def comparableStep(kanji: Kanji): (Step, Step) = {
    steps foreach { thisStep: Step =>
      kanji.steps foreach { argStep: Step =>
        if (thisStep.partiallyMatches(argStep)) {
          return (thisStep: Step, argStep: Step)
        }
      }
    }
    (steps.last: Step, kanji.steps.last: Step)
  }
}

object Kanji {
  def empty: Kanji = new Kanji(Array.empty[Step])
}