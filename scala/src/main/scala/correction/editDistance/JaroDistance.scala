package correction.editDistance

/**
 * val threshold = 0.7
 * val scalingFactor = 0.1 // should not exceed 0.25
 */
object JaroDistance extends JaroWinklerDistance(threshold = 0.7, scalingFactor = 0.1) {
  override def calculate[Element](array1: Array[Element], array2: Array[Element]): Double = {
    JaroWinklerDistance.calculateJaroDistance(array1, array2)
  }
}