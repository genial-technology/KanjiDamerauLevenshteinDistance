package correction.ranking

import correction.SurNameDictionary

import scala.io.Source

class SurNameJRanking(topN: Int) extends JRanking(topN, SurNameDictionary) {
  override protected def set(text: String): Unit = {
    val source = Source.fromFile(dictionary.path.toFile)
    source.getLines foreach { line: String =>
      val tokens: Array[String] = line.trim.split(':').map(_.trim)
      if (tokens.length == 2 && tokens.head.startsWith(""""entry"""")) {
        val entry: String = tokens.last.replaceAllLiterally(""""""", "")
        val similarity: Double = distance.calculate(text.take(entry.length): String, entry: String)
        //System.err.println(text)
        //System.err.println(entry)
        //System.err.println(similarity)
        enqueue(entry: String, similarity: Double)
      }
    }
    source.close()
  }
}