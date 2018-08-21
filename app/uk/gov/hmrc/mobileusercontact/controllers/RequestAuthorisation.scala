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

import play.api.mvc.Results.{Forbidden, Unauthorized}
import play.api.mvc.{Request, Result}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{EmptyRetrieval, Retrievals}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait RequestAuthorisation {

  def authConnector: AuthConnector

  protected final val predicates: Predicate = ConfidenceLevel.L200

  def recover(r:Future[Result])(implicit hc:HeaderCarrier, ec: ExecutionContext): Future[Result] = {
    r.recover {
      case e: NoActiveSession => Unauthorized(s"Authorisation failure [${e.reason}]")
      case e: InsufficientConfidenceLevel => Forbidden(s"Authorisation failure [${e.reason}]")
      case e: AuthorisationException => Forbidden(s"Authorisation failure [${e.reason}]")
    }
  }
}


trait SupportRequestAuthorisation extends RequestAuthorisation {

  def authoriseSupportRequest[A](request: Request[A])(block: Request[_] => Future[Result])
                                (implicit hc:HeaderCarrier, ec:ExecutionContext): Future[Result] = {

    recover( authConnector.authorise(predicates, EmptyRetrieval).flatMap(_ => block(request)) )
  }
}

trait FeedbackRequestAuthorisation extends RequestAuthorisation {

  def authoriseAndEnrichFeedbackRequest[A](request: Request[A])(block: RequestWithName[_] => Future[Result])(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[Result] = {
    recover {
      authConnector.authorise(predicates, Retrievals.itmpName) flatMap {
        name => block(new RequestWithName(name, request))
      }
    }
  }
}

