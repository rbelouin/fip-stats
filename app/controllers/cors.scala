package controllers

import play.api._
import play.api.mvc._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

object CorsConf {
  import Play.current

  val origin = Play.configuration.getString("cors.origin").get
  val headers = Seq("Content-Type")
  val methods = Seq("GET", "POST", "PUT", "DELETE", "OPTIONS")
}

class Cors extends Controller {
  def sendHeaders(all: String) = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> CorsConf.origin,
      "Access-Control-Allow-Headers" -> CorsConf.headers.mkString(","),
      "Access-Control-Allow-Methods" -> CorsConf.methods.mkString(",")
    )
  }
}

object CorsAction extends ActionBuilder[Request] {
  override def invokeBlock[A](req: Request[A], block: Request[A] => Future[Result]) = {
    block(req).map(res => res.withHeaders(
      "Access-Control-Allow-Origin" -> CorsConf.origin,
      "Access-Control-Allow-Headers" -> CorsConf.headers.mkString(","),
      "Access-Control-Allow-Methods" -> CorsConf.methods.mkString(",")
    ))
  }
}
