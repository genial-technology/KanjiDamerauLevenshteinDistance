package correction

import java.io.PrintWriter
import java.nio.file.{Files, Path, Paths}

import correction.ranking.{CompanyAddressDLRanking, CompanyAddressDLRankingSmallVersion, CompanyAddressJRanking, CompanyAddressJRankingSmallVersion, CompanyAddressJWRanking, CompanyAddressJWRankingSmallVersion, CompanyAddressKDLRankingSmallVersion, CompanyAddressLRanking, CompanyAddressLRankingSmallVersion, CompanyNameDLRanking, CompanyNameDLRankingSmallVersion, CompanyNameJRanking, CompanyNameJRankingSmallVersion, CompanyNameJWRanking, CompanyNameJWRankingSmallVersion, CompanyNameKDLRankingSmallVersion, CompanyNameLRanking, CompanyNameLRankingSmallVersion, FirstNameDLRanking, FirstNameJRanking, FirstNameJWRanking, FirstNameKDLRanking, FirstNameLRanking, SurNameDLRanking, SurNameJRanking, SurNameJWRanking, SurNameKDLRanking, SurNameLRanking, TermDLRanking, TermJRanking, TermJWRanking, TermKDLRanking, TermLRanking}
import evaluation.{AdobeAcrobat, FujiXerox, OCR, Tesseract}
import normalization.Normalizer

import scala.collection.mutable
import scala.io.Source

object Main extends App {
  //private val companyNameDLRanker = new CompanyNameDLRanking(1)
  //private val companyNameJWRanker = new CompanyNameJWRanking(1)

  private def fillTemplate(
                   dlNameRate: String, dlNameCount: Int,
                   dlCompanyNameRate: String, dlCompanyNameCount: Int,
                   dlCompanyAddressRate: String, dlCompanyAddressCount: Int,
                   dlTermRate: String, dlTermCount: Int,
                   lNameRate: String, lNameCount: Int,
                   lCompanyNameRate: String, lCompanyNameCount: Int,
                   lCompanyAddressRate: String, lCompanyAddressCount: Int,
                   lTermRate: String, lTermCount: Int,
                   jwNameRate: String, jwNameCount: Int,
                   jwCompanyNameRate: String, jwCompanyNameCount: Int,
                   jwCompanyAddressRate: String, jwCompanyAddressCount: Int,
                   jwTermRate: String, jwTermCount: Int,
                   jNameRate: String, jNameCount: Int,
                   jCompanyNameRate: String, jCompanyNameCount: Int,
                   jCompanyAddressRate: String, jCompanyAddressCount: Int,
                   jTermRate: String, jTermCount: Int
                  ): String = {
    raw"""         & Kanji-DL & 0.500 (550/1,100) & 0.500 (550/1,100) & 0.500 (550/1,100) & 0.500 (550/1,100) \\
         |         & DL & $dlNameRate ($dlNameCount/1,100) & $dlCompanyNameRate ($dlCompanyNameCount/1,100) & $dlCompanyAddressRate ($dlCompanyAddressCount/1,100) & $dlTermRate ($dlTermCount/1,100) \\
         |         & L & $lNameRate ($lNameCount/1,100) & $lCompanyNameRate ($lCompanyNameCount/1,100) & $lCompanyAddressRate ($lCompanyAddressCount/1,100) & $lTermRate ($lTermCount/1,100) \\
         |         & JW & $jwNameRate ($jwNameCount/1,100) & $jwCompanyNameRate ($jwCompanyNameCount/1,100) & $jwCompanyAddressRate ($jwCompanyAddressCount/1,100) & $jwTermRate ($jwTermCount/1,100) \\
         |         & J & $jNameRate ($jNameCount/1,100) & $jCompanyNameRate ($jCompanyNameCount/1,100) & $jCompanyAddressRate ($jCompanyAddressCount/1,100) & $jTermRate ($jTermCount/1,100) \\
         |""".stripMargin
  }

