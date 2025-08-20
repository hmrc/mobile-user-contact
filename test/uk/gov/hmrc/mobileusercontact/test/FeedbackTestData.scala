/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.auth.core.retrieve.ItmpName
import uk.gov.hmrc.mobileusercontact.connectors.HmrcDeskproFeedback
import uk.gov.hmrc.mobileusercontact.domain.{CSATFeedback, FeedbackSubmission, OriginEnum}

trait FeedbackTestData extends FieldTransformerTestData {

  protected val appFeedback = FeedbackSubmission(
    email             = "email@example.com",
    message           = "It's OK",
    userAgent         = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    journeyId         = Some("<JourneyID>")
  )

  protected val itmpName = Some(ItmpName(
    givenName  = Some("Given"),
    middleName = Some("Middle"),
    familyName = Some("Family"))
  )

  protected val enrolledInHelpToSave = false

  /**
    * The HmrcDeskproFeedback that should be sent
    * when [[appFeedback]] is received
    * and the the ItmpName retrieved from auth is [[itmpName]]
    * and the enrolment status received from help-to-save is [[enrolledInHelpToSave]]
    */
  protected val expectedDeskproFeedback = HmrcDeskproFeedback(
    name               = "Given Middle Family",
    email              = "email@example.com",
    message            = "It's OK",
    userAgent          = "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
    referrer           = "<JourneyID>",
    subject            = "App Feedback",
    javascriptEnabled  = "",
    authId             = testAuthId,
    areaOfTax          = "",
    sessionId          = testSessionId,
    rating             = "",
    userTaxIdentifiers = expectedUserTaxIdentifiers
  )

  val csatFeedbackModel: CSATFeedback = CSATFeedback(OriginEnum.mobilePaye,Some(true), Some(5), Some("It was great"), Some(4))
  val emptyCsatFeedbackModel: CSATFeedback = CSATFeedback(OriginEnum.mobilePaye,None, None, None, None)


  val csatFeedbackJson: JsObject = Json.obj(
    "origin"-> "mobile-paye",
    "ableToDo" -> true,
    "howEasyScore" -> 5,
    "whyGiveScore" -> "It was great",
    "howDoYouFeelScore" -> 4)

  val csatFeedbackInvalidOriginJson: JsObject = Json.obj(
    "origin" -> "mobile-push-notifications",
    "ableToDo" -> true,
    "howEasyScore" -> 5,
    "whyGiveScore" -> "It was great",
    "howDoYouFeelScore" -> 4)

  val emptyCsatFeedbackJson: JsObject = Json.obj(
    "origin" -> "mobile-paye")

  val testAuditModel: Map[String, String] = Map("origin" -> "mobile-paye", "ableToDo" -> "true", "howEasyScore" -> "5", "whyGiveScore" -> "It was great", "howDoYouFeelScore" -> "4")

  val testAuditModelNoAnswers: Map[String, String] = Map("origin" -> "mobile-paye", "ableToDo" -> "-", "howEasyScore" -> "-", "whyGiveScore" -> "-", "howDoYouFeelScore" -> "-")

}
