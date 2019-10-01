package kdld.dld

import kdld.Result
import kdld.element.{AHBKK, HBKK, NoHBKK}

object StrokeLevelDLD extends DLD {

  def result(hbkk1: HBKK, hbkk2: HBKK): Result = {
    val (cost: Double, length: Int) = getCostAndLength(hbkk1, hbkk2)
    val correctedCost: Double = getCorrectedCost(cost)
    new Result(
      correctedCost: Double,
      getDissimilarity(correctedCost, length): Double,
      length: Int
    )
  }

  private def getCorrectedCost(cost: Double): Double = {
    val correction: Int = 1
    cost + correction
  }

  private def getCostAndLength(hbkk1: HBKK, hbkk2: HBKK): (Double, Int) = {
    hbkk1 match {
      case aHBKK1: AHBKK =>
        hbkk2 match {
          case aHBKK2: AHBKK =>
            (
              math.abs(aHBKK1.numOfStrokes - aHBKK2.numOfStrokes),
              math.max(aHBKK1.numOfStrokes, aHBKK2.numOfStrokes)
            )
          case NoHBKK =>
            (
              aHBKK1.numOfStrokes,
              aHBKK1.numOfStrokes
            )
        }
      case NoHBKK =>
        hbkk2 match {
          case aHBKK2: AHBKK =>
            (
              aHBKK2.numOfStrokes,
              aHBKK2.numOfStrokes
            )
          case NoHBKK =>
            (
              0d,
              0
            )
        }
    }
  }
}
