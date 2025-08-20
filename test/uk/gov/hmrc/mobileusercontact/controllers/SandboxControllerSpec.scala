/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.http.Status.ACCEPTED
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, stubControllerComponents}
import uk.gov.hmrc.mobileusercontact.domain.{FeedbackSubmission, SupportRequest}
import uk.gov.hmrc.mobileusercontact.test.{BaseSpec, SupportTestData}

class SandboxControllerSpec extends BaseSpec with SupportTestData {

  "SandboxControllerSpec" should {

    "accept support requests for users " in {

      val controller = new SandboxController(stubControllerComponents())
      val result = controller.requestSupport(journeyId)(FakeRequest().withBody[SupportRequest](supportTicket))
      status(result) shouldBe ACCEPTED
    }

    "submit feedback for demo account users" in {

      val controller = new SandboxController(stubControllerComponents())
      val result = controller.submitFeedback(journeyId)(FakeRequest().withBody[FeedbackSubmission](feedback))
      status(result) shouldBe ACCEPTED
    }
  }
}
