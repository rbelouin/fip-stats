package models

import play.api._
import play.api.libs.json._

import java.time.ZonedDateTime
import java.util.UUID

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Try

import org.mongodb.scala._
import org.mongodb.scala.bson.{BsonDocument, BsonString, BsonInt32}

case class BrowsingStatInput(
  `type`: String,
  browserId: UUID,
  screen: (Int, Int),
  lang: String
) {

  def toBrowsingStat(userAgent: String, ip: String, timestamp: ZonedDateTime) = {
    val ipStart = if(ip.contains(':')) {
      ip.split(':').take(6).mkString(":")
    } else {
      ip.split('.').take(2).mkString(".")
    }

    BrowsingStat(browserId, userAgent, ipStart, screen, lang, timestamp)
  }

}

case class BrowsingStat(
  browserId: UUID,
  userAgent: String,
  ip: String,
  screen: (Int, Int),
  lang: String,
  timestamp: ZonedDateTime
) {

  def toDocument: Document = Document(
    "_id" -> UUID.randomUUID.toString,
    "browserId" -> browserId.toString,
    "userAgent" -> userAgent,
    "ip" -> ip,
    "screen" -> Document("x" -> screen._1, "y" -> screen._2),
    "lang" -> lang,
    "timestamp" -> timestamp.toString
  )

}

object BrowsingStat {
  implicit val tf = new Format[(Int,Int)] {
    override def reads(json: JsValue) = for {
      x <- (json \ "x").validate[Int]
      y <- (json \ "y").validate[Int]
    } yield (x, y)

    override def writes(tuple: (Int, Int)) = tuple match {
      case (x, y) => JsObject(Seq(
        "x" -> JsNumber(x),
        "y" -> JsNumber(y)
      ))
    }
  }

  implicit val bsir = Json.reads[BrowsingStatInput]
  implicit val bsw = Json.writes[BrowsingStat]

  def fromJson(json: JsValue, userAgent: String, ip: String, timestamp: ZonedDateTime) = {
    json.validate[BrowsingStatInput].map {
      input => input.toBrowsingStat(userAgent, ip, timestamp)
    }
  }

  def fromDocument(doc: Document) = for {
    browserId   <- doc.get[BsonString]("browserId").map(_.getValue)
    browserUUID <- Try(UUID.fromString(browserId)).toOption
    userAgent   <- doc.get[BsonString]("userAgent").map(_.getValue)
    ip          <- doc.get[BsonString]("ip").map(_.getValue)
    screen      <- doc.get[BsonDocument]("screen")
    screenX     <- Document(screen).get[BsonInt32]("x").map(_.getValue)
    screenY     <- Document(screen).get[BsonInt32]("y").map(_.getValue)
    lang        <- doc.get[BsonString]("lang").map(_.getValue)
    timestamp_  <- doc.get[BsonString]("timestamp").map(_.getValue)
    timestamp   <- Try(ZonedDateTime.parse(timestamp_)).toOption
  } yield BrowsingStat(
    browserUUID,
    userAgent,
    ip,
    (screenX -> screenY),
    lang,
    timestamp
  )
}

object Stat {
  import Play.current

  val conf = Play.configuration

  val mongoUri = "mongodb://ugpiyivlefqt2pi:CIskeeaFski3umfo4Brf@bshkgz2pcngikkv-mongodb.services.clever-cloud.com:27017/bshkgz2pcngikkv"
  val mongoDb = conf.getString("mongo.db").get
  val mongoCollection = conf.getString("mongo.collection").getOrElse("stats")

  val client = MongoClient(mongoUri)
  val database = client.getDatabase(mongoDb)
  val collection = database.getCollection(mongoCollection)

  def saveStat(stat: BrowsingStat): Future[Unit] = {
    collection.insertOne(stat.toDocument).head().map(_ => ())
  }

}
