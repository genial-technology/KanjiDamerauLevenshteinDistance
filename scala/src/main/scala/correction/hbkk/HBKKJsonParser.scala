package correction.hbkk

import java.io.FileInputStream
import java.nio.file.Paths

import kdld.element.{AHBKK, Kanji, Step, Term}
import play.api.libs.json.{JsArray, JsResult, JsValue, Json}

import scala.collection.mutable

object HBKKJsonParser extends App {
  println(new HBKKJsonParser().getTerm("往文書").kanjiList.head.steps.head.listOfHBKKAndNumOfStrokes.head.asInstanceOf[AHBKK].codePoint)
}

class HBKKJsonParser() {
  private val kanjiHBKKMap = mutable.Map.empty[String, Kanji]
  private val path = Paths.get("src/main/resources/dic/hbkk.json")
  private val json: JsValue = Json.parse(new FileInputStream(path.toFile))
  //var i = 0
  json.asOpt[JsArray] match {
    case Some(jsonArray) =>
      jsonArray.value foreach { kanjiElement =>
        //println(i)
        //i += 1
        val original = (kanjiElement \ "original").as[String]
        kanjiHBKKMap(original) = {
          Kanji({
            for (stepElement <- (kanjiElement \ "steps").as[JsArray].value) yield {
              //(stepElement \ "step").as[Int]
              Step({
                for (hbkkElement <- (stepElement \ "hbkkList").as[JsArray].value) yield {
                  AHBKK(
                    (hbkkElement \ "hbkk").as[String].codePointAt(0),
                    (hbkkElement \ "numOfStrokes").as[Int].toShort
                  )
                }
                }.toArray)
            }
            }.toArray, original.codePointAt(0))
        }
/*
        val jsResult: JsResult[JsonKanji] = kanjiElement.validate[JsonKanji]
        jsResult.asOpt foreach { jsonKanji: JsonKanji =>
          //println(jsonKanji.original)
          System.err.println(jsonKanji.steps)
          System.err.println(jsonKanji.original)
          val kanji = new Kanji(jsonKanji.steps.map { step: JsonStep =>
            Step(step.hbkkList.map { hbkk: JsonHBKK =>
              AHBKK(hbkk.hbkk.codePointAt(0), hbkk.numOfStrokes.toShort)
            }.toArray)
          }.toArray, jsonKanji.original.codePointAt(0))
          kanjiHBKKMap(jsonKanji.original) = kanji
        }

 */
      }
    case None =>
      // Do nothing
  }

  private def getKanji(kanji: String): Option[Kanji] = {
    if (kanjiHBKKMap.contains(kanji)) {
      Option(kanjiHBKKMap(kanji))
    } else {
      None
    }
  }

  def getTerm(term: String): Term = {
    Term(term.codePoints.toArray.map { codePoint: Int =>
      val c = new String(Array[Int](codePoint), 0, 1)
      getKanji(c).getOrElse(Kanji(Array.empty, codePoint))
    })
  }
}
