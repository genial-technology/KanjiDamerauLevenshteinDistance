package kdld.dld

import kdld.{CostParameters, DamerauLevenshteinDistance, Edit, Replace, Result}
import kdld.element.{Kanji, Step}

import scala.collection.mutable.ArrayBuffer

class CharacterLevelDLD(characterLevelCostParameters: CostParameters,
                        hbkkLevelCostParameters: CostParameters) extends DLD {

  def result(kanjiArray1: Array[Kanji], kanjiArray2: Array[Kanji]): Result = {
    val hbkkLevelDLD = new HBKKLevelDLD(hbkkLevelCostParameters)
    val length: Int = kanjiArray1.length
    val (cost: Double, operationList: ArrayBuffer[Edit[Kanji]]) = {
      new DamerauLevenshteinDistance(characterLevelCostParameters).
        calculateCost[Kanji](kanjiArray1: Array[Kanji], kanjiArray2: Array[Kanji])
    }
    var negativeFeedback: Double = 0d
    operationList foreach {
      case replace: Replace[Kanji] =>
        val source: Kanji = replace.element1
        val target: Kanji = replace.element2
        val (step1: Step, step2: Step) = source.comparableStep(target)
        val result: Result = hbkkLevelDLD.result(step1, step2)
        negativeFeedback += characterLevelCostParameters.replaceCost * (1 - result.dissimilarity)
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
