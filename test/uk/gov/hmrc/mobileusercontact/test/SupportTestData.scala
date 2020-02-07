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

package uk.gov.hmrc.mobileusercontact.test

import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproSupport
import uk.gov.hmrc.mobileusercontact.domain.SupportRequest

trait SupportTestData extends FieldTransformerTestData {

  protected val supportTicket = SupportRequest(
    email     = "email@example.com",
    message   = "Where is my payment?",
    userAgent = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    journeyId = Some("<JourneyID>"),
    name      = "Name Namely",
    service   = Some("HTS")
  )

  /**
    * The HmrcDeskproSupport that should be sent
    * when [[supportTicket]] is received, headers are [[hc]] and enrolments returned by auth are [[enrolments]]
    */
  protected val expectedDeskproSupport = HmrcDeskproSupport(
    name               = "Name Namely",
    email              = "email@example.com",
    message            = "Where is my payment?",
    userAgent          = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    referrer           = "<JourneyID>",
    subject            = "App Support Request",
    javascriptEnabled  = "",
    authId             = testAuthId,
    areaOfTax          = "",
    sessionId          = testSessionId,
    service            = Some("HTS"),
    userTaxIdentifiers = expectedUserTaxIdentifiers
  )
}
