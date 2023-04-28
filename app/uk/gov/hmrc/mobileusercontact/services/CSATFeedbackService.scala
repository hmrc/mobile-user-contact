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

package uk.gov.hmrc.mobileusercontact.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.domain.CSATFeedback
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CSATFeedbackService @Inject()(auditConnector: AuditConnector)(implicit val ec: ExecutionContext) {

  val auditType = "feedback"

  def buildAuditMap(feedback: CSATFeedback): Map[String, String] = {
    val withOrigin: Map[String, String] = Map("origin" -> feedback.origin.toString)
    val ableToDo: Map[String, String] = Map("ableToDo" -> feedback.ableToDo.map(_.toString).getOrElse("-"))
    val howEasyScore: Map[String, String] = Map("howEasyScore" -> feedback.howEasyScore.map(_.toString).getOrElse("-"))
    val whyGiveScore: Map[String, String] = Map("whyGiveScore" -> feedback.whyGiveScore.getOrElse("-"))
    val howDoYouFeelScore: Map[String, String] = Map("howDoYouFeelScore" -> feedback.howDoYouFeelScore.map(_.toString).getOrElse("-"))

    val auditMap = withOrigin ++ ableToDo ++ howEasyScore ++ whyGiveScore ++ howDoYouFeelScore

    auditMap

  }

  def sendAudit(feedback: CSATFeedback)(implicit hc: HeaderCarrier): Future[Unit] = {

    auditConnector.sendExplicitAudit(auditType, buildAuditMap(feedback))

    Future.successful(())
  }
}
