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
import play.api.libs.json.Json
import uk.gov.hmrc.mobileusercontact.test.FeedbackTestData

class CSATFeedbackSpec extends WordSpec with Matchers with FeedbackTestData  {

  "correctly write CSATFeedback model to JSON" in {
    Json.toJson(csatFeedbackModel) shouldBe csatFeedbackJson
  }

  "correctly read a feedback model from json" in {
    csatFeedbackJson.as[CSATFeedback] shouldBe csatFeedbackModel
  }

  "correctly write CSATFeedback model to JSON when no option fields are present" in {
    Json.toJson(emptyCsatFeedbackModel) shouldBe emptyCsatFeedbackJson
  }

  "correctly read a CSATFeedback model from json when no optional fields are present" in {
    emptyCsatFeedbackJson.as[CSATFeedback] shouldBe emptyCsatFeedbackModel
  }
}
