package kdld.dld

trait DLD {
  protected def getDissimilarity(cost: Double, length: Int): Double = {
    math.min(cost / length, 1d)
  }
}
