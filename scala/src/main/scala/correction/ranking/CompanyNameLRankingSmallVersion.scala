package correction.ranking

import java.io.File

import correction.CompanyNameDictionary

import scala.io.Source

class CompanyNameLRankingSmallVersion extends LRanking(1, CompanyNameDictionary) {
  override def set(text: String): Unit = {
    val source = Source.fromFile(new File("src/main/resources/ocrResults/companyName_adobe.tsv"))
    source.getLines foreach { line: String =>
      val tokens: Array[String] = line.trim.split('\t').map(_.trim)
      if (tokens.length == 4) {
        val entry: String = tokens(1)
        val similarity: Double = distance.calculate(text, entry)
        enqueue(entry, similarity)
      }
    }
    source.close()
  }
}
