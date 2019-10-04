package kdld

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
/**
 *
 */
class DamerauLevenshteinDistance(costParameters: CostParameters) {

  def calculateCost[Element](array1: Array[Element],
                             array2: Array[Element]): (Double, ArrayBuffer[Edit[Element]]) = {

    //println(array1.map(_.toString).mkString(", "))
    //println(array2.map(_.toString).mkString(", "))

    val length1: Int = array1.length
    val length2: Int = array2.length
    val operationList = ArrayBuffer.empty[Edit[Element]]

    import costParameters._

    /**
     * if array1.length == 0
     */
    if (length1 == 0) {
      for (i <- array2.indices) {
        val operation = Insert(array2(i), i)
        operation.cost = insertCost
        operation.i = i
        operation.j = i
        operationList += operation
      }
      return (length2 * insertCost: Double, operationList: ArrayBuffer[Edit[Element]])
    }

    /**
     * if array2.length == 0
     */
    if (length2 == 0) {
      for (i <- array1.indices) {
        val operation = Delete(array1(i), i)
        operation.cost = deleteCost
        operation.i = i
        operation.j = i
        operationList += operation
      }
      return (length1 * deleteCost: Double, operationList: ArrayBuffer[Edit[Element]])
    }

    /**
     * initialize operationTable
     */
    val operationTable: ArrayBuffer[ArrayBuffer[Edit[Element]]] = ArrayBuffer.empty[ArrayBuffer[Edit[Element]]]
    for (i <- 0 until length1) {
      operationTable += ArrayBuffer.empty[Edit[Element]]
      for (_ <- 0 until length2) {
        operationTable(i) += NoEdit[Element]()
      }
    }

    /**
     * initialize table
     */
    val table: Array[Array[Double]] = Array.ofDim(length1, length2)

    val array1IndexByElement = mutable.Map.empty[Element, Int]
    if (array1(0) != array2(0)) {
      val cost = math.min(replaceCost, deleteCost + insertCost)
      def addDelete(): Unit = {
        val delete = Delete(array1(0), 0)
        delete.cost = cost
        delete.i = 0
        delete.j = 0
        operationList += delete
      }
      def addInsert(): Unit = {
        val insert = Insert(array2(0), 0)
        insert.cost = cost
        insert.i = 0
        insert.j = 0
        operationList += insert
      }
      def addReplace(): Unit = {
        val replace = Replace(array1(0), 0, array2(0), 0)
        replace.cost = cost
        replace.i = 0
        replace.j = 0
        operationList += replace
      }
      if (length1 < length2 ) {
        if (array1(0) == array2(1)) {
          addInsert()
        } else {
          addReplace()
        }
      } else if (length2 < length1) {
        if (array2(0) == array1(1)) {
          addDelete()
        } else {
          addReplace()
        }
      } else {
        if (replaceCost < deleteCost + insertCost) {
          addReplace()
        } else {
          addDelete()
          addInsert()
        }
      }
      table(0)(0) = cost
    }
    array1IndexByElement.put(array1(0), 0)

    for (i <- 1 until length1) {
      val deleteDistance: Double = table(i - 1)(0) + deleteCost
      val insertDistance: Double = (i + 1) * deleteCost + insertCost
      val matchDistance: Double = i * deleteCost + (if (array1(i) == array2(0)) 0 else replaceCost)
      table(i)(0) = Seq[Double](deleteDistance, insertDistance, matchDistance).min
    }

    for (j <- 1 until length2) {
      val deleteDistance: Double = (j + 1) * insertCost + deleteCost
      val insertDistance: Double = table(0)(j - 1) + insertCost
      val matchDistance: Double = j * insertCost + (if (array1(0) == array2(j)) 0 else replaceCost)
      table(0)(j) = Seq[Double](deleteDistance, insertDistance, matchDistance).min
    }

    for (i <- 1 until length1) {
      var maxArray1LetterMatchIndex: Int = if (array1(i) == array2(0)) 0 else -1
      for (j <- 1 until length2) {
        val candidateSwapIndex: Option[Int] = array1IndexByElement.get(array2(j))
        val jSwap: Int = maxArray1LetterMatchIndex


        /**
         * elements A1: array1(0) to array1(i) =
         * elements B1: array1(0) to array1(i - 1) + an element array1(i)
         */
        val deleteDistance: Double = table(i - 1)(j) + deleteCost
        val delete = Delete[Element](array1(i), i)

        /**
         * elements A2: array2(0) to array2(j) =
         * elements B2: array2(0) to array2(j - 1) + an element array2(j)
         */
        val insertDistance: Double = table(i)(j - 1) + insertCost
        val insert = Insert[Element](array2(j), j)

        var matchDistance: Double = table(i - 1)(j - 1)
        val replaceOpt: Option[Replace[Element]] = {
          if (array1(i) != array2(j)) {
            /**
             * an element array1(i) <=> array2(j)
             */
            matchDistance += replaceCost
            Option(Replace[Element](array1(i), i, array2(j), j))
          } else {
            maxArray1LetterMatchIndex = j
            None
          }
        }

        var swapOpt = Option.empty[Swap[Element]]
        val swapDistance: Double = {
          if (candidateSwapIndex.nonEmpty && jSwap != -1) {
            val iSwap: Int = candidateSwapIndex.get
            val preSwapCost: Double = {
              if (iSwap == 0 && jSwap == 0) {
                0
              } else {
                table(math.max(0, iSwap - 1))(math.max(0, jSwap - 1))
              }
            }

            /**
             * an element array1(i) = an element array2(j - jSwap - 1)
             * an element array1(i - iSwap - 1) = an element array2(j)
             *
             * an element array1(i - 1) <=> an element array1(i)
             */
            swapOpt = Option(Swap[Element](array1(i - 1), i - 1, array1(i), i))
            preSwapCost + (i - iSwap - 1) * deleteCost + (j - jSwap - 1) * insertCost + swapCost
          } else {
            Int.MaxValue
          }
        }

        val distance: Double = Seq(deleteDistance, insertDistance, matchDistance, swapDistance).min
        distance match {
          case d if d <= 0 =>
            // Ignore
          case d if d == deleteDistance =>
            operationTable(i)(j) = delete
          case d if d == insertDistance =>
            operationTable(i)(j) = insert
          case d if d == matchDistance =>
            replaceOpt foreach { replace: Replace[Element] =>
              operationTable(i)(j) = replace
            }
          case d if d == swapDistance =>
            swapOpt foreach { swap: Swap[Element] =>
              operationTable(i)(j) = swap
            }
          case _ =>
            // Error
        }
        table(i)(j) = distance

      }
      array1IndexByElement.put(array1(i), i)
    }



    def addEdit(operation: Edit[Element],
                     getIndex: Edit[Element] => (Int, Int)): Unit = {
      if (!operation.isInstanceOf[NoEdit[Element]]) {
        val (indexI: Int, indexJ: Int) = getIndex(operation)
        operation.cost = {
          operation match {
            case _: Swap[Element] =>
              val previousCost: Double = {
                if (0 < indexI && 0 < indexJ) {
                  table(indexI - 1)(indexJ - 1)
                } else {
                  0d
                }
              }
              table(operation.i - 1)(operation.j - 1) - previousCost
            case _ =>
              table(operation.i)(operation.j) - table(indexI)(indexJ)
          }
        }
        operationList += operation
      }
    }

    def getIndex11(o: Edit[Element]): (Int, Int) = (o.i - 1, o.j - 1)
    def getIndex01(o: Edit[Element]): (Int, Int) = (o.i,     o.j - 1)
    def getIndex10(o: Edit[Element]): (Int, Int) = (o.i - 1, o.j    )

    for (i <- 0 until math.min(length1, length2)) {
      val operation = operationTable(i)(i)
      operation.i = i
      operation.j = i
      addEdit(operation , getIndex11)
    }

    if (length1 < length2) {
      for (i <- length1 until length2) {
        val operation = operationTable(length1 - 1)(i)
        operation.i = length1 - 1
        operation.j = i
        addEdit(operation, getIndex01)
      }
    } else if (length2 < length1) {
      for (i <- length2 until length1) {
        val operation = operationTable(i)(length2 - 1)
        operation.i = i
        operation.j = length2 - 1
        addEdit(operation, getIndex10)
      }
    }

    val operationListResult1 = ArrayBuffer.empty[Edit[Element]]
    var i: Int = 0
    while (i < operationList.length - 1) {
      operationList(i) match {
        case o1: Replace[Element] =>
          operationList(i + 1) match {
            case o2: Swap[Element] =>
              if (o1.element1 == o2.element1 && o1.element2 == o2.element2) {
                operationListResult1 += o2
                i += 1
              }
            case _: Edit[Element] =>
              operationListResult1 += o1
          }
        case otherwise: Edit[Element] =>
          operationListResult1 += otherwise
      }
      i += 1
    }
    operationList.lastOption match {
      case Some(_: Swap[Element]) | None =>
        // Do nothing
      case Some(otherwise: Edit[Element]) =>
        operationListResult1 += otherwise
    }

    val operationListResult2 = ArrayBuffer.empty[Edit[Element]]
    if (replaceCost < deleteCost + insertCost) {
      operationListResult2 ++= operationListResult1
    } else {
      operationListResult1 foreach {
        case o: Replace[Element] =>
          operationListResult2 += Delete[Element](o.element1, o.index1)
          operationListResult2 += Insert[Element](o.element2, o.index2)
          table(length1 - 1)(length2 - 1) += deleteCost + insertCost - replaceCost
        case otherwise: Edit[Element] =>
          operationListResult2 += otherwise
      }
    }

    //println()
    //operationListResult2 foreach println

    /*
    println("START OPERATION")
    operationTable foreach { operationElements =>
      println("---")
      operationElements foreach { operation =>
        println(operation.toString)
      }
      println("---")
    }
    println("END OPERATION")
    */
    /*
    println("START COST")
    for (i <- table.indices) {
      println("---")
      for (j <- table(i).indices) {
        println(s"$i $j ${table(i)(j)}")
      }
      println("---")
    }
    println("END COST")
    */
    (table(length1 - 1)(length2 - 1), operationListResult2)
  }
}