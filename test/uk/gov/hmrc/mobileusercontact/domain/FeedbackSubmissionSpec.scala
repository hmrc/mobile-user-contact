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

package uk.gov.hmrc.mobileusercontact.domain

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.mobileusercontact.test.FeedbackTestData

class FeedbackSubmissionSpec extends WordSpec with Matchers
  with FeedbackTestData {

  "toDeskpro" should {
    "map FeedbackSubmission fields to HmrcDeskproFeedback" in {
      appFeedback.toDeskpro(itmpName, enrolledInHelpToSave = enrolledInHelpToSave) shouldBe expectedDeskproFeedback
    }

    "omit town when signUpForResearch = false" in {
      appFeedback.copy(signUpForResearch = false).toDeskpro(itmpName, enrolledInHelpToSave = enrolledInHelpToSave) shouldBe expectedDeskproFeedback.copy(
        message =
          """It's OK
            |
            |Contact preference: no""".stripMargin
      )
    }

    """include "HtS: yes" when user is not enrolled in Help to Save""" in {
      val deskproFeedback = appFeedback.toDeskpro(itmpName, enrolledInHelpToSave = true)
      deskproFeedback.message should include("HtS: yes")
      deskproFeedback shouldBe expectedDeskproFeedback.copy(
        message =
      """It's OK
        |
        |Contact preference: yes
        |
        |HtS: yes
        |
        |Town: Test town""".stripMargin
      )
    }

    """omit "HtS: yes" when user is not enrolled in Help to Save""" in {
      appFeedback.toDeskpro(itmpName, enrolledInHelpToSave = false).message should not include "HtS: yes"
    }

    "handle optional fields being None" in {
      appFeedback.copy(town = None, journeyId = None).toDeskpro(itmpName, enrolledInHelpToSave = enrolledInHelpToSave) shouldBe expectedDeskproFeedback.copy(
        message =
          """It's OK
            |
            |Contact preference: yes""".stripMargin,
        referrer = ""
      )
    }

    "format name correctly when middleName is None" in {
      appFeedback.toDeskpro(itmpName.copy(middleName = None), enrolledInHelpToSave = enrolledInHelpToSave) shouldBe expectedDeskproFeedback.copy(
        name = "Given Family"
      )
    }

    "use FieldTransformer to populate authId, sessionId and userTaxIdentifiers" is pending
  }

}
