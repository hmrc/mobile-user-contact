/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.LoggerLike
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[AuthorisedImpl])
trait Authorised {

  def authorise[B, R](
    request:    Request[B],
    retrievals: Retrieval[R]
  )(block:      R => Future[Result]
  ): Future[Result]
}

@Singleton
class AuthorisedImpl @Inject() (
  logger:        LoggerLike,
  authConnector: AuthConnector
)(implicit ec:   ExecutionContext)
    extends Authorised
    with Results {

  override def authorise[B, R](
    request:    Request[B],
    retrievals: Retrieval[R]
  )(block:      R => Future[Result]
  ): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    val predicates = ConfidenceLevel.L200

    authConnector
      .authorise(predicates, retrievals)
      .flatMap { retrieved =>
        block(retrieved)
      }
      .recover {
        case e: NoActiveSession =>
          val message = s"Authorisation failure - NoActiveSession [${e.reason}]"
          logger.info(message)
          Unauthorized(message)
        case e: InsufficientConfidenceLevel =>
          logger.warn(
            "Forbidding access due to insufficient confidence level. User will see an error screen. To fix this see NGC-3381."
          )
          Forbidden(s"Authorisation failure [${e.reason}]")
        case e: AuthorisationException =>
          val message = s"Authorisation failure [${e.reason}]"
          logger.info(message)
          Forbidden(message)
      }
  }
}
