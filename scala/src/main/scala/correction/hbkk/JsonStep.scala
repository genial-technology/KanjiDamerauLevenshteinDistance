package correction.hbkk

import play.api.libs.json.{Json, OFormat}

case class JsonStep(step: Int, hbkkList: Seq[JsonHBKK])
object JsonStep {
  implicit val jsonFormat: OFormat[JsonStep] = Json.format[JsonStep]
}