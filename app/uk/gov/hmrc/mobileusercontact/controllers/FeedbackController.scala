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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission
import uk.gov.hmrc.mobileusercontact.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobileusercontact.services.Feedback
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class FeedbackController @Inject() (
  service:     Feedback,
  authorised:  Authorised,
  val cc:      ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def submitFeedback(journeyId: JourneyId): Action[FeedbackSubmission] = Action.async(parse.json[FeedbackSubmission]) {
    implicit request =>
      authorised.authorise(request, Retrievals.itmpName and Retrievals.allEnrolments) {
        case itmpName ~ enrolments =>
          val appFeedback: FeedbackSubmission = request.body

          service.submitFeedback(appFeedback, itmpName, enrolments).map { _ =>
            Accepted
          }
      }
  }
}
