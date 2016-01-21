package controllers

import play.api._
import play.api.mvc._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import java.time.ZonedDateTime
import java.util.UUID

import models._

class EventController extends Controller {

  def addEvent = CorsAction.async(parse.json) { req =>
    val ip =  req.headers.get("X-Forwarded-For")
                .getOrElse(req.remoteAddress)

    val ipPrefix = Ip.removeLastBytes(ip).getOrElse("127.0")

    val userAgent = req.headers.get("User-Agent")
    val datetime = ZonedDateTime.now

    val o_input = EventInput(req.body)

    val o_event = o_input.map {
      input => Event.fromEventInput(input, ipPrefix, userAgent, datetime)
    }

    val o_document = o_event.map {
      event => MongoConversions.fromEvent(event, UUID.randomUUID)
    }

    o_document.fold[Future[Result]](Future(BadRequest)) {
      doc => Mongo.insertOne(doc).map(_ => NoContent)
    }
  }

}
