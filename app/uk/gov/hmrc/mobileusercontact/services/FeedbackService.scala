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

package uk.gov.hmrc.mobileusercontact.services

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.connectors.{HmrcDeskproConnector, HmrcDeskproFeedback}
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeedbackService  @Inject() (
  hmrcDeskproConnector: HmrcDeskproConnector
){
  def submitFeedback(appFeedback: FeedbackSubmission)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {
    hmrcDeskproConnector.createFeedback(toDeskpro(appFeedback))
  }

  private[services] def toDeskpro(appFeedback: FeedbackSubmission): HmrcDeskproFeedback = {
    val townMessage = if (appFeedback.signUpForResearch) {
      appFeedback.town.fold("") { t =>
        s"""
           |
           |Town: $t""".stripMargin
      }
    } else {
      ""
    }

    val contactMessage =
      s"""
         |
         |Contact preference: ${if (appFeedback.signUpForResearch) "yes" else "no"}""".stripMargin

    val messageWithExtras = s"${appFeedback.message}$contactMessage$townMessage"

    HmrcDeskproFeedback(
      subject = "App Feedback",
      email = appFeedback.email,
      message = messageWithExtras,
      userAgent = appFeedback.userAgent,
      referrer = appFeedback.journeyId.getOrElse(""),
      javascriptEnabled = "",
      authId = "",
      areaOfTax = "",
      sessionId = "",
      rating = ""
    )
  }
}
