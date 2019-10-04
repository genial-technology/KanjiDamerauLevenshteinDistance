package kdld

import scala.collection.mutable.ArrayBuffer

class LevenshteinDistance(costParameters: CostParameters) {

  def calculateCost[Element](array1: Array[Element],
                             array2: Array[Element]): (Double, ArrayBuffer[Edit[Element]]) = {

    //println(array1.map(_.toString).mkString(", "))
    //println(array2.map(_.toString).mkString(", "))

    val operationList = ArrayBuffer.empty[Edit[Element]]

    import costParameters._

    val length1: Int = array1.length
    val length2: Int = array2.length

    var p = new Array[Double](length1 + 1)
    var d = new Array[Double](length1 + 1)

    if (length1 == 0 || length2 == 0) {
      if (length1 == length2) {
        return (1D, operationList)
      } else {
        return (0D, operationList)
      }
    }

    for (i <- 0 to length1) {
      p(i) = i
    }

    for (j <- 1 to length2) {
      val t_j: Element = array2(j-1)
      d(0) = j

      for (i <- 1 to length1) {
        val replaceCost_ : Double = p(i - 1) + {
          if (array1(i - 1) == t_j) {
            0
          } else if (replaceCost < insertCost + deleteCost) {
            replaceCost
          } else {
            insertCost + deleteCost
          }
        }
        val insertCost_ : Double = d(i - 1) + insertCost
        val deleteCost_ : Double = p(i) + deleteCost

        d(i) = Seq[Double](
          insertCost_,
          deleteCost_,
          replaceCost_
        ).min

        def addDelete(): Unit = {
          val delete = Delete(array1(i - 1), i - 1)
          delete.i = i - 1
          delete.j = j - 1
          delete.cost = deleteCost
          operationList += delete
        }

        def addInsert(): Unit = {
          val insert = Insert(array2(j - 1), j - 1)
          insert.i = i - 1
          insert.j = j - 1
          insert.cost = insertCost
          operationList += insert
        }

        def addReplace(): Unit = {
          val replace = Replace(array1(i - 1), i - 1, array2(j - 1), j - 1)
          replace.i = i - 1
          replace.j = j - 1
          replace.cost = replaceCost
          operationList += replace
        }

        d(i) match {
          case d if d - p(i - 1) == 0 =>
          // Do nothing
          case d if d == deleteCost_ =>
            addDelete()
          case d if d == insertCost_ =>
            addInsert()
          case d if d == replaceCost_ =>
            if (replaceCost < insertCost + deleteCost) {
              addReplace()
            } else {
              addDelete()
              addInsert()
            }
          case _ =>
          // Error
        }
      }

      val _d: Array[Double] = p
      p = d
      d = _d
    }

    (p(length1), operationList)
  }
}

object LevenshteinDistance extends App {
  val d = new LevenshteinDistance(CostParameters(1, 1, 1, 0))

  val (distance, operationList) = d.calculateCost[Char](
    "abc".toCharArray,
    "tc".toCharArray
  )

  println(s"distance = $distance")
  println(operationList.mkString("\n"))
}