/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.test.Helpers._
import play.api.test.FakeRequest
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission
import uk.gov.hmrc.mobileusercontact.services.Feedback
import uk.gov.hmrc.mobileusercontact.test.{BaseSpec, FeedbackTestData}

class FeedbackControllerSpec extends BaseSpec with FeedbackTestData {

  "submitFeedback" should {
    "ensure user is logged in by checking permissions using Authorised" in {
      val service    = mock[Feedback]
      val controller = new FeedbackController(service, NeverAuthorised, stubControllerComponents())
      status(controller.submitFeedback(journeyId)(FakeRequest().withBody[FeedbackSubmission](appFeedback))) shouldBe FORBIDDEN
    }
  }
}
