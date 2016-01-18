import org.specs2._

import java.time.ZonedDateTime
import java.util.UUID

import org.mongodb.scala.bson._

import models._

class MongoConversionsSpec extends Specification { def is=s2"""

  Mongo should

    convert an event into a MongoDB document              $fromEvent
    convert an event with no U-A into a MongoDB document  $fromEvent2
"""
  
  def fromEvent = {
    val _id = UUID.randomUUID
    val uuid = UUID.randomUUID
    val userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0"
    val datetime = ZonedDateTime.now

    val event = BrowseEvent(
      uuid,
      "/path",
      "en",
      (1440, 900),
      "192.168",
      Some(userAgent),
      datetime
    )

    MongoConversions.fromEvent(event, _id) === Document(
      "_id" -> _id.toString,
      "browserId" -> uuid.toString,
      "path" -> "/path",
      "language" -> "en",
      "screen" -> Document(
        "x" -> 1440,
        "y" -> 900
      ),
      "ip" -> "192.168",
      "userAgent" -> userAgent,
      "datetime" -> datetime.toString
    )
  }
  
  def fromEvent2 = {
    val _id = UUID.randomUUID
    val uuid = UUID.randomUUID
    val datetime = ZonedDateTime.now

    val event = BrowseEvent(
      uuid,
      "/path",
      "en",
      (1440, 900),
      "192.168",
      None,
      datetime
    )

    MongoConversions.fromEvent(event, _id) === Document(
      "_id" -> _id.toString,
      "browserId" -> uuid.toString,
      "path" -> "/path",
      "language" -> "en",
      "screen" -> Document(
        "x" -> 1440,
        "y" -> 900
      ),
      "ip" -> "192.168",
      "userAgent" -> None,
      "datetime" -> datetime.toString
    )
  }
}
