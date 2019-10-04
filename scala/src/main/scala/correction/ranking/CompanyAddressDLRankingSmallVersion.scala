package correction.ranking

import java.io.File

import correction.{CompanyAddressDictionary, CompanyNameDictionary}

import scala.io.Source

class CompanyAddressDLRankingSmallVersion extends DLRanking(1, CompanyAddressDictionary) {
  override def set(text: String): Unit = {
    val source = Source.fromFile(new File("src/main/resources/ocrResults/address_adobe.tsv"))
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