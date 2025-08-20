/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.mobileusercontact.api

import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.mobileusercontact.stubs.{AuthStub, DeskproTicketQueueStub}
import uk.gov.hmrc.mobileusercontact.test.BaseISpec
import play.api.libs.ws.WSBodyWritables.*

class SupportISpec extends BaseISpec {

  private val supportRequestJson = """
                                     |{
                                     |  "name": "John Smith",
                                     |  "email": "testy@example.com",
                                     |  "message": "I can't find my latest payment",
                                     |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
                                     |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
                                     |  "service": "HTS"
                                     |}
                           """.stripMargin

  "POST /support-requests" should {

    "Use deskpro-ticket-queue to create a support Deskpro ticket" in {

      AuthStub.userIsLoggedIn(nino = Some(nino))
      DeskproTicketQueueStub.createSupportTicketWillSucceed()

      val response = await(
        wsUrl(s"/support-requests?journeyId=$journeyId")
          .addHttpHeaders("Content-Type" -> "application/json")
          .addHttpHeaders(HeaderNames.xSessionId -> "test-sessionId")
          .addHttpHeaders(authorisationJsonHeader)
          .post(supportRequestJson)
      )

      response.status shouldBe 202

      DeskproTicketQueueStub.createSupportShouldHaveBeenCalled(
        Json.obj(
          "name"              -> "John Smith",
          "email"             -> "testy@example.com",
          "subject"           -> "App Support Request",
          "message"           -> "I can't find my latest payment",
          "referrer"          -> "eaded345-4ccd-4c27-9285-cde938bd896d",
          "userAgent"         -> "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
          "service"           -> "HTS",
          "javascriptEnabled" -> "",
          // authId is n/a because there is no userId in the session - to inject one we'd have to build & encrypt a Play session cookie
          "authId"    -> "n/a",
          "areaOfTax" -> "",
          "sessionId" -> "test-sessionId",
          "userTaxIdentifiers" -> Json.obj(
            "nino" -> nino.value
          )
        )
      )

      // We avoid retrieving the ITMP name as a minor performance enhancement
      AuthStub.itmpNameShouldNotHaveBeenRetrieved()
    }

    "return 401 if no user is logged in" in {
      AuthStub.userIsNotLoggedIn()
      DeskproTicketQueueStub.createSupportTicketWillSucceed()

      val response = await(
        wsUrl(s"/support-requests?journeyId=$journeyId")
          .addHttpHeaders("Content-Type" -> "application/json")
          .post(supportRequestJson)
      )

      response.status shouldBe 401

      DeskproTicketQueueStub.createSupportShouldNotHaveBeenCalled()
    }

    "return 403 Forbidden when the user is logged in with an insufficient confidence level" in {
      AuthStub.userIsLoggedInWithInsufficientConfidenceLevel()
      DeskproTicketQueueStub.createSupportTicketWillSucceed()

      val response = await(
        wsUrl(s"/support-requests?journeyId=$journeyId")
          .addHttpHeaders("Content-Type" -> "application/json")
          .addHttpHeaders(authorisationJsonHeader)
          .post(supportRequestJson)
      )

      response.status shouldBe 403

      DeskproTicketQueueStub.createSupportShouldNotHaveBeenCalled()
    }

    "return 500 if deskpro-ticket-queue returns an error 500" in {
      AuthStub.userIsLoggedIn()
      DeskproTicketQueueStub.createSupportWillRespondWithInternalServerError()

      val response = await(
        wsUrl(s"/support-requests?journeyId=$journeyId")
          .addHttpHeaders("Content-Type" -> "application/json")
          .addHttpHeaders(authorisationJsonHeader)
          .post(supportRequestJson)
      )

      response.status shouldBe 500
    }

    "return 400 if journeyId not supplied" in {
      AuthStub.userIsNotLoggedIn()
      DeskproTicketQueueStub.createSupportTicketWillSucceed()

      val response = await(
        wsUrl(s"/support-requests")
          .addHttpHeaders("Content-Type" -> "application/json")
          .post(supportRequestJson)
      )

      response.status shouldBe 400
    }

    "return 400 if invalid journeyId supplied" in {
      AuthStub.userIsNotLoggedIn()
      DeskproTicketQueueStub.createSupportTicketWillSucceed()

      val response = await(
        wsUrl(s"/support-requests?journeyId=InvalidJourneyId")
          .addHttpHeaders("Content-Type" -> "application/json")
          .post(supportRequestJson)
      )

      response.status shouldBe 400
    }
  }
}
