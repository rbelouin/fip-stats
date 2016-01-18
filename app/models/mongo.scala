package models

import play.api._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import org.mongodb.scala._

object Mongo {
  import Play.current

  val conf = Play.configuration

  val mongoUri = conf.getString("mongo.uri").get
  val mongoDb = conf.getString("mongo.db").get
  val mongoCollection = conf.getString("mongo.collection").get

  val client = MongoClient(mongoUri)
  val database = client.getDatabase(mongoDb)
  val collection = database.getCollection(mongoCollection)


  def insertOne(d: Document): Future[Unit] = {
    collection.insertOne(d).head().map(_ => ())
  }
}
