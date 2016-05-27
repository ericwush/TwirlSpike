package model

import play.api.libs.json.Json

object Models {

  case class Record(service: String, description: String, quantity: Int, price: Double, total: Double)
  implicit val recordFormat = Json.format[Record]

  case class Records(records: Seq[Record])
  implicit val recordsFormat = Json.format[Records]

}
