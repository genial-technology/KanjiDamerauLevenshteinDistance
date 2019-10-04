package correction.editDistance

import correction.hbkk.HBKKJsonParser
import kdld.{CostParameters, KanjiDamerauLevenshteinDistance}

object KanjiDamerauLevenshteinDistance extends Distance {
  private val dld = new KanjiDamerauLevenshteinDistance(
    CostParameters(1, 1, 1, 1),
    CostParameters(1, 1, 1, 0.5))
  private val dic = new HBKKJsonParser()
  override def calculate[Element](array1: Array[Element], array2: Array[Element]): Double = {
    0d
  }

  override def calculate(text1: String, text2: String): Double = {
    val result = dld.result(dic.getTerm(text1), dic.getTerm(text2))
    DirectionTurner.oneMinus(result.dissimilarity)
  }
}
