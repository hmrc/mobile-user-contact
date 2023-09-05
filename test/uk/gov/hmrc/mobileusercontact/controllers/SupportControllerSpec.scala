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
import uk.gov.hmrc.mobileusercontact.domain.SupportRequest
import uk.gov.hmrc.mobileusercontact.services.Support
import uk.gov.hmrc.mobileusercontact.test.{BaseSpec, SupportTestData}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper

class SupportControllerSpec extends BaseSpec with SupportTestData {

  "requestSupport" should {

    "reject support requests for users that have an insufficient confidence level" in {
      val service: Support = stub[Support]
      val controller = new SupportController(service, NeverAuthorised, stubControllerComponents())
      status(controller.requestSupport(journeyId)(FakeRequest().withBody[SupportRequest](supportTicket))) mustBe FORBIDDEN
    }
  }
}
