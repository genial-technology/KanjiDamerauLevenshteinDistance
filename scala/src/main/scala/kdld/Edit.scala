package kdld

sealed trait Edit[Element] {
  var cost: Double = 0d
  var i: Int = -1
  var j: Int = -1
}

case class Delete[Element](element: Element, index: Int) extends Edit[Element] {
  override def toString: String = {
    s"""delete $element $index, cost $cost, i $i, j $j"""
  }
}

case class Insert[Element](element: Element, index: Int) extends Edit[Element] {
  override def toString: String = {
    s"insert $element $index, cost $cost, i $i, j $j"
  }
}

case class Replace[Element](element1: Element, index1: Int,
                            element2: Element, index2: Int) extends Edit[Element] {
  override def toString: String = {
    s"replace $element1 $index1 with $element2 $index2, cost $cost, i $i, j $j"
  }
}

case class Swap[Element](element1: Element, index1: Int,
                         element2: Element, index2: Int) extends Edit[Element] {
  override def toString: String = {
    s"swap $element1 $index1 and $element2 $index2, cost $cost, i $i, j $j"
  }
}

case class NoEdit[Element]() extends Edit[Element] {
  override def toString: String = {
    s"no operation, i $i, j $j"
  }
}