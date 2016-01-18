package models

import java.time.ZonedDateTime
import java.util.UUID

import org.mongodb.scala.bson._

object MongoConversions {

  def fromEvent(event: Event, id: UUID): Document = event match {
    case (be: BrowseEvent) => Document(
      "_id" -> id.toString,
      "browserId" -> be.browserId.toString,
      "path" -> be.path,
      "language" -> be.language,
      "screen" -> Document(
        "x" -> be.screen._1,
        "y" -> be.screen._2
      ),
      "ip" -> be.ip,
      "userAgent" -> be.userAgent,
      "datetime" -> be.datetime.toString
    )
  }
}
