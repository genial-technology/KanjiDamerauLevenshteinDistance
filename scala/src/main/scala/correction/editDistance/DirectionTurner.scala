package correction.editDistance

object DirectionTurner {
  def reciprocal(score: Double): Double = {
    if (score == 0) {
      1D
    } else {
      1 / score
    }
  }

  def oneMinus(score: Double): Double = {
    1D - score
  }
}