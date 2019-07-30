/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.mobileusercontact.controllers

import java.util.UUID.randomUUID

import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.{status, _}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission
import uk.gov.hmrc.mobileusercontact.services.Feedback
import uk.gov.hmrc.mobileusercontact.test.FeedbackTestData

import scala.concurrent.ExecutionContext.Implicits.global

class FeedbackControllerSpec extends PlaySpec with DefaultAwaitTimeout with MockFactory with FeedbackTestData {

  private val journeyId = randomUUID().toString

  "submitFeedback" must {
    "ensure user is logged in by checking permissions using Authorised" in {
      val service    = mock[Feedback]
      val controller = new FeedbackController(service, NeverAuthorised, stubControllerComponents())
      status(controller.submitFeedback(journeyId)(FakeRequest().withBody[FeedbackSubmission](appFeedback))) mustBe FORBIDDEN
    }
  }
}
