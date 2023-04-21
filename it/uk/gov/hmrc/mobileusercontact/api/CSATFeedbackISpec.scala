package uk.gov.hmrc.mobileusercontact.api

import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSRequest
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mobileusercontact.test.{OneServerPerSuiteWsClient, WireMockSupport}

import java.util.UUID.randomUUID


class CSATFeedbackISpec extends WordSpec
  with Matchers
  with FutureAwaits
  with DefaultAwaitTimeout
  with WireMockSupport
  with OneServerPerSuiteWsClient {

  override implicit lazy val app: Application = appBuilder.build()

  private val journeyId: String      = randomUUID().toString

  val csatFeedbackJson: JsObject = Json.obj(
    "origin" -> "mobile-paye",
    "ableToDo" -> true,
    "howEasyScore" -> 5,
    "whyGiveScore" -> "It was great",
    "howDoYouFeelScore" -> 4)

  "POST /csat-survey?journeyId=$" should {

    "return 204 No Content" in {

      val request: WSRequest = wsUrl(
        s"/csat-survey?journeyId=$journeyId")
      val response = await(request.post(csatFeedbackJson))
      response.status shouldEqual Status.NO_CONTENT

    }


  }
}

