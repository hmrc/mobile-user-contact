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

package uk.gov.hmrc.mobileusercontact.api

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.mobileusercontact.json.JsonResource.loadResourceJson
import uk.gov.hmrc.mobileusercontact.scalatest.SchemaMatchers

class JsonExamplesSpec
  extends WordSpec with Matchers
    with FeedbackSubmissionJsonSchema with SupportRequestJsonSchema
    with SchemaMatchers {

  "examples/feedback-submission.json" should {
    "be a valid instance of the schema used in the RAML and not contain undocumented properties" in {
      loadResourceJson("/public/api/conf/1.0/examples/feedback-submission.json") should validateAgainstSchema(strictRamlFeedbackSubmissionSchema)
    }
  }

  "examples/support-request.json" should {
    "be a valid instance of the schema used in the RAML and not contain undocumented properties" in {
      loadResourceJson("/public/api/conf/1.0/examples/support-request.json") should validateAgainstSchema(strictRamlSupportRequestSchema)
    }
  }
}
