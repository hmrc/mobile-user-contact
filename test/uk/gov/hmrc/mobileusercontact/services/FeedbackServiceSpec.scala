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

package uk.gov.hmrc.mobileusercontact.services

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.connectors.{HmrcDeskproConnector, HmrcDeskproFeedback}
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission

import scala.concurrent.{ExecutionContext, Future}

class FeedbackServiceSpec extends WordSpec with Matchers {

  private val appFeedback = FeedbackSubmission(
    email = "email@example.com",
    message = "It's OK",
    userAgent = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    signUpForResearch = true,
    town = Some("Test town"),
    journeyId = Some("<JourneyID>")
  )

  private val expectedDeskproFeedback = HmrcDeskproFeedback(
    email = "email@example.com",
    message =
      """It's OK
        |
        |Contact preference: yes
        |
        |Town: Test town""".stripMargin,
    userAgent = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    referrer = "<JourneyID>",
    subject = "App Feedback",
    javascriptEnabled = "",
    authId = "",
    areaOfTax = "",
    sessionId = "",
    rating = "")

  private val service = new FeedbackService(new HmrcDeskproConnector {
    //noinspection NotImplementedCode
    override def createFeedback(feedback: HmrcDeskproFeedback)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = ???
  })

  "toDeskpro" should {
    "map FeedbackSubmission fields to HmrcDeskproFeedback" in {
      service.toDeskpro(appFeedback) shouldBe expectedDeskproFeedback
    }

    "omit town when signUpForResearch = false" in {
      service.toDeskpro(appFeedback.copy(signUpForResearch = false)) shouldBe expectedDeskproFeedback.copy(
        message =
          """It's OK
            |
            |Contact preference: no""".stripMargin
      )
    }

    "handle optional fields being None" in {
      service.toDeskpro(appFeedback.copy(town = None, journeyId = None)) shouldBe expectedDeskproFeedback.copy(
        message =
          """It's OK
            |
            |Contact preference: yes""".stripMargin,
        referrer = ""
      )
    }
  }

}
