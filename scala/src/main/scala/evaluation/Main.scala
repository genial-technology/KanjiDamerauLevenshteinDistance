package evaluation

import java.io.PrintWriter
import java.nio.file.{Files, Path}

import normalization.Normalizer

import scala.collection.mutable
import scala.io.Source

object Main extends App {

  private def fillTemplate(totalRateForName: String,
                           totalRateForCompanyName: String,
                           totalRateForCompanyAddress: String,
                           totalRateForTerm: String,
                           totalCountForName: Int,
                           totalCountForCompanyName: Int,
                           totalCountForCompanyAddress: Int,
                           totalCountForTerm: Int,
                           msRateForName: String,
                           msRateForCompanyName: String,
                           msRateForCompanyAddress: String,
                           msRateForTerm: String,
                           msCountForName: Int,
                           msCountForCompanyName: Int,
                           msCountForCompanyAddress: Int,
                           msCountForTerm: Int,
                           mspRateForName: String,
                           mspRateForCompanyName: String,
                           mspRateForCompanyAddress: String,
                           mspRateForTerm: String,
                           mspCountForName: Int,
                           mspCountForCompanyName: Int,
                           mspCountForCompanyAddress: Int,
                           mspCountForTerm: Int,
                           yuRateForName: String,
                           yuRateForCompanyName: String,
                           yuRateForCompanyAddress: String,
                           yuRateForTerm: String,
                           yuCountForName: Int,
                           yuCountForCompanyName: Int,
                           yuCountForCompanyAddress: Int,
                           yuCountForTerm: Int,
                           meiryoRateForName: String,
                           meiryoRateForCompanyName: String,
                           meiryoRateForCompanyAddress: String,
                           meiryoRateForTerm: String,
                           meiryoCountForName: Int,
                           meiryoCountForCompanyName: Int,
                           meiryoCountForCompanyAddress: Int,
                           meiryoCountForTerm: Int
                          ): String =
    raw"""      & 総合 & $totalRateForName & $totalRateForCompanyName & $totalRateForCompanyAddress & $totalRateForTerm \\
       |      & (/1,100) & $totalCountForName & $totalCountForCompanyName & $totalCountForCompanyAddress & $totalCountForTerm \\\cline{2-6}
       |      & MS明朝 & $msRateForName & $msRateForCompanyName & $msRateForCompanyAddress & $msRateForTerm \\
       |      & (/275) & $msCountForName & $msCountForCompanyName & $msCountForCompanyAddress & $msCountForTerm \\\cline{2-6}
       |      & MS P明朝 & $mspRateForName & $mspRateForCompanyName & $mspRateForCompanyAddress & $mspRateForTerm \\
       |      & (/275) & $mspCountForName & $mspCountForCompanyName  & $mspCountForCompanyAddress & $mspCountForTerm \\\cline{2-6}
       |      & 游ゴシック & $yuRateForName & $yuRateForCompanyName & $yuRateForCompanyAddress & $yuRateForTerm \\
       |      & (/275) & $yuCountForName & $yuCountForCompanyName & $yuCountForCompanyAddress & $yuCountForTerm \\\cline{2-6}
       |      & メイリオ & $meiryoRateForName & $meiryoRateForCompanyName & $meiryoRateForCompanyAddress & $meiryoRateForTerm \\
       |      & (/275) & $meiryoCountForName & $meiryoCountForCompanyName & $meiryoCountForCompanyAddress & $meiryoCountForTerm \\\hline
       |
       |""".stripMargin


  private def accuracyOfOCRResults(tsv: Path, errorTsv: Path): (Map[String, Int], Map[String, String]) = {
    println("---")
    if (tsv.toFile.canRead) {
      val fileName: String = tsv.getFileName.toString
      println(fileName)
      val writer = new PrintWriter(Files.newBufferedWriter(errorTsv))
      val source = Source.fromFile(tsv.toFile)
      var isFirst = true
      val results = mutable.Map.empty[String, Int]
      val accuracies = mutable.Map.empty[String, String]

      def countUp(key: String): Unit = {
        if (results.contains(key)) {
          results(key) += 1
        } else {
          results(key) = 1
        }
      }

      source.getLines foreach { line: String =>
        if (isFirst) {
          isFirst = false
        } else {
          val tokens = line.split('\t')
          if (tokens.length == 4) {
            //val number: String = tokens.head.trim
            val goldStandard: String = tokens(1).trim
            val font: String = tokens(2).trim
            val output: String = tokens.last.trim
            val normalizedGoldStandard: String = Normalizer.normalize(goldStandard)
            val normalizedOutput: String = Normalizer.normalize(output)
            countUp("Total")
            if (normalizedGoldStandard == normalizedOutput) {
              countUp("Total-Correct")
              countUp(s"$font-Correct")
            } else {
              System.err.println(line)
              writer.println(line)
              countUp("Total-Incorrect")
              countUp(s"$font-Incorrect")
            }
          } else if (tokens.length == 3) {
            val font: String = tokens(2).trim
            countUp("Total-Incorrect")
            countUp(s"$font-Incorrect")
            System.err.println("Error:0")
            System.err.println(line)
          } else {
            System.err.println("Error:1")
            System.err.println(line)
          }
        }
      }
      source.close()
      writer.close()
      results foreach {
        case (key: String, count: Int) =>
          //        println(s"$key = $count")
          if (key.endsWith("-Correct")) {
            println(s"$key = $count")
            val accuracyFormat: String = "%.3f"
            val accuracy: Double = {
              if (key.startsWith("Total")) {
                count.toDouble / 1100
              } else {
                count.toDouble / 275
              }
            }
            val result: String = accuracyFormat.format(accuracy)
            accuracies(key) = result
          }
      }
      (results.toMap, accuracies.toMap)
    } else {
      (Map(), Map())
    }
  }

  private def accuracyOfCompanyAddressOCRResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRResults(ocr.companyAddressPath, ocr.companyAddressErrorPath)
  }

  private def accuracyOfCompanyNameOCRResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRResults(ocr.companyNamePath, ocr.companyNameErrorPath)
  }

  private def accuracyOfNameOCRResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRResults(ocr.namePath, ocr.nameErrorPath)
  }

  private def accuracyOfTermOCRResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRResults(ocr.termPath, ocr.termErrorPath)
  }

  val (tesseractNameCount, tesseractNameRate) = accuracyOfNameOCRResults(Tesseract)
  val (tesseractCompanyNameCount, tesseractCompanyNameRate) = accuracyOfCompanyNameOCRResults(Tesseract)
  val (tesseractCompanyAddressCount, tesseractCompanyAddressRate) = accuracyOfCompanyAddressOCRResults(Tesseract)
  val (tesseractTermCount, tesseractTermRate) = accuracyOfTermOCRResults(Tesseract)
  val (adobeNameCount, adobeNameRate) = accuracyOfNameOCRResults(AdobeAcrobat)
  val (adobeCompanyNameCount, adobeCompanyNameRate) = accuracyOfCompanyNameOCRResults(AdobeAcrobat)
  val (adobeCompanyAddressCount, adobeCompanyAddressRate) = accuracyOfCompanyAddressOCRResults(AdobeAcrobat)
  val (adobeTermCount, adobeTermRate) = accuracyOfTermOCRResults(AdobeAcrobat)
  val (xeroxNameCount, xeroxNameRate) = accuracyOfNameOCRResults(FujiXerox)
  val (xeroxCompanyNameCount, xeroxCompanyNameRate) = accuracyOfCompanyNameOCRResults(FujiXerox)
  val (xeroxCompanyAddressCount, xeroxCompanyAddressRate) = accuracyOfCompanyAddressOCRResults(FujiXerox)
  val (xeroxTermCount, xeroxTermRate) = accuracyOfTermOCRResults(FujiXerox)

  println(
    fillTemplate(
      adobeNameRate("Total-Correct"), adobeCompanyNameRate("Total-Correct"), adobeCompanyAddressRate("Total-Correct"), adobeTermRate("Total-Correct"),
      adobeNameCount("Total-Correct"), adobeCompanyNameCount("Total-Correct"), adobeCompanyAddressCount("Total-Correct"), adobeTermCount("Total-Correct"),
      adobeNameRate("MS明朝-Correct"), adobeCompanyNameRate("MS明朝-Correct"), adobeCompanyAddressRate("MS明朝-Correct"), adobeTermRate("MS明朝-Correct"),
      adobeNameCount("MS明朝-Correct"), adobeCompanyNameCount("MS明朝-Correct"), adobeCompanyAddressCount("MS明朝-Correct"), adobeTermCount("MS明朝-Correct"),
      adobeNameRate("MS P明朝-Correct"), adobeCompanyNameRate("MS P明朝-Correct"), adobeCompanyAddressRate("MS P明朝-Correct"), adobeTermRate("MS P明朝-Correct"),
      adobeNameCount("MS P明朝-Correct"), adobeCompanyNameCount("MS P明朝-Correct"), adobeCompanyAddressCount("MS P明朝-Correct"), adobeTermCount("MS P明朝-Correct"),
      adobeNameRate("游ゴシック-Correct"), adobeCompanyNameRate("游ゴシック-Correct"), adobeCompanyAddressRate("游ゴシック-Correct"), adobeTermRate("游ゴシック-Correct"),
      adobeNameCount("游ゴシック-Correct"), adobeCompanyNameCount("游ゴシック-Correct"), adobeCompanyAddressCount("游ゴシック-Correct"), adobeTermCount("游ゴシック-Correct"),
      adobeNameRate("メイリオ-Correct"), adobeCompanyNameRate("メイリオ-Correct"), adobeCompanyAddressRate("メイリオ-Correct"), adobeTermRate("メイリオ-Correct"),
      adobeNameCount("メイリオ-Correct"), adobeCompanyNameCount("メイリオ-Correct"), adobeCompanyAddressCount("メイリオ-Correct"), adobeTermCount("メイリオ-Correct")
    )
  )
  println(
    fillTemplate(
      xeroxNameRate("Total-Correct"), xeroxCompanyNameRate("Total-Correct"), xeroxCompanyAddressRate("Total-Correct"), xeroxTermRate("Total-Correct"),
      xeroxNameCount("Total-Correct"), xeroxCompanyNameCount("Total-Correct"), xeroxCompanyAddressCount("Total-Correct"), xeroxTermCount("Total-Correct"),
      xeroxNameRate("MS明朝-Correct"), xeroxCompanyNameRate("MS明朝-Correct"), xeroxCompanyAddressRate("MS明朝-Correct"), xeroxTermRate("MS明朝-Correct"),
      xeroxNameCount("MS明朝-Correct"), xeroxCompanyNameCount("MS明朝-Correct"), xeroxCompanyAddressCount("MS明朝-Correct"), xeroxTermCount("MS明朝-Correct"),
      xeroxNameRate("MS P明朝-Correct"), xeroxCompanyNameRate("MS P明朝-Correct"), xeroxCompanyAddressRate("MS P明朝-Correct"), xeroxTermRate("MS P明朝-Correct"),
      xeroxNameCount("MS P明朝-Correct"), xeroxCompanyNameCount("MS P明朝-Correct"), xeroxCompanyAddressCount("MS P明朝-Correct"), xeroxTermCount("MS P明朝-Correct"),
      xeroxNameRate("游ゴシック-Correct"), xeroxCompanyNameRate("游ゴシック-Correct"), xeroxCompanyAddressRate("游ゴシック-Correct"), xeroxTermRate("游ゴシック-Correct"),
      xeroxNameCount("游ゴシック-Correct"), xeroxCompanyNameCount("游ゴシック-Correct"), xeroxCompanyAddressCount("游ゴシック-Correct"), xeroxTermCount("游ゴシック-Correct"),
      xeroxNameRate("メイリオ-Correct"), xeroxCompanyNameRate("メイリオ-Correct"), xeroxCompanyAddressRate("メイリオ-Correct"), xeroxTermRate("メイリオ-Correct"),
      xeroxNameCount("メイリオ-Correct"), xeroxCompanyNameCount("メイリオ-Correct"), xeroxCompanyAddressCount("メイリオ-Correct"), xeroxTermCount("メイリオ-Correct")
    )
  )
  println(
    fillTemplate(
      tesseractNameRate("Total-Correct"), tesseractCompanyNameRate("Total-Correct"), tesseractCompanyAddressRate("Total-Correct"), tesseractTermRate("Total-Correct"),
      tesseractNameCount("Total-Correct"), tesseractCompanyNameCount("Total-Correct"), tesseractCompanyAddressCount("Total-Correct"), tesseractTermCount("Total-Correct"),
      tesseractNameRate("MS明朝-Correct"), tesseractCompanyNameRate("MS明朝-Correct"), tesseractCompanyAddressRate("MS明朝-Correct"), tesseractTermRate("MS明朝-Correct"),
      tesseractNameCount("MS明朝-Correct"), tesseractCompanyNameCount("MS明朝-Correct"), tesseractCompanyAddressCount("MS明朝-Correct"), tesseractTermCount("MS明朝-Correct"),
      tesseractNameRate("MS P明朝-Correct"), tesseractCompanyNameRate("MS P明朝-Correct"), tesseractCompanyAddressRate("MS P明朝-Correct"), tesseractTermRate("MS P明朝-Correct"),
      tesseractNameCount("MS P明朝-Correct"), tesseractCompanyNameCount("MS P明朝-Correct"), tesseractCompanyAddressCount("MS P明朝-Correct"), tesseractTermCount("MS P明朝-Correct"),
      tesseractNameRate("游ゴシック-Correct"), tesseractCompanyNameRate("游ゴシック-Correct"), tesseractCompanyAddressRate("游ゴシック-Correct"), tesseractTermRate("游ゴシック-Correct"),
      tesseractNameCount("游ゴシック-Correct"), tesseractCompanyNameCount("游ゴシック-Correct"), tesseractCompanyAddressCount("游ゴシック-Correct"), tesseractTermCount("游ゴシック-Correct"),
      tesseractNameRate("メイリオ-Correct"), tesseractCompanyNameRate("メイリオ-Correct"), tesseractCompanyAddressRate("メイリオ-Correct"), tesseractTermRate("メイリオ-Correct"),
      tesseractNameCount("メイリオ-Correct"), tesseractCompanyNameCount("メイリオ-Correct"), tesseractCompanyAddressCount("メイリオ-Correct"), tesseractTermCount("メイリオ-Correct")
    )
  )
}
