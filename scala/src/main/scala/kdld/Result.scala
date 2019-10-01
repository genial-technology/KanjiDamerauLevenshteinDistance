package kdld

class Result(var distance: Double,
             var dissimilarity: Double,
             var length: Int) {
  override def toString: String = {
    s"Result(distance = $distance, dissimilarity = $dissimilarity, length = $length)"
  }
}