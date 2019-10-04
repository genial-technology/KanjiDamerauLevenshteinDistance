package correction.ranking

import correction.CompanyAddressDictionary

import scala.io.Source

class CompanyAddressLRanking(topN: Int) extends LRanking(topN, CompanyAddressDictionary) {
  override protected def set(text: String): Unit = {
    //for example, text = ocrResult@cityName@横浜市保土ヶ谷区
    val source = Source.fromFile(dictionary.path.toFile)
    var isEntry: Boolean = false
    var entry: String = null
    val array = text.split('@')
    if (array.length == 3) {
      val ocrResult: String = array.head
      val label: String = array(1)//streetName, cityName, prefectureName
      val nextLargerArea: String = array.last
      var isLabel: Boolean = false

      source.getLines foreach { line: String =>
        val tokens: Array[String] = line.trim.split(':').map(_.trim)

        if (tokens.length == 2) {
          if (tokens.head.trim.startsWith(""""labelForAddress"""")) {
            //isEntry = false
            val labelForAddress: String = tokens.last.trim.replaceAllLiterally(""""""", "").replaceAllLiterally(",", "")
            if (labelForAddress == label) { //streetName, cityName, prefectureName
              isLabel = true
            }
          } else if (tokens.head.startsWith(""""theNextLargerArea"""")) {
            isEntry = false
            if (isLabel) {
              isLabel = false
              val theNextLargerArea: String = tokens.last.replaceAllLiterally(""""""", "")
              if (theNextLargerArea == nextLargerArea) {
                val similarity: Double = distance.calculate(ocrResult.take(entry.length): String, entry: String)
                enqueue(entry: String, similarity: Double)
              }
            }
          } else if (tokens.head.trim.startsWith(""""entry"""")) {
            isEntry = true
            entry = tokens.last.trim.replaceAllLiterally(""""""", "").replaceAllLiterally(",", "")

            //System.err.println(text)
            //System.err.println(entry)
            //System.err.println(similarity)
          }
        }
      }
    }
    source.close()
  }
}