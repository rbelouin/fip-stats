package controllers

import play.api._
import play.api.mvc._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import java.time.ZonedDateTime

import models._

class StatController extends Controller {

  def saveStat = Action.async(parse.json) { req =>
    val o_stat = BrowsingStat.fromJson(
      req.body,
      req.headers.get("User-Agent").getOrElse(""),
      req.remoteAddress,
      ZonedDateTime.now
    ).asOpt

    o_stat.fold[Future[Result]](Future(BadRequest))(stat => {
      Stat.saveStat(stat).map(_ => NoContent)
    })
  }

}
