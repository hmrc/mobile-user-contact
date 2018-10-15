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

package uk.gov.hmrc.mobileusercontact.contactfrontend

import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

/**
  * Based on code in contact-frontend. If you are considering refactoring then consider the cost of keeping up to date with changes in contact-frontend.
  */
trait FieldTransformer {
  val NA = "n/a"

  def sessionIdFrom(hc: HeaderCarrier): String = hc.sessionId.map(_.value).getOrElse("n/a")

  def userIdFrom(hc: HeaderCarrier): String =
    hc.userId.map(_.value).getOrElse(NA)

  private def extractIdentifier(enrolments: Enrolments, enrolment : String, identifierKey : String): Option[String] = {
    enrolments.getEnrolment(enrolment).flatMap(_.identifiers.find(_.key == identifierKey)).map(_.value)
  }

  def userTaxIdentifiersFromEnrolments(enrolmentsOption: Option[Enrolments]): UserTaxIdentifiers = {
    enrolmentsOption.map {
      enrolments =>
        val nino = extractIdentifier(enrolments, "HMRC-NI", "NINO")
        val saUtr = extractIdentifier(enrolments, "IR-SA", "UTR")
        val ctUtr = extractIdentifier(enrolments, "IR-CT", "UTR")
        val vrn = extractIdentifier(enrolments, "HMCE-VATDEC-ORG", "VATRegNo")
            .orElse(extractIdentifier(enrolments, "HMCE-VATVAR-ORG", "VATRegNo"))

        val empRef = for (
          taxOfficeNumber <- extractIdentifier(enrolments, "IR-PAYE", "TaxOfficeNumber");
          taxOfficeRef <- extractIdentifier(enrolments, "IR-PAYE", "TaxOfficeReference")
        ) yield s"$taxOfficeNumber/$taxOfficeRef"

        UserTaxIdentifiers(nino, ctUtr, saUtr, vrn, empRef)
    }.getOrElse(UserTaxIdentifiers(None, None, None, None, None))
  }
}

object FieldTransformer extends FieldTransformer
