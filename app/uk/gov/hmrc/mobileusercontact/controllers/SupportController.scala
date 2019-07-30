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

package uk.gov.hmrc.mobileusercontact.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.mobileusercontact.domain.SupportRequest
import uk.gov.hmrc.mobileusercontact.services.Support
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class SupportController @Inject()(
  service:     Support,
  authorised:  Authorised,
  cc:          ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def requestSupport(journeyId: String): Action[SupportRequest] = Action.async(parse.json[SupportRequest]) { implicit request =>
    authorised.authorise(request, Retrievals.allEnrolments) { enrolments =>
      service.requestSupport(request.body, enrolments) map (_ => Accepted)
    }
  }
}