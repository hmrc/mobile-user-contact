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

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.LoggerLike
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{ItmpName, Retrievals}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext.fromLoggingDetails

import scala.concurrent.Future

class RequestWithName[+A](val itmpName: ItmpName, request: Request[A]) extends WrappedRequest[A](request)

@ImplementedBy(classOf[AuthorisedWithNameImpl])
trait AuthorisedWithName extends ActionBuilder[RequestWithName] with ActionRefiner[Request, RequestWithName]

@Singleton
class AuthorisedWithNameImpl @Inject() (
  logger: LoggerLike,
  authConnector: AuthConnector
) extends AuthorisedWithName with Results {
  override protected def refine[A](request: Request[A]): Future[Either[Result, RequestWithName[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers)

    val predicates = ConfidenceLevel.L200
    val retrievals = Retrievals.itmpName

    authConnector.authorise(predicates, retrievals).map { itmpName =>
      Right(new RequestWithName(itmpName, request))
    }.recover {
      case e: NoActiveSession => Left(Unauthorized(s"Authorisation failure [${e.reason}]"))
      case e: InsufficientConfidenceLevel =>
        logger.warn("Forbidding access due to insufficient confidence level. User will see an error screen. To fix this see NGC-3381.")
        Left(Forbidden(s"Authorisation failure [${e.reason}]"))
      case e: AuthorisationException => Left(Forbidden(s"Authorisation failure [${e.reason}]"))
    }
  }
}