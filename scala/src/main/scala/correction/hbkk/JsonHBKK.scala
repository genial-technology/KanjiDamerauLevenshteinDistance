package correction.hbkk

import play.api.libs.json.{Json, OFormat}

case class JsonHBKK(hbkk: String, numOfStrokes: Int)
object JsonHBKK {
  implicit val jsonFormat: OFormat[JsonHBKK] = Json.format[JsonHBKK]
}