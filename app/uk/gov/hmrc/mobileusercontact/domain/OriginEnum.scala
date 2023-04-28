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

package uk.gov.hmrc.mobileusercontact.domain

import play.api.libs.json._

object OriginEnum extends Enumeration {

   val mobilePaye: OriginEnum.Value = Value("mobile-paye")
   val mobileSelfAssessment: OriginEnum.Value = Value("mobile-self-assessment")
   val mobileSelfAssessmentPaymentStatus: OriginEnum.Value = Value("mobile-self-assessment-payment-status")
   val mobileTaxCredits: OriginEnum.Value = Value("mobile-tax-credits")
   val mobileHelpToSave: OriginEnum.Value = Value("mobile-help-to-save")
   val mobileMessages: OriginEnum.Value = Value("mobile-messages")
   val mobileSubmissionTracker: OriginEnum.Value = Value("mobile-submission-tracker")
   val mobileTaxCalculator: OriginEnum.Value = Value("mobile-tax-calculator")
   val mobileCustomerProfile: OriginEnum.Value = Value("mobile-customer-profile")

  implicit val format: Format[OriginEnum.Value] = new Format[OriginEnum.Value] {

    override def writes(o: OriginEnum.Value): JsValue = {
      JsString(o.toString)
    }

    override def reads(json: JsValue): JsResult[OriginEnum.Value] = {
      json.as[String] match {
        case "mobile-paye" => JsSuccess(mobilePaye)
        case "mobile-self-assessment" => JsSuccess(mobileSelfAssessment)
        case "mobile-self-assessment-payment-status" => JsSuccess(mobileSelfAssessmentPaymentStatus)
        case "mobile-tax-credits" => JsSuccess(mobileTaxCredits)
        case "mobile-help-to-save" => JsSuccess(mobileHelpToSave)
        case "mobile-messages" => JsSuccess(mobileMessages)
        case "mobile-submission-tracker" => JsSuccess(mobileSubmissionTracker)
        case "mobile-tax-calculator" => JsSuccess(mobileTaxCalculator)
        case "mobile-customer-profile" => JsSuccess(mobileCustomerProfile)
        case e => JsError(s"$e not recognised")
      }
    }


  }

}
