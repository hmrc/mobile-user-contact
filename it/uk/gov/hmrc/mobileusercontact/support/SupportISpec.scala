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

package uk.gov.hmrc.mobileusercontact.support

import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mobileusercontact.stubs.{AuthStub, HmrcDeskproStub}
import uk.gov.hmrc.mobileusercontact.test.{OneServerPerSuiteWsClient, WireMockSupport}

class SupportISpec
  extends WordSpec
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with WireMockSupport
    with OneServerPerSuiteWsClient {

  override implicit lazy val app: Application = appBuilder.build()

  private val supportResourcePath = "/support-requests"

  private val supportRequestJson =
    """
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

    "Use hmrc-deskpro to create a support Deskpro ticket" in {

      AuthStub.userIsLoggedIn()
      HmrcDeskproStub.createSupportTicketWillSucceed()

      val response = postSupportRequest()
      response.status shouldBe 204

      HmrcDeskproStub.createSupportShouldHaveBeenCalled(Json.obj(
        "name" -> "John Smith",
        "email" -> "testy@example.com",
        "subject" -> "App Support Request",
        "message" -> "I can't find my latest payment",
        "referrer" -> "eaded345-4ccd-4c27-9285-cde938bd896d",
        "userAgent" -> "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
        "service" -> "HTS",
        "javascriptEnabled" -> "",
        "authId" ->  "",
        "areaOfTax" ->  "",
        "sessionId" ->  ""))
    }

    "return 401 if no user is logged in" in {
      AuthStub.userIsNotLoggedIn()
      HmrcDeskproStub.createSupportTicketWillSucceed()

      postSupportRequest().status shouldBe 401

      HmrcDeskproStub.createSupportShouldNotHaveBeenCalled()
    }

    "return 403 Forbidden when the user is logged in with an insufficient confidence level" in {
      AuthStub.userIsLoggedInWithInsufficientConfidenceLevel()
      HmrcDeskproStub.createSupportTicketWillSucceed()

      postSupportRequest().status shouldBe 403

      HmrcDeskproStub.createSupportShouldNotHaveBeenCalled()
    }

    "return 502 if hmrc-deskpro returns an error 500" in {
      AuthStub.userIsLoggedIn()
      HmrcDeskproStub.createSupportWillRespondWithInternalServerError()

      postSupportRequest().status shouldBe 502
    }
  }

  private def postSupportRequest(): WSResponse = {
    await(
      wsUrl(supportResourcePath)
        .withHeaders("Content-Type" -> "application/json")
        .post(supportRequestJson))
  }
}
