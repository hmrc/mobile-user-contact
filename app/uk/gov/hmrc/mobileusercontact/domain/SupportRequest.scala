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
import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproSupport

case class SupportRequest(
   name: String,
   email: String,
   message: String,
   userAgent: String,
   journeyId: Option[String],
   service: Option[String]
 ) {

  def toDeskpro(): HmrcDeskproSupport = HmrcDeskproSupport(
    name = name,
    email = email,
    subject = "App Support",
    message = message,
    referrer = journeyId.getOrElse(""),
    javascriptEnabled = "",
    userAgent = userAgent,
    authId = "",
    areaOfTax = "",
    sessionId = "",
    service = service
  )
}


object SupportRequest {
  implicit val reads: Reads[SupportRequest] = Json.reads[SupportRequest]
}