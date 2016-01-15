import org.specs2._

import play.api._
import play.api.libs.json._

import java.time.ZonedDateTime
import java.util.UUID

import models._

class EventSpec extends Specification { def is=s2"""

  EventType should
    
    find the type matching a given string           $applyType
    not find anything if the type does not exist    $applyType2

  EventInput should

    parse an event                                  $applyInput
    not parse an event with a non existing type     $applyInput2

  Event should

    create an event from an input and extra data    $fromEventInput
"""
  
  def applyType = {
    EventType("browse") === Some(BrowseEventType)
  }

  def applyType2 = {
    EventType("IERSTaoe") === None
  }

  def applyInput = {
    val ev = EventInput(JsObject(Seq(
      "type" -> JsString("browse"),
      "browserId" -> JsString("889c38b8-0bb3-467e-9f68-c8e1d71f737d"),
      "path" -> JsString("/radios/fip-radio"),
      "language" -> JsString("en"),
      "screen" -> JsObject(Seq(
        "x" -> JsNumber(1400),
        "y" -> JsNumber(900)
      ))
    )))

    ev === Some(BrowseEventInput(
      UUID.fromString("889c38b8-0bb3-467e-9f68-c8e1d71f737d"),
      "/radios/fip-radio",
      "en",
      (1400, 900)
    ))
  }

  def applyInput2 = {
    val ev = EventInput(JsObject(Seq(
      "type" -> JsString("do-not-exist"),
      "browserId" -> JsString("889c38b8-0bb3-467e-9f68-c8e1d71f737d"),
      "path" -> JsString("/radios/fip-radio"),
      "language" -> JsString("en"),
      "screen" -> JsObject(Seq(
        "x" -> JsNumber(1400),
        "y" -> JsNumber(900)
      ))
    )))

    ev === None
  }

  def fromEventInput = {
    val uuid = UUID.randomUUID
    val datetime = ZonedDateTime.now

    val input = BrowseEventInput(
      uuid,
      "/path",
      "en",
      (1440, 900)
    )

    val event = Event.fromEventInput(
      input,
      "192.168",
      Some("Mozilla/5.0 (X11; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0"),
      datetime
    )

    event === BrowseEvent(
      uuid,
      "/path",
      "en",
      (1440, 900),
      "192.168",
      Some("Mozilla/5.0 (X11; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0"),
      datetime
    )
  }
}
