package uk.gov.hmrc.mobileusercontact.api

import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.mobileusercontact.stubs.AuthStub
import uk.gov.hmrc.mobileusercontact.test.BaseISpec
import play.api.libs.ws.writeableOf_JsValue

class CSATFeedbackISpec extends BaseISpec {

  val csatFeedbackJson: JsObject =
    Json.obj("origin" -> "mobile-paye", "ableToDo" -> true, "howEasyScore" -> 5, "whyGiveScore" -> "It was great", "howDoYouFeelScore" -> 4)

  val csatFeedbackInvalidOriginJson: JsObject = Json.obj("origin"            -> "mobile-push-notifications",
                                                         "ableToDo"          -> true,
                                                         "howEasyScore"      -> 5,
                                                         "whyGiveScore"      -> "It was great",
                                                         "howDoYouFeelScore" -> 4
                                                        )

  s"POST /csat-survey?journeyId=$journeyId" should {

    "return 204 No Content" in {
      AuthStub.confidenceLevelRetrieved()

      val request = wsUrl(s"/csat-survey?journeyId=$journeyId")
        .addHttpHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json", authorisationJsonHeader)

      val response = await(
        request.post(csatFeedbackJson)
      )
      response.status shouldEqual Status.NO_CONTENT

    }

    "return 400 Bad Request if an invalid origin is supplied" in {
      AuthStub.confidenceLevelRetrieved()

      val request = wsUrl(s"/csat-survey?journeyId=$journeyId")
        .addHttpHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json", authorisationJsonHeader)

      val response = await(
        request.post(csatFeedbackInvalidOriginJson)
      )
      response.status shouldEqual Status.BAD_REQUEST

    }
  }
}
