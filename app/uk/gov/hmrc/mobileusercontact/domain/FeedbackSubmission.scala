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

package uk.gov.hmrc.mobileusercontact.domain

import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.auth.core.retrieve.ItmpName
import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproFeedback

case class FeedbackSubmission(
  email: String,
  message: String,
  userAgent: String,
  signUpForResearch: Boolean,
  town: Option[String],
  journeyId: Option[String],
  service: Option[String]
) {
  
  def toDeskpro(
    itmpName: ItmpName,
    enrolledInHelpToSave: Boolean
  ): HmrcDeskproFeedback = {
    val contactMessage =
      s"""
         |
         |Contact preference: ${if (signUpForResearch) "yes" else "no"}""".stripMargin

    val htsMessage = if (enrolledInHelpToSave) {
      """
        |
        |HtS: yes""".stripMargin
    } else {
      ""
    }

    val townMessage = if (signUpForResearch) {
      town.fold("") { t =>
        s"""
           |
           |Town: $t""".stripMargin
      }
    } else {
      ""
    }

    val messageWithExtras = s"$message$contactMessage$htsMessage$townMessage"

    val fullName = Seq(itmpName.givenName, itmpName.middleName, itmpName.familyName).flatten.mkString(" ")

    HmrcDeskproFeedback(
      name = fullName,
      subject = "App Feedback",
      email = email,
      message = messageWithExtras,
      userAgent = userAgent,
      service = service,
      referrer = journeyId.getOrElse(""),
      javascriptEnabled = "",
      authId = "",
      areaOfTax = "",
      sessionId = "",
      rating = ""
    )
  }

}

object FeedbackSubmission {
  implicit val reads: Reads[FeedbackSubmission] = Json.reads[FeedbackSubmission]
}
