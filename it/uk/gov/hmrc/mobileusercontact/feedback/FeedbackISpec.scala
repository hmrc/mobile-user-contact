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

package uk.gov.hmrc.mobileusercontact.feedback

import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mobileusercontact.stubs.{AuthStub, HelpToSaveStub, HmrcDeskproStub}
import uk.gov.hmrc.mobileusercontact.test.{OneServerPerSuiteWsClient, WireMockSupport}

class FeedbackISpec extends WordSpec with Matchers
 with FutureAwaits with DefaultAwaitTimeout
 with WireMockSupport with OneServerPerSuiteWsClient {

  override implicit lazy val app: Application = appBuilder.build()

  private val feedbackSubmissionJson =
    """
      |{
      |  "email": "testy@example.com",
      |  "message": "I think the app is great",
      |  "signUpForResearch": true,
      |  "town": "Leeds",
      |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
      |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)"
      |}
    """.stripMargin

  "POST /feedback-submissions" should {

    "Use hmrc-deskpro to create a feedback Deskpro ticket" in {
      AuthStub.userIsLoggedIn(Some("Given"), Some("Middle"), Some("Family"))
      HelpToSaveStub.currentUserIsEnrolled()
      HmrcDeskproStub.createFeedbackWillSucceed()

      val response = await(
        wsUrl("/feedback-submissions")
          .withHeaders("Content-Type" -> "application/json")
          .post(feedbackSubmissionJson))

      response.status shouldBe 204

      val messageWithExtras =
        """I think the app is great
           |
           |Contact preference: yes
           |
           |HtS: yes
           |
           |Town: Leeds""".stripMargin

      HmrcDeskproStub.createFeedbackShouldHaveBeenCalled(Json.obj(
        "name" -> "Given Middle Family",
        "email" -> "testy@example.com",
        "subject" -> "App Feedback",
        "message" -> messageWithExtras,
        "referrer" -> "eaded345-4ccd-4c27-9285-cde938bd896d",
        "userAgent" -> "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
        // These empty strings are here because based on reading hmrc-deskpro's code it looks like these fields must be present.
        // iOS app sends them as empty strings to the old native-apps-api-orchestration API too.
        "javascriptEnabled" -> "",
        "authId" ->  "",
        "areaOfTax" ->  "",
        "sessionId" ->  "",
        "rating" -> ""))
    }

    "return 401 if no user is logged in" in {
      AuthStub.userIsNotLoggedIn()
      HelpToSaveStub.currentUserIsEnrolled()
      HmrcDeskproStub.createFeedbackWillSucceed()

      val response = await(
        wsUrl("/feedback-submissions")
          .withHeaders("Content-Type" -> "application/json")
          .post(feedbackSubmissionJson))

      response.status shouldBe 401

      HmrcDeskproStub.createFeedbackShouldNotHaveBeenCalled()
    }

    "return 403 Forbidden when the user is logged in with an insufficient confidence level" in {
      AuthStub.userIsLoggedInWithInsufficientConfidenceLevel()
      HelpToSaveStub.currentUserIsEnrolled()
      HmrcDeskproStub.createFeedbackWillSucceed()

      val response = await(
        wsUrl("/feedback-submissions")
          .withHeaders("Content-Type" -> "application/json")
          .post(feedbackSubmissionJson))

      response.status shouldBe 403

      HmrcDeskproStub.createFeedbackShouldNotHaveBeenCalled()
    }

    "return 502 if hmrc-deskpro returns an error 500" in {
      AuthStub.userIsLoggedIn(Some("Given"), Some("Middle"), Some("Family"))
      HelpToSaveStub.currentUserIsEnrolled()
      HmrcDeskproStub.createFeedbackWillRespondWithInternalServerError()

      val response = await(
        wsUrl("/feedback-submissions")
          .withHeaders("Content-Type" -> "application/json")
          .post(feedbackSubmissionJson))

      response.status shouldBe 502
    }

    "return 502 if help-to-save returns an error 500" in {
      AuthStub.userIsLoggedIn(Some("Given"), Some("Middle"), Some("Family"))
      HelpToSaveStub.enrolmentStatusReturnsInternalServerError()
      HmrcDeskproStub.createFeedbackWillSucceed()

      val response = await(
        wsUrl("/feedback-submissions")
          .withHeaders("Content-Type" -> "application/json")
          .post(feedbackSubmissionJson))

      response.status shouldBe 502
    }
  }
}
