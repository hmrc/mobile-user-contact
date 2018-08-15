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

package uk.gov.hmrc.mobileusercontact.support

import uk.gov.hmrc.auth.core.retrieve.ItmpName
import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproFeedback
import uk.gov.hmrc.mobileusercontact.domain.FeedbackSubmission

trait FeedbackTestData {

  protected val appFeedback = FeedbackSubmission(
    email = "email@example.com",
    message = "It's OK",
    userAgent = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    signUpForResearch = true,
    town = Some("Test town"),
    journeyId = Some("<JourneyID>")
  )

  protected val itmpName = ItmpName(
    givenName = Some("Given"),
    middleName = Some("Middle"),
    familyName = Some("Family")
  )

  /** The HmrcDeskproFeedback that should be sent when [appFeedback] is received and the the ItmpName retrieved from auth is [itmpName] */
  protected val expectedDeskproFeedback = HmrcDeskproFeedback(
    name = "Given Middle Family",
    email = "email@example.com",
    message =
      """It's OK
        |
        |Contact preference: yes
        |
        |Town: Test town""".stripMargin,
    userAgent = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    referrer = "<JourneyID>",
    subject = "App Feedback",
    javascriptEnabled = "",
    authId = "",
    areaOfTax = "",
    sessionId = "",
    rating = "")

}
