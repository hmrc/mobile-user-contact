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

package uk.gov.hmrc.mobileusercontact.test

import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.logging.SessionId
import uk.gov.hmrc.http.{HeaderCarrier, UserId}
import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproSupport
import uk.gov.hmrc.mobileusercontact.contactfrontend.UserTaxIdentifiers
import uk.gov.hmrc.mobileusercontact.domain.SupportRequest

trait SupportTestData {

  protected val supportTicket = SupportRequest(
    email = "email@example.com",
    message = "Where is my payment?",
    userAgent = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    journeyId = Some("<JourneyID>"),
    name = "Name Namely",
    service = Some("HTS")
  )

  protected val hc = HeaderCarrier(
    userId = Some(UserId("test-authId")),
    sessionId = Some(SessionId("test-sessionId"))
  )
  protected val enrolments = Enrolments(
    Set(Enrolment("HMRC-NI", Seq(EnrolmentIdentifier("NINO", "AA000003D")), "Activated", None))
  )

  protected val expectedUserTaxIdentifiers = UserTaxIdentifiers(nino = Some("AA000003D"), ctUtr = None, utr = None, vrn = None, empRef = None)

  /**
    * The HmrcDeskproSupport that should be sent
    * when [[supportTicket]] is received, headers are [[hc]] and enrolments returned by auth are [[enrolments]]
    */
  protected val expectedDeskproSupport = HmrcDeskproSupport(
    name = "Name Namely",
    email = "email@example.com",
    message = "Where is my payment?",
    userAgent = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    referrer = "<JourneyID>",
    subject = "App Support Request",
    javascriptEnabled = "",
    authId = hc.userId.get.value,
    areaOfTax = "",
    sessionId = hc.sessionId.get.value,
    service = Some("HTS"),
    userTaxIdentifiers = expectedUserTaxIdentifiers
  )
}
