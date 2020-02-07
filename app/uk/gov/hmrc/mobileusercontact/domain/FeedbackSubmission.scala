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

package uk.gov.hmrc.mobileusercontact.domain

import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.ItmpName
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproFeedback
import uk.gov.hmrc.mobileusercontact.contactfrontend.FieldTransformer

case class FeedbackSubmission(
  email:             String,
  message:           String,
  userAgent:         String,
  signUpForResearch: Boolean,
  town:              Option[String],
  journeyId:         Option[String]) {

  def toDeskpro(
    fieldTransformer:     FieldTransformer,
    itmpName:             Option[ItmpName],
    enrolledInHelpToSave: Boolean,
    enrolments:           Enrolments
  )(implicit hc:          HeaderCarrier
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

    val fullName = itmpName.map(name => Seq(name.givenName, name.middleName, name.familyName).flatten.mkString(" "))

    HmrcDeskproFeedback(
      name               = fullName.getOrElse(""),
      subject            = "App Feedback",
      email              = email,
      message            = messageWithExtras,
      userAgent          = userAgent,
      referrer           = journeyId.getOrElse(""),
      javascriptEnabled  = "",
      authId             = fieldTransformer.userIdFrom(hc),
      areaOfTax          = "",
      sessionId          = fieldTransformer.sessionIdFrom(hc),
      rating             = "",
      userTaxIdentifiers = fieldTransformer.userTaxIdentifiersFromEnrolments(Some(enrolments))
    )
  }

}

object FeedbackSubmission {
  implicit val reads: Reads[FeedbackSubmission] = Json.reads[FeedbackSubmission]
}
