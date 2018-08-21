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
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission
import uk.gov.hmrc.mobileusercontact.services.Feedback
import uk.gov.hmrc.play.HeaderCarrierConverter.fromHeadersAndSession
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext.fromLoggingDetails

import scala.concurrent.Future

@Singleton
class FeedbackController @Inject() (
  service: Feedback,
  override val authConnector: AuthConnector
) extends BaseController with FeedbackRequestAuthorisation {

  val submitFeedback: Action[FeedbackSubmission] = Action.async(parse.json[FeedbackSubmission])(submitFeedbackTicket)

  private def submitFeedbackTicket(request: Request[FeedbackSubmission]): Future[Result] = {

    implicit val headerCarrier: HeaderCarrier = fromHeadersAndSession(request.headers)

    authoriseAndEnrichFeedbackRequest(request) {
      requestWithItmpName => service.submitFeedback(request.body, requestWithItmpName.itmpName).map(_ => NoContent)
    }
  }
}

