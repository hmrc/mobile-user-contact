/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.mobileusercontact.domain

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.test.{MockFieldTransformerForTestData, SupportTestData}

class SupportRequestSpec extends WordSpec with Matchers with SupportTestData with MockFieldTransformerForTestData {

  "toDeskpro" should {
    "map SupportRequest fields to HmrcDeskproSupport and use FieldTransformer to populate authId, sessionId and userTaxIdentifiers" in {
      val fieldTransformer = mockFieldTransformerForTestData

      implicit val implicitHc: HeaderCarrier = hc
      supportTicket.toDeskpro(fieldTransformer, enrolments) shouldBe expectedDeskproSupport
    }
  }
}
