package kdld.element

/**
 * HBKK = 偏旁冠脚
 * the number of strokes = 画数
 */
sealed trait HBKK
case class AHBKK(codePoint: Int, numOfStrokes: Short) extends HBKK {

  override def toString: String = {
    val label = new String(Array[Int](codePoint), 0, 1)
    s"AHBKK($label: $codePoint, $numOfStrokes)"
  }

  override def equals(o: Any): Boolean = {
    o match {
      case a: AHBKK =>
        this.codePoint == a.codePoint
      case _ => false
    }
  }
}
case object NoHBKK extends HBKK