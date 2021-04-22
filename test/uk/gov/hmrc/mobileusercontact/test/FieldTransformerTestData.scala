/*
 * Copyright 2021 HM Revenue & Customs
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

import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.mobileusercontact.contactfrontend.{FieldTransformer, UserTaxIdentifiers}

trait FieldTransformerTestData {

  protected val testAuthId    = "n/a"
  protected val testSessionId = "test-sessionId"

  protected val hc = HeaderCarrier(
    sessionId = Some(SessionId(testSessionId))
  )

  protected val enrolments = Enrolments(
    Set(Enrolment("HMRC-NI", Seq(EnrolmentIdentifier("NINO", "AA000003D")), "Activated", None))
  )

  protected val expectedUserTaxIdentifiers =
    UserTaxIdentifiers(nino = Some("AA000003D"), ctUtr = None, utr = None, vrn = None, empRef = None)

}

trait MockFieldTransformerForTestData extends FieldTransformerTestData with MockFactory {

  def mockFieldTransformerForTestData: FieldTransformer = {
    val fieldTransformer = mock[FieldTransformer]

    fieldTransformer.sessionIdFrom _ expects hc returning testSessionId
    fieldTransformer.userTaxIdentifiersFromEnrolments _ expects Some(enrolments) returning expectedUserTaxIdentifiers

    fieldTransformer
  }
}
