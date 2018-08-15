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

package uk.gov.hmrc.mobileusercontact.controllers

import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import play.api.test.Helpers.{status, _}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission
import uk.gov.hmrc.mobileusercontact.services.FeedbackService
import uk.gov.hmrc.mobileusercontact.support.FeedbackTestData

class FeedbackControllerSpec extends WordSpec with Matchers
  with DefaultAwaitTimeout
  with MockFactory
  with FeedbackTestData {

  "submitFeedback" should {
    "ensure user is logged in by checking permissions using AuthorisedWithName" in {
      val service = mock[FeedbackService]
      val controller = new FeedbackController(service, NeverAuthorisedWithName)
      status(controller.submitFeedback()(FakeRequest().withBody[FeedbackSubmission](appFeedback))) shouldBe FORBIDDEN
    }
  }
}