package controllers

import play.api._
import play.api.mvc._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class Cors extends Controller {
  def sendHeaders(all: String) = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "http://fip.rbelouin.local:8080",
      "Access-Control-Allow-Headers" -> "Content-Type",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS"
    )
  }
}

object CorsAction extends ActionBuilder[Request] {
  override def invokeBlock[A](req: Request[A], block: Request[A] => Future[Result]) = {
    block(req).map(res => res.withHeaders(
      "Access-Control-Allow-Origin" -> "http://fip.rbelouin.local:8080",
      "Access-Control-Allow-Headers" -> "Content-Type",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS"
    ))
  }
}
