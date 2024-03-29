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
import play.api.libs.json.Reads
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.mobileusercontact.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobileusercontact.domain.{FeedbackSubmission, SupportRequest}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.Future._

@Singleton
class SandboxController @Inject() (cc: ControllerComponents) extends BackendController(cc) {

  private def acceptedAction[A](implicit r: Reads[A]): Action[A] = Action.async(parse.json[A]) { implicit request =>
    successful(Accepted)
  }

  def requestSupport(journeyId: JourneyId): Action[SupportRequest]     = acceptedAction[SupportRequest]
  def submitFeedback(journeyId: JourneyId): Action[FeedbackSubmission] = acceptedAction[FeedbackSubmission]
}
