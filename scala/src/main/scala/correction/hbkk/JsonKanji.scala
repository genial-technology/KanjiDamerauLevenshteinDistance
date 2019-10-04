package correction.hbkk

import play.api.libs.json.{Json, OFormat}

case class JsonKanji(original: String,
                     steps: Seq[JsonStep])
object JsonKanji {
  implicit val jsonFormat: OFormat[JsonKanji] = Json.format[JsonKanji]
}