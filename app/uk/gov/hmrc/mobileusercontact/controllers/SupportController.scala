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

package uk.gov.hmrc.mobileusercontact.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, Request, Result}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.mobileusercontact.domain.SupportRequest
import uk.gov.hmrc.mobileusercontact.services.Support
import uk.gov.hmrc.play.HeaderCarrierConverter._
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext.fromLoggingDetails

import scala.concurrent.Future

@Singleton
class SupportController @Inject()(
   service: Support,
   override val authConnector: AuthConnector
) extends BaseController with SupportRequestAuthorisation {

  val submitSupport: Action[SupportRequest] = Action.async(parse.json[SupportRequest])(submitSupportTicket)

  private def submitSupportTicket(r: Request[SupportRequest]): Future[Result] = {
    implicit val hc = fromHeadersAndSession(r.headers)
    authoriseSupportRequest(r) {
      _ => service.submitSupportRequest(r.body).map(_ => NoContent)
    }
  }
}