  def accuracyOfOCRErrorCorrectionResults(errorTsv: Path,
                                          errorCorrectionTsvFormat: String,
                                          dataType: DataType): (Map[String, Int], Map[String, String]) = {
    println("---")
    if (errorTsv.toFile.canRead) {
      val fileName: String = errorTsv.getFileName.toString
      println(fileName)
      val kdlWriter = new PrintWriter(Files.newBufferedWriter(Paths.get(errorCorrectionTsvFormat.format("KDL"))))
      val dlWriter = new PrintWriter(Files.newBufferedWriter(Paths.get(errorCorrectionTsvFormat.format("DL"))))
      val jWriter = new PrintWriter(Files.newBufferedWriter(Paths.get(errorCorrectionTsvFormat.format("J"))))
      val jwWriter = new PrintWriter(Files.newBufferedWriter(Paths.get(errorCorrectionTsvFormat.format("JW"))))
      val lWriter = new PrintWriter(Files.newBufferedWriter(Paths.get(errorCorrectionTsvFormat.format("L"))))
      val source = Source.fromFile(errorTsv.toFile)
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
        println(line)
        val tokens = line.split('\t')
        if (tokens.length == 4) {
          //val number: String = tokens.head.trim
          val goldStandard: String = tokens(1).trim
          val font: String = tokens(2).trim
          val output: String = tokens.last.trim
          val normalizedGoldStandard: String = Normalizer.normalize(goldStandard)
          val normalizedOutput: String = Normalizer.normalize(output)
          dataType match {
            case CompanyAddress =>
              val addressKDLRanker = new CompanyAddressKDLRankingSmallVersion(1)
              val kdlResults = addressKDLRanker.result(normalizedOutput)
              val (kdlCorrection: String, kdlSimilarity) = kdlResults.head
              val normalizedKDLCorrection: String = Normalizer.normalize(kdlCorrection)
              if (normalizedKDLCorrection != normalizedGoldStandard) {
                countUp("KDL")
                kdlWriter.println(line + s"\t$kdlCorrection")
                System.err.println(line + s"\t$kdlCorrection")
              }
              /*
              val addressDLRanker = new CompanyAddressDLRankingSmallVersion
              val dlResults = addressDLRanker.result(normalizedOutput)
              val (dlCorrection: String, dlSimilarity) = dlResults.head
              val normalizedDLCorrection: String = Normalizer.normalize(dlCorrection)
              if (normalizedDLCorrection != normalizedGoldStandard) {
                countUp("DL")
                dlWriter.println(line + s"\t$dlCorrection")
                System.err.println(line + s"\t$dlCorrection")
              }

              val addressLRanker = new CompanyAddressLRankingSmallVersion
              val lResults = addressLRanker.result(normalizedOutput)
              val (lCorrection: String, lSimilarity) = lResults.head
              val normalizedLCorrection: String = Normalizer.normalize(lCorrection)
              if (normalizedLCorrection != normalizedGoldStandard) {
                countUp("L")
                lWriter.println(line + s"\t$lCorrection")
                System.err.println(line + s"\t$lCorrection")
              }

              val addressJRanker = new CompanyAddressJRankingSmallVersion
              val jResults = addressJRanker.result(normalizedOutput)
              val (jCorrection: String, jSimilarity) = jResults.head
              val normalizedJCorrection: String = Normalizer.normalize(jCorrection)
              if (normalizedJCorrection != normalizedGoldStandard) {
                countUp("J")
                jWriter.println(line + s"\t$jCorrection")
                System.err.println(line + s"\t$jCorrection")
              }

              val addressJWRanker = new CompanyAddressJWRankingSmallVersion
              val jwResults = addressJWRanker.result(normalizedOutput)
              val (jwCorrection: String, jwSimilarity) = jwResults.head
              val normalizedJWCorrection: String = Normalizer.normalize(jwCorrection)
              if (normalizedJWCorrection != normalizedGoldStandard) {
                countUp("JW")
                jwWriter.println(line + s"\t$jwCorrection")
                System.err.println(line + s"\t$jwCorrection")
              }

               */

              /*
              val addressDLRanker = new CompanyAddressDLRanking(1)
              val (dlPrefectureNameCorrection: String, dlPrefectureNameSimilarity: Double) = addressDLRanker.result(s"$normalizedOutput@prefectureName@日本国").head
              val (dlCityNameCorrection: String, dlCityNameSimilarity: Double) = addressDLRanker.result(s"${normalizedOutput.drop(dlPrefectureNameCorrection.length)}@cityName@$dlPrefectureNameCorrection").head
              val (dlStreetNameCorrection: String, dlStreetNameSimilarity: Double) = addressDLRanker.result(s"${normalizedOutput.drop(dlPrefectureNameCorrection.length + dlCityNameCorrection.length)}@streetName@$dlCityNameCorrection").head
              val dlCorrection: String = s"$dlPrefectureNameCorrection$dlCityNameCorrection$dlStreetNameCorrection"
              val normalizedDLCorrection: String = Normalizer.normalize(dlCorrection)
              if (normalizedDLCorrection != normalizedGoldStandard) {
                countUp("DL")
                dlWriter.println(line + s"\t$dlCorrection")
                System.err.println(line + s"\t$dlCorrection")
              }

               */
              /*
              val addressJRanker = new CompanyAddressJRanking(1)
              val (jPrefectureNameCorrection: String, jPrefectureNameSimilarity: Double) = addressJRanker.result(s"$normalizedOutput@prefectureName@日本国").head
              val (jCityNameCorrection: String, jCityNameSimilarity: Double) = addressJRanker.result(s"${normalizedOutput.drop(jPrefectureNameCorrection.length)}@cityName@$jPrefectureNameCorrection").head
              val (jStreetNameCorrection: String, jStreetNameSimilarity: Double) = addressJRanker.result(s"${normalizedOutput.drop(jPrefectureNameCorrection.length + jCityNameCorrection.length)}@streetName@$jCityNameCorrection").head
              val jCorrection: String = s"$jPrefectureNameCorrection$jCityNameCorrection$jStreetNameCorrection"
              val normalizedJCorrection: String = Normalizer.normalize(jCorrection)
              if (normalizedJCorrection != normalizedGoldStandard) {
                countUp("J")
                jWriter.println(line + s"\t$jCorrection")
                System.err.println(line + s"\t$jCorrection")
              }
               */
              /*
              val addressJWRanker = new CompanyAddressJWRanking(1)
              val (jwPrefectureNameCorrection: String, jwPrefectureNameSimilarity: Double) = addressJWRanker.result(s"$normalizedOutput@prefectureName@日本国").head
              val (jwCityNameCorrection: String, jwCityNameSimilarity: Double) = addressJWRanker.result(s"${normalizedOutput.drop(jwPrefectureNameCorrection.length)}@cityName@$jwPrefectureNameCorrection").head
              val (jwStreetNameCorrection: String, jwStreetNameSimilarity: Double) = addressJWRanker.result(s"${normalizedOutput.drop(jwPrefectureNameCorrection.length + jwCityNameCorrection.length)}@streetName@$jwCityNameCorrection").head
              val jwCorrection: String = s"$jwPrefectureNameCorrection$jwCityNameCorrection$jwStreetNameCorrection"
              val normalizedJWCorrection: String = Normalizer.normalize(jwCorrection)
              if (normalizedJWCorrection != normalizedGoldStandard) {
                countUp("JW")
                jwWriter.println(line + s"\t$jwCorrection")
                System.err.println(line + s"\t$jwCorrection")
              }

               */
              /*
              val addressLRanker = new CompanyAddressLRanking(1)
              val (lPrefectureNameCorrection: String, lPrefectureNameSimilarity: Double) = addressLRanker.result(s"$normalizedOutput@prefectureName@日本国").head
              val (lCityNameCorrection: String, lCityNameSimilarity: Double) = addressLRanker.result(s"${normalizedOutput.drop(lPrefectureNameCorrection.length)}@cityName@$lPrefectureNameCorrection").head
              val (lStreetNameCorrection: String, lStreetNameSimilarity: Double) = addressLRanker.result(s"${normalizedOutput.drop(lPrefectureNameCorrection.length + lCityNameCorrection.length)}@streetName@$lCityNameCorrection").head
              val lCorrection: String = s"$lPrefectureNameCorrection$lCityNameCorrection$lStreetNameCorrection"
              val normalizedLCorrection: String = Normalizer.normalize(lCorrection)
              if (normalizedLCorrection != normalizedGoldStandard) {
                countUp("L")
                lWriter.println(line + s"\t$lCorrection")
                System.err.println(line + s"\t$lCorrection")
              }

               */
            case CompanyName =>
              val companyNameKDLRanker = new CompanyNameKDLRankingSmallVersion(1)
              val kdlResults = companyNameKDLRanker.result(normalizedOutput)
              val (kdlCorrection: String, kdlSimilarity) = kdlResults.head
              val normalizedKDLCorrection: String = Normalizer.normalize(kdlCorrection)
              if (normalizedKDLCorrection != normalizedGoldStandard) {
                countUp("KDL")
                kdlWriter.println(line + s"\t$kdlCorrection")
                System.err.println(line + s"\t$kdlCorrection")
              }

              /*

              val companyNameDLRanker = new CompanyNameDLRankingSmallVersion
              val dlResults = companyNameDLRanker.result(normalizedOutput)
              val (dlCorrection: String, dlSimilarity) = dlResults.head
              val normalizedDLCorrection: String = Normalizer.normalize(dlCorrection)
              if (normalizedDLCorrection != normalizedGoldStandard) {
                countUp("DL")
                dlWriter.println(line + s"\t$dlCorrection")
                System.err.println(line + s"\t$dlCorrection")
              }

              val companyNameJRanker = new CompanyNameJRankingSmallVersion
              val (jCorrection: String, jSimilarity) = companyNameJRanker.result(normalizedOutput).head
              val normalizedJCorrection: String = Normalizer.normalize(jCorrection)
              if (normalizedJCorrection != normalizedGoldStandard) {
                countUp("J")
                jWriter.println(line + s"\t$jCorrection")
                System.err.println(line + s"\t$jCorrection")
              }

              val companyNameJWRanker = new CompanyNameJWRankingSmallVersion
              val (jwCorrection: String, jwSimilarity) = companyNameJWRanker.result(normalizedOutput).head
              val normalizedJWCorrection: String = Normalizer.normalize(jwCorrection)
              if (normalizedJWCorrection != normalizedGoldStandard) {
                countUp("JW")
                jwWriter.println(line + s"\t$jwCorrection")
                System.err.println(line + s"\t$jwCorrection")
              }

              val companyNameLRanker = new CompanyNameLRankingSmallVersion
              val (lCorrection: String, lSimilarity) = companyNameLRanker.result(normalizedOutput).head
              val normalizedLCorrection: String = Normalizer.normalize(lCorrection)
              if (normalizedLCorrection != normalizedGoldStandard) {
                countUp("L")
                lWriter.println(line + s"\t$lCorrection")
                System.err.println(line + s"\t$lCorrection")
              }

               */
            case Name =>
              val surNameKDLRanker = new SurNameKDLRanking(1)
              val (kdlSurNameCorrection: String, kdlSurNameSimilarity: Double) = surNameKDLRanker.result(normalizedOutput).head
              val firstNameKDLRanker = new FirstNameKDLRanking(1)
              val (kdlFirstNameCorrection: String, kdlFirstNameSimilarity: Double) = firstNameKDLRanker.result(normalizedOutput.drop(kdlSurNameCorrection.length)).head
              val kdlCorrection: String = kdlSurNameCorrection.concat(kdlFirstNameCorrection)
              val normalizedKDLCorrection: String = Normalizer.normalize(kdlCorrection)
              if (normalizedKDLCorrection != normalizedGoldStandard) {
                countUp("KDL")
                kdlWriter.println(line + s"\t$kdlCorrection")
                System.err.println(line + s"\t$kdlCorrection")
              }

              /*
              val surNameDLRanker = new SurNameDLRanking(1)
              val (dlSurNameCorrection: String, dlSurNameSimilarity: Double) = surNameDLRanker.result(normalizedOutput).head
              val firstNameDLRanker = new FirstNameDLRanking(1)
              val (dlFirstNameCorrection: String, dlFirstNameSimilarity: Double) = firstNameDLRanker.result(normalizedOutput.drop(dlSurNameCorrection.length)).head
              val dlCorrection: String = dlSurNameCorrection.concat(dlFirstNameCorrection)
              val normalizedDLCorrection: String = Normalizer.normalize(dlCorrection)
              if (normalizedDLCorrection != normalizedGoldStandard) {
                countUp("DL")
                dlWriter.println(line + s"\t$dlCorrection")
                System.err.println(line + s"\t$dlCorrection")
              }

              val surNameJRanker = new SurNameJRanking(1)
              val (jSurNameCorrection: String, jSurNameSimilarity: Double) = surNameJRanker.result(normalizedOutput).head
              val firstNameJRanker = new FirstNameJRanking(1)
              val (jFirstNameCorrection: String, jFirstNameSimilarity: Double) = firstNameJRanker.result(normalizedOutput.drop(jSurNameCorrection.length)).head
              val jCorrection: String = jSurNameCorrection.concat(jFirstNameCorrection)
              val normalizedJCorrection: String = Normalizer.normalize(jCorrection)
              if (normalizedJCorrection != normalizedGoldStandard) {
                countUp("J")
                jWriter.println(line + s"\t$jCorrection")
                System.err.println(line + s"\t$jCorrection")
              }

              val surNameJWRanker = new SurNameJWRanking(1)
              val (jwSurNameCorrection: String, jwSurNameSimilarity: Double) = surNameJWRanker.result(normalizedOutput).head
              val firstNameJWRanker = new FirstNameJWRanking(1)
              val (jwFirstNameCorrection: String, jwFirstNameSimilarity: Double) = firstNameJWRanker.result(normalizedOutput.drop(jwSurNameCorrection.length)).head
              val jwCorrection: String = jwSurNameCorrection.concat(jwFirstNameCorrection)
              val normalizedJWCorrection: String = Normalizer.normalize(jwCorrection)
              if (normalizedJWCorrection != normalizedGoldStandard) {
                countUp("JW")
                jwWriter.println(line + s"\t$jwCorrection")
                System.err.println(line + s"\t$jwCorrection")
              }

              val surNameLRanker = new SurNameLRanking(1)
              val (lSurNameCorrection: String, lSurNameSimilarity: Double) = surNameLRanker.result(normalizedOutput).head
              val firstNameLRanker = new FirstNameLRanking(1)
              val (lFirstNameCorrection: String, lFirstNameSimilarity: Double) = firstNameLRanker.result(normalizedOutput.drop(lSurNameCorrection.length)).head
              val lCorrection: String = lSurNameCorrection.concat(lFirstNameCorrection)
              val normalizedLCorrection: String = Normalizer.normalize(lCorrection)
              if (normalizedLCorrection != normalizedGoldStandard) {
                countUp("L")
                lWriter.println(line + s"\t$lCorrection")
                System.err.println(line + s"\t$lCorrection")
              }

               */

            case Term =>

              val termKDLRanker = new TermKDLRanking(1)
              val (kdlCorrection: String, kdlSimilarity: Double) = termKDLRanker.result(normalizedOutput).head
              val normalizedKDLCorrection: String = Normalizer.normalize(kdlCorrection)
              if (normalizedKDLCorrection != normalizedGoldStandard) {
                countUp("KDL")
                kdlWriter.println(line + s"\t$kdlCorrection")
                System.err.println(line + s"\t$kdlCorrection")
              }
/*
              val termDLRanker = new TermDLRanking(1)
              val (dlCorrection: String, dlSimilarity: Double) = termDLRanker.result(normalizedOutput).head
              val normalizedDLCorrection: String = Normalizer.normalize(dlCorrection)
              if (normalizedDLCorrection != normalizedGoldStandard) {
                countUp("DL")
                dlWriter.println(line + s"\t$dlCorrection")
                System.err.println(line + s"\t$dlCorrection")
              }

              val termJRanker = new TermJRanking(1)
              val (jCorrection: String, jSimilarity: Double) = termJRanker.result(normalizedOutput).head
              val normalizedJCorrection: String = Normalizer.normalize(jCorrection)
              if (normalizedJCorrection != normalizedGoldStandard) {
                countUp("J")
                jWriter.println(line + s"\t$jCorrection")
                System.err.println(line + s"\t$jCorrection")
              }

              val termJWRanker = new TermJWRanking(1)
              val (jwCorrection: String, jwSimilarity: Double) = termJWRanker.result(normalizedOutput).head
              val normalizedJWCorrection: String = Normalizer.normalize(jwCorrection)
              if (normalizedJWCorrection != normalizedGoldStandard) {
                countUp("JW")
                jwWriter.println(line + s"\t$jwCorrection")
                System.err.println(line + s"\t$jwCorrection")
              }

              val termLRanker = new TermLRanking(1)
              val (lCorrection: String, lSimilarity: Double) = termLRanker.result(normalizedOutput).head
              val normalizedLCorrection: String = Normalizer.normalize(lCorrection)
              if (normalizedLCorrection != normalizedGoldStandard) {
                countUp("L")
                lWriter.println(line + s"\t$lCorrection")
                System.err.println(line + s"\t$lCorrection")
              }
 */
          }

        } else {
          System.err.println("Error:1")
          System.err.println(line)
        }
      }
      source.close()
      dlWriter.close()
      jWriter.close()
      jwWriter.close()
      lWriter.close()
      //results("DL") = 275 - results("DL")
      //results("L") = 275 - results("L")
      //results("JW") = 275 - results("JW")
      //results("J") = 275 - results("J")

      //accuracies("DL") = "%.3f".format(results("DL").toDouble / 275)
      //accuracies("J") = "%.3f".format(results("J").toDouble / 275)
      //accuracies("JW") = "%.3f".format(results("JW").toDouble / 275)
      //accuracies("L") = "%.3f".format(results("L").toDouble / 275)
      (results.toMap, accuracies.toMap)
    } else {
      (Map(), Map())
    }
  }

  private def accuracyOfCompanyAddressOCRErrorCorrectionResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRErrorCorrectionResults(ocr.companyAddressErrorPath, ocr.companyAddressErrorCorrectionFormat, CompanyAddress)
  }

  private def accuracyOfCompanyNameOCRErrorCorrectionResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRErrorCorrectionResults(ocr.companyNameErrorPath, ocr.companyNameErrorCorrectionFormat, CompanyName)
  }

  private def accuracyOfNameOCRErrorCorrectionResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRErrorCorrectionResults(ocr.nameErrorPath, ocr.nameErrorCorrectionFormat, Name)
  }

  private def accuracyOfTermOCRErrorCorrectionResults(ocr: OCR): (Map[String, Int], Map[String, String]) = {
    accuracyOfOCRErrorCorrectionResults(ocr.termErrorPath, ocr.termErrorCorrectionFormat, Term)
  }

  //val (adobeNameCount, adobeNameRate) = accuracyOfNameOCRErrorCorrectionResults(AdobeAcrobat)
  val (adobeCompanyNameCount, adobeCompanyNameRate) = accuracyOfCompanyNameOCRErrorCorrectionResults(AdobeAcrobat)
  //val (adobeCompanyAddressCount, adobeCompanyAddressRate) = accuracyOfCompanyAddressOCRErrorCorrectionResults(AdobeAcrobat)
  //val (adobeTermCount, adobeTermRate) = accuracyOfTermOCRErrorCorrectionResults(AdobeAcrobat)
