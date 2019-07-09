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

package uk.gov.hmrc.mobileusercontact.api

import com.eclipsesource.schema.SchemaType
import uk.gov.hmrc.mobileusercontact.json.JsonResource.loadResourceJson
import uk.gov.hmrc.mobileusercontact.json.Schema.banAdditionalProperties

trait FeedbackSubmissionJsonSchema {
  protected val strictRamlFeedbackSubmissionSchema: SchemaType =
    banAdditionalProperties(loadResourceJson("/public/api/conf/1.0/schemas/feedback-submission.json"))
      .as[SchemaType]
}
