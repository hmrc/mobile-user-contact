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

package uk.gov.hmrc.mobileusercontact.domain

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.auth.core.retrieve.ItmpName
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.test.{FeedbackTestData, MockFieldTransformerForTestData}

class FeedbackSubmissionSpec extends WordSpec with Matchers with FeedbackTestData with MockFieldTransformerForTestData {

  "toDeskpro" should {
    "map FeedbackSubmission fields to HmrcDeskproFeedback and use FieldTransformer to populate authId, sessionId and userTaxIdentifiers" in {
      val fieldTransformer = mockFieldTransformerForTestData
      implicit val implicitHc: HeaderCarrier = hc

      appFeedback
        .toDeskpro(fieldTransformer, itmpName, enrolledInHelpToSave = enrolledInHelpToSave, enrolments) shouldBe expectedDeskproFeedback
    }

    """include "HtS: yes" when user is not enrolled in Help to Save""" in {
      val fieldTransformer = mockFieldTransformerForTestData
      implicit val implicitHc: HeaderCarrier = hc

      val deskproFeedback = appFeedback.toDeskpro(fieldTransformer, itmpName, enrolledInHelpToSave = true, enrolments)
      deskproFeedback.message should include("HtS: yes")
      deskproFeedback shouldBe expectedDeskproFeedback.copy(
        message = """It's OK
                    |
                    |HtS: yes""".stripMargin
      )
    }

    """omit "HtS: yes" when user is not enrolled in Help to Save""" in {
      val fieldTransformer = mockFieldTransformerForTestData
      implicit val implicitHc: HeaderCarrier = hc

      appFeedback
        .toDeskpro(fieldTransformer, itmpName, enrolledInHelpToSave = false, enrolments)
        .message should not include "HtS: yes"
    }

    "handle optional fields being None" in {
      val fieldTransformer = mockFieldTransformerForTestData
      implicit val implicitHc: HeaderCarrier = hc

      appFeedback
        .copy(journeyId = None)
        .toDeskpro(fieldTransformer, itmpName, enrolledInHelpToSave = enrolledInHelpToSave, enrolments) shouldBe expectedDeskproFeedback
        .copy(
          referrer = ""
        )
    }

    "format name correctly when middleName is None" in {
      val fieldTransformer = mockFieldTransformerForTestData
      implicit val implicitHc: HeaderCarrier = hc

      appFeedback.toDeskpro(fieldTransformer,
                            Some(ItmpName(givenName = Some("Given"), None, familyName = Some("Family"))),
                            enrolledInHelpToSave = enrolledInHelpToSave,
                            enrolments) shouldBe expectedDeskproFeedback.copy(
        name = "Given Family"
      )
    }
  }

}
