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

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsString, Json}

class OriginEnumSpec extends WordSpec with Matchers{

  "be writable to JSON for mobile-paye" in {
    val result = Json.toJson(OriginEnum.mobilePaye)
    result shouldBe JsString("mobile-paye")
  }

  "be writable to JSON for mobile-self-assessment" in {
    val result = Json.toJson(OriginEnum.mobileSelfAssessment)
    result shouldBe JsString("mobile-self-assessment")
  }

  "be writable to JSON for mobile-self-assessment-payment-status" in {
    val result = Json.toJson(OriginEnum.mobileSelfAssessmentPaymentStatus)
    result shouldBe JsString("mobile-self-assessment-payment-status")
  }

  "be writable to JSON for mobile-tax-credits" in {
    val result = Json.toJson(OriginEnum.mobileTaxCredits)
    result shouldBe JsString("mobile-tax-credits")
  }

  "be writable to JSON for mobile-help-to-save" in {
    val result = Json.toJson(OriginEnum.mobileHelpToSave)
    result shouldBe JsString("mobile-help-to-save")
  }

  "be writable to JSON for mobile-messages" in {
    val result = Json.toJson(OriginEnum.mobileMessages)
    result shouldBe JsString("mobile-messages")
  }

  "be writable to JSON for mobile-submission-tracker" in {
    val result = Json.toJson(OriginEnum.mobileSubmissionTracker)
    result shouldBe JsString("mobile-submission-tracker")
  }

  "be writable to JSON for mobile-tax-calculator" in {
    val result = Json.toJson(OriginEnum.mobileTaxCalculator)
    result shouldBe JsString("mobile-tax-calculator")
  }

  "be writable to JSON for mobile-customer-profile" in {
    val result = Json.toJson(OriginEnum.mobileCustomerProfile)
    result shouldBe JsString("mobile-customer-profile")
  }

  "be readable from JSON for mobile-paye" in {
    val result = Json.fromJson(JsString("mobile-paye"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobilePaye
  }

  "be readable from JSON for mobile-self-assessment" in {
    val result = Json.fromJson(JsString("mobile-self-assessment"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileSelfAssessment
  }

  "be readable from JSON for mobile-self-assessment-payment-status" in {
    val result = Json.fromJson(JsString("mobile-self-assessment-payment-status"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileSelfAssessmentPaymentStatus
  }

  "be readable from JSON for mobile-tax-credits" in {
    val result = Json.fromJson(JsString("mobile-tax-credits"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileTaxCredits
  }

  "be readable from JSON for mobile-help-to-save" in {
    val result = Json.fromJson(JsString("mobile-help-to-save"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileHelpToSave
  }

  "be readable from JSON for mobile-messages" in {
    val result = Json.fromJson(JsString("mobile-messages"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileMessages
  }

  "be readable from JSON for mobile-submission-tracker" in {
    val result = Json.fromJson(JsString("mobile-submission-tracker"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileSubmissionTracker
  }

  "be readable from JSON for mobile-tax-calculator" in {
    val result = Json.fromJson(JsString("mobile-tax-calculator"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileTaxCalculator
  }

  "be readable from JSON for mobile-customer-profile" in {
    val result = Json.fromJson(JsString("mobile-customer-profile"))(OriginEnum.format)
    result.get shouldBe OriginEnum.mobileCustomerProfile
  }

  "return JsError when the enum is not readable" in {
    val result = Json.fromJson(JsString("unknown"))(OriginEnum.format)
    result.isError shouldBe true
  }

}
