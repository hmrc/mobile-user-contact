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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.mobileusercontact.domain.CSATFeedback
import uk.gov.hmrc.mobileusercontact.domain.types.JourneyId
import uk.gov.hmrc.mobileusercontact.services.CSATFeedbackService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CSATFeedbackController @Inject() (
  cc: ControllerComponents,
  service: CSATFeedbackService,
  authorised: Authorised
)(implicit ex: ExecutionContext)
    extends BackendController(cc) {

  def feedback(journeyId: JourneyId): Action[JsValue] =
    Action.async(parse.json) { implicit request =>
      authorised.authorise(request, Retrievals.confidenceLevel) { _ =>
        withJsonBody[CSATFeedback] { response =>
          Json.toJson(response)
          service.sendAudit(response).map { _ =>
            NoContent
          }
        }
      }
    }
}
