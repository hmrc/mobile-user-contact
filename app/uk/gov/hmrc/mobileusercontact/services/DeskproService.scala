/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.mobileusercontact.services

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.ItmpName
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.connectors.{HelpToSaveConnector, HmrcDeskproConnector}
import uk.gov.hmrc.mobileusercontact.contactfrontend.FieldTransformer
import uk.gov.hmrc.mobileusercontact.domain.{FeedbackSubmission, SupportRequest}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DeskproService])
trait Feedback {

  def submitFeedback(
    appFeedback: FeedbackSubmission,
    itmpName:    Option[ItmpName],
    enrolments:  Enrolments
  )(implicit hc: HeaderCarrier,
    ec:          ExecutionContext
  ): Future[Unit]
}

@ImplementedBy(classOf[DeskproService])
trait Support {

  def requestSupport(
    appSupportRequest: SupportRequest,
    enrolments:        Enrolments
  )(implicit hc:       HeaderCarrier,
    ec:                ExecutionContext
  ): Future[Unit]
}

@Singleton
class DeskproService @Inject() (
  hmrcDeskproConnector: HmrcDeskproConnector,
  helpToSaveConnector:  HelpToSaveConnector)
    extends Feedback
    with Support {

  override def submitFeedback(
    appFeedback: FeedbackSubmission,
    itmpName:    Option[ItmpName],
    enrolments:  Enrolments
  )(implicit hc: HeaderCarrier,
    ec:          ExecutionContext
  ): Future[Unit] =
    helpToSaveConnector.enrolmentStatus().flatMap { enrolledInHelpToSave =>
      hmrcDeskproConnector.createFeedback(
        appFeedback.toDeskpro(FieldTransformer, itmpName, enrolledInHelpToSave = enrolledInHelpToSave, enrolments)
      )
    }

  override def requestSupport(
    appSupportRequest: SupportRequest,
    enrolments:        Enrolments
  )(implicit hc:       HeaderCarrier,
    ec:                ExecutionContext
  ): Future[Unit] =
    hmrcDeskproConnector.createSupport(appSupportRequest.toDeskpro(FieldTransformer, enrolments))
}
