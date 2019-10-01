package kdld.dld

import kdld.{CostParameters, DamerauLevenshteinDistance, Edit, Replace, Result}
import kdld.element.{HBKK, Step}

import scala.collection.mutable.ArrayBuffer

class HBKKLevelDLD(costParameters: CostParameters) extends DLD {

  def result(step1: Step, step2: Step): Result = {
    val length: Int = step1.listOfHBKKAndNumOfStrokes.length
    val (cost: Double, operationList: ArrayBuffer[Edit[HBKK]]) = {
      new DamerauLevenshteinDistance(costParameters).
        calculateCost[HBKK](
          step1.listOfHBKKAndNumOfStrokes: Array[HBKK],
          step2.listOfHBKKAndNumOfStrokes: Array[HBKK]
        )
    }
    var negativeFeedback: Double = 0d
    operationList foreach {
      case replace: Replace[HBKK] =>
        val result: Result = StrokeLevelDLD.result(replace.element1, replace.element2)
        negativeFeedback += costParameters.replaceCost * (1 - result.dissimilarity)
      case _ =>
      // Do nothing
    }
    val correctedCost: Double = cost - negativeFeedback
    new Result(
      correctedCost: Double,
      getDissimilarity(correctedCost, length): Double,
      length: Int
    )
  }
}
