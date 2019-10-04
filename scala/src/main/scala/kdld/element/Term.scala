package kdld.element

case class Term(kanjiList: Array[Kanji]) {

  override def toString: String = {
    val label: String = kanjiList.toSeq.map { kanji: Kanji =>
      val (expression: String, codePoint: Int) = kanji.originalExpression
      s"$expression: $codePoint"
    }.mkString(", ")
    s"Term($label)"
  }

  override def equals(o: Any): Boolean = {
    o match {
      case t: Term =>
        this.toString == t.toString
      case _ => false
    }
  }

}