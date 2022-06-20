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

package uk.gov.hmrc.mobileusercontact.domain

import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproSupport
import uk.gov.hmrc.mobileusercontact.contactfrontend.FieldTransformer

case class SupportRequest(
  name:      String,
  email:     String,
  message:   String,
  userAgent: String,
  journeyId: Option[String],
  service:   Option[String]) {

  def toDeskpro(
    fieldTransformer: FieldTransformer,
    enrolments:       Enrolments
  )(implicit hc:      HeaderCarrier
  ): HmrcDeskproSupport = HmrcDeskproSupport(
    name               = name,
    email              = email,
    subject            = "App Support Request",
    message            = message,
    referrer           = journeyId.getOrElse(""),
    javascriptEnabled  = "",
    userAgent          = userAgent,
    authId             = fieldTransformer.NA,
    areaOfTax          = "",
    sessionId          = fieldTransformer.sessionIdFrom(hc),
    service            = service,
    userTaxIdentifiers = fieldTransformer.userTaxIdentifiersFromEnrolments(Some(enrolments))
  )
}

object SupportRequest {
  implicit val reads: Reads[SupportRequest] = Json.reads[SupportRequest]
}
