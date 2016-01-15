package models

import play.api._
import play.api.libs.json._

import java.time.ZonedDateTime
import java.util.UUID

sealed abstract class EventType(val name: String)
case object BrowseEventType extends EventType("browse")

object EventType {
  val values = List(BrowseEventType)

  def apply(name: String) = values.find(_.name == name)
}

sealed trait EventInput

case class BrowseEventInput(
  browserId: UUID,
  path: String,
  language: String,
  screen: (Int, Int)
) extends EventInput

object EventInput {
  implicit val teif = new Format[(Int, Int)] {
    override def reads(json: JsValue) = for {
      obj <- json.validate[JsObject]
      x <- (obj \ "x").validate[Int]
      y <- (obj \ "y").validate[Int]
    } yield (x -> y)

    override def writes(tuple: (Int, Int)) = JsObject(Seq(
      "x" -> JsNumber(tuple._1),
      "y" -> JsNumber(tuple._2)
    ))
  }

  implicit val beif = Json.format[BrowseEventInput]
  
  implicit val eir = new Reads[EventInput] {
    def parse(t: EventType)(json: JsValue) = t match {
      case BrowseEventType => json.validate[BrowseEventInput]
    }

    override def reads(json: JsValue) = for {
      obj <- json.validate[JsObject]
      typeName <- (obj \ "type").validate[String]
      t <- EventType(typeName).fold[JsResult[EventType]](JsError())(JsSuccess(_))
      input <- parse(t)(json)
    } yield input
  }

  val values = List(BrowseEventInput)

  def apply(json: JsValue): Option[EventInput] = {
    json.asOpt[EventInput]
  }
}

sealed trait Event

case class BrowseEvent(
  browserId: UUID,
  path: String,
  language: String,
  screen: (Int, Int),
  ip: String,
  userAgent: Option[String],
  datetime: ZonedDateTime
) extends Event

object Event {
  def fromEventInput(input: EventInput, ip: String, userAgent: Option[String], datetime: ZonedDateTime): Event = input match {
    case (i: BrowseEventInput) => BrowseEvent(
      i.browserId,
      i.path,
      i.language,
      i.screen,
      ip,
      userAgent,
      datetime
    )
  }
}
