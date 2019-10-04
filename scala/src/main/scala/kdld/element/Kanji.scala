package kdld.element

case class Kanji(steps: Array[Step], original: Int) {

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
      (new String(Array[Int](original), 0, 1), original)
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

  def comparableStep(kanji: Kanji): Option[(Step, Step)] = {
    steps foreach { thisStep: Step =>
      kanji.steps foreach { argStep: Step =>
        if (thisStep.partiallyMatches(argStep)) {
          return Option((thisStep: Step, argStep: Step))
        }
      }
    }
    if (0 < steps.length && 0 < kanji.steps.length) {
      Option((steps.last: Step, kanji.steps.last: Step))
    } else {
      None
    }
  }
}

object Kanji {
  def empty: Kanji = new Kanji(Array.empty[Step], -1)
}