/*
  println(
    fillTemplate(
      adobeNameRate("DL"), adobeNameCount("DL"),
      adobeCompanyNameRate("DL"), adobeCompanyNameCount("DL"),
      adobeCompanyAddressRate("DL"), adobeCompanyAddressCount("DL"),
      adobeTermRate("DL"), adobeTermCount("DL"),
      adobeNameRate("L"), adobeNameCount("L"),
      adobeCompanyNameRate("L"), adobeCompanyNameCount("L"),
      adobeCompanyAddressRate("L"), adobeCompanyAddressCount("L"),
      adobeTermRate("L"), adobeTermCount("L"),
      adobeNameRate("JW"), adobeNameCount("JW"),
      adobeCompanyNameRate("JW"), adobeCompanyNameCount("JW"),
      adobeCompanyAddressRate("JW"), adobeCompanyAddressCount("JW"),
      adobeTermRate("JW"), adobeTermCount("JW"),
      adobeNameRate("J"), adobeNameCount("J"),
      adobeCompanyNameRate("J"), adobeCompanyNameCount("J"),
      adobeCompanyAddressRate("J"), adobeCompanyAddressCount("J"),
      adobeTermRate("J"), adobeTermCount("J")
    )
  )
*/
  //val (xeroxNameCount, xeroxNameRate) = accuracyOfNameOCRErrorCorrectionResults(FujiXerox)
  //val (xeroxCompanyNameCount, xeroxCompanyNameRate) = accuracyOfCompanyNameOCRErrorCorrectionResults(FujiXerox)
  //val (xeroxCompanyAddressCount, xeroxCompanyAddressRate) = accuracyOfCompanyAddressOCRErrorCorrectionResults(FujiXerox)
  //val (xeroxTermCount, xeroxTermRate) = accuracyOfTermOCRErrorCorrectionResults(FujiXerox)
  /*
  println(
    fillTemplate(
      xeroxNameRate("DL"), xeroxNameCount("DL"),
      xeroxCompanyNameRate("DL"), xeroxCompanyNameCount("DL"),
      xeroxCompanyAddressRate("DL"), xeroxCompanyAddressCount("DL"),
      xeroxTermRate("DL"), xeroxTermCount("DL"),
      xeroxNameRate("L"), xeroxNameCount("L"),
      xeroxCompanyNameRate("L"), xeroxCompanyNameCount("L"),
      xeroxCompanyAddressRate("L"), xeroxCompanyAddressCount("L"),
      xeroxTermRate("L"), xeroxTermCount("L"),
      xeroxNameRate("JW"), xeroxNameCount("JW"),
      xeroxCompanyNameRate("JW"), xeroxCompanyNameCount("JW"),
      xeroxCompanyAddressRate("JW"), xeroxCompanyAddressCount("JW"),
      xeroxTermRate("JW"), xeroxTermCount("JW"),
      xeroxNameRate("J"), xeroxNameCount("J"),
      xeroxCompanyNameRate("J"), xeroxCompanyNameCount("J"),
      xeroxCompanyAddressRate("J"), xeroxCompanyAddressCount("J"),
      xeroxTermRate("J"), xeroxTermCount("J")
    )
  )
  */

  //val (tesseractNameCount, tesseractNameRate) = accuracyOfNameOCRErrorCorrectionResults(Tesseract)
  //val (tesseractCompanyNameCount, tesseractCompanyNameRate) = accuracyOfCompanyNameOCRErrorCorrectionResults(Tesseract)
  //val (tesseractCompanyAddressCount, tesseractCompanyAddressRate) = accuracyOfCompanyAddressOCRErrorCorrectionResults(Tesseract)
  //val (tesseractTermCount, tesseractTermRate) = accuracyOfTermOCRErrorCorrectionResults(Tesseract)
  /*
  println(
    fillTemplate(
      tesseractNameRate("DL"), tesseractNameCount("DL"),
      tesseractCompanyNameRate("DL"), tesseractCompanyNameCount("DL"),
      tesseractCompanyAddressRate("DL"), tesseractCompanyAddressCount("DL"),
      tesseractTermRate("DL"), tesseractTermCount("DL"),
      tesseractNameRate("L"), tesseractNameCount("L"),
      tesseractCompanyNameRate("L"), tesseractCompanyNameCount("L"),
      tesseractCompanyAddressRate("L"), tesseractCompanyAddressCount("L"),
      tesseractTermRate("L"), tesseractTermCount("L"),
      tesseractNameRate("JW"), tesseractNameCount("JW"),
      tesseractCompanyNameRate("JW"), tesseractCompanyNameCount("JW"),
      tesseractCompanyAddressRate("JW"), tesseractCompanyAddressCount("JW"),
      tesseractTermRate("JW"), tesseractTermCount("JW"),
      tesseractNameRate("J"), tesseractNameCount("J"),
      tesseractCompanyNameRate("J"), tesseractCompanyNameCount("J"),
      tesseractCompanyAddressRate("J"), tesseractCompanyAddressCount("J"),
      tesseractTermRate("J"), tesseractTermCount("J")
    )
  )
  */
}
