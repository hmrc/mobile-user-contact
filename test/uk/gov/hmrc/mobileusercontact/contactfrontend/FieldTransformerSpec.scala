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

package uk.gov.hmrc.mobileusercontact.contactfrontend

import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import uk.gov.hmrc.domain.EmpRef
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}

/**
  * Based on code in contact-frontend. If you are considering refactoring then consider the cost of keeping up to date with changes in contact-frontend.
  */
class FieldTransformerSpec extends PlaySpec {

  "Field Transformer" should {

    "transform sessionId in the header carrier to session id" in new FieldTransformerScope {
      transformer.sessionIdFrom(hc) mustBe sessionId
    }

    "transform no sessionId in the header carrier to n/a" in new FieldTransformerScope {
      transformer.sessionIdFrom(hc.copy(sessionId = None)) mustBe "n/a"
    }

    "transform non authorised user to UserTaxIdentifiers containing no identifiers" in new FieldTransformerScope {
      transformer.userTaxIdentifiersFromEnrolments(None) mustBe expectedUserTaxIdentifiers()
    }

    "transform paye authorised user to UserTaxIdentifiers containing one identifier i.e. the SH233544B" in new FieldTransformerScope {
      transformer.userTaxIdentifiersFromEnrolments(Some(payeUser)) mustBe expectedUserTaxIdentifiers(nino =
        Some("SH233544B")
      )
    }

    "transform business tax authorised user to UserTaxIdentifiers containing all the Business Tax Identifiers (and HMCE-VATDEC-ORG endorsement)" in new FieldTransformerScope {
      transformer.userTaxIdentifiersFromEnrolments(Some(bizTaxUserWithVatDec)) mustBe expectedUserTaxIdentifiers(
        utr    = Some("sa"),
        ctUtr  = Some("ct"),
        vrn    = Some("vrn1"),
        empRef = Some(EmpRef("officeNum", "officeRef").value)
      )
    }

    "transform business tax authorised user to UserTaxIdentifiers containing all the Business Tax Identifiers (and HMCE-VATVAR-ORG endorsement)" in new FieldTransformerScope {
      transformer.userTaxIdentifiersFromEnrolments(Some(bizTaxUserWithVatVar)) mustBe expectedUserTaxIdentifiers(
        utr    = Some("sa"),
        ctUtr  = Some("ct"),
        vrn    = Some("vrn2"),
        empRef = Some(EmpRef("officeNum", "officeRef").value)
      )
    }
  }

}

class FieldTransformerScope {
  lazy val transformer: FieldTransformer = new FieldTransformer {}

  lazy val payeUser =
    Enrolments(
      Set(
        Enrolment("HMRC-NI").withIdentifier("NINO", "SH233544B")
      )
    )

  lazy val bizTaxUserWithVatDec =
    Enrolments(
      Set(
        Enrolment("IR-SA").withIdentifier("UTR", "sa"),
        Enrolment("IR-CT").withIdentifier("UTR", "ct"),
        Enrolment("HMCE-VATDEC-ORG").withIdentifier("VATRegNo", "vrn1"),
        Enrolment("IR-PAYE")
          .withIdentifier("TaxOfficeNumber", "officeNum")
          .withIdentifier("TaxOfficeReference", "officeRef")
      )
    )

  lazy val bizTaxUserWithVatVar =
    Enrolments(
      Set(
        Enrolment("IR-SA").withIdentifier("UTR", "sa"),
        Enrolment("IR-CT").withIdentifier("UTR", "ct"),
        Enrolment("HMCE-VATVAR-ORG").withIdentifier("VATRegNo", "vrn2"),
        Enrolment("IR-PAYE")
          .withIdentifier("TaxOfficeNumber", "officeNum")
          .withIdentifier("TaxOfficeReference", "officeRef")
      )
    )

  val sessionId: String = "sessionIdValue"
  val hc = HeaderCarrier(sessionId = Some(SessionId(sessionId)))
  val userAgent: String = "Mozilla"
  val name:      String = "name"
  val email:     String = "email"
  val subject:   String = "subject"
  val message:   String = "message"
  val referrer:  String = "referer"

  def expectedUserTaxIdentifiers(
    nino:   Option[String] = None,
    ctUtr:  Option[String] = None,
    utr:    Option[String] = None,
    vrn:    Option[String] = None,
    empRef: Option[String] = None
  ) = UserTaxIdentifiers(nino, ctUtr, utr, vrn, empRef)
}
