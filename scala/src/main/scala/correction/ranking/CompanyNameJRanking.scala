package correction.ranking

import correction.CompanyNameDictionary

import scala.io.Source

class CompanyNameJRanking(topN: Int) extends JRanking(topN, CompanyNameDictionary) {
  override protected def set(text: String): Unit = {
    val source = Source.fromFile(dictionary.path.toFile)
    var isEntry: Boolean = false
    var entry: String = null
    source.getLines foreach { line: String =>
      val tokens: Array[String] = line.trim.split(':').map(_.trim)

      if (tokens.length == 2) {
        if (tokens.head.trim.startsWith(""""labelForAddress"""")) {
          isEntry = false
          val labelForAddress: String = tokens.last.trim.replaceAllLiterally(""""""", "").replaceAllLiterally(",", "")
          if (labelForAddress == "corporationName") {
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
    source.close()
  }
}