package correction.ranking

import correction.CompanyNameDictionary

import scala.collection.mutable.ListBuffer
import scala.io.Source

class CompanyNameDLRanking(topN: Int) extends DLRanking(topN, CompanyNameDictionary) {
  private val lines: List[String] = {
    println("a")
    val source = Source.fromFile(dictionary.path.toFile)
    println("b")
    val list = source.getLines.toList
    println("c")
    source.close()
    list
  }

  override protected def set(text: String): Unit = {
    //System.out.println(dictionary.path.toFile.canRead)
    //val source = Source.fromFile(dictionary.path.toFile)
    var isEntry: Boolean = false
    var entry: String = null

    //source.getLines
    lines foreach { line: String =>
      //println(s"asdfasdf  $line")
      val tokens: Array[String] = line.trim.split(':').map(_.trim)


      if (tokens.length == 2) {
        //System.err.println("HERE3")

        if (tokens.head.trim.startsWith(""""labelForAddress"""")) {
          isEntry = false
          //System.err.println("HERE2")
          val labelForAddress: String = tokens.last.trim.replaceAllLiterally(""""""", "").replaceAllLiterally(",", "")
          if (labelForAddress == "corporationName") {
            //System.err.println("HERE1")
            val similarity: Double = distance.calculate(text: String, entry: String)
            enqueue(entry: String, similarity: Double)
          }
        //} else if (tokens.head.startsWith("""theNextLargerArea""")) {
          //isEntry = false
          //val theNextLargerArea: String = tokens.last.replaceAllLiterally(""""""", "")

        } else if (tokens.head.trim.startsWith(""""entry"""")) {
          isEntry = true
          entry = tokens.last.trim.replaceAllLiterally(""""""", "").replaceAllLiterally(",", "")

          //System.err.println(text)
          //System.err.println(entry)
          //System.err.println(similarity)
        }
      }
    }
    //source.close()
  }
}