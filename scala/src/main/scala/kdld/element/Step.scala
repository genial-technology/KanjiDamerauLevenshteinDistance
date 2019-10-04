package kdld.element

case class Step(listOfHBKKAndNumOfStrokes: Array[HBKK]) {

  private def contains(thisHBKK: AHBKK): Boolean = {
    listOfHBKKAndNumOfStrokes foreach {
      case hbkk: AHBKK if thisHBKK == hbkk =>
        return true
      case _ => // Do nothing
    }
    false
  }

  def partiallyMatches(step: Step): Boolean = {
    step.listOfHBKKAndNumOfStrokes foreach {
      case hbkk: AHBKK if contains(hbkk) =>
        return true
      case _ => // Do nothing
    }
    false
  }
}