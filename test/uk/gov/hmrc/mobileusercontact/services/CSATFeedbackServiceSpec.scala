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

package uk.gov.hmrc.mobileusercontact.services

import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, OneInstancePerTest, WordSpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.test.FeedbackTestData
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext

class CSATFeedbackServiceSpec extends WordSpec with MockFactory with FeedbackTestData with OneInstancePerTest with Matchers {

  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  lazy val auditConnector: AuditConnector = mock[AuditConnector]
  lazy val csatFeedbackService: CSATFeedbackService = new CSATFeedbackService(auditConnector)

  "Calling the .buildAuditModel" should {

    "return an auditMap with an origin and feedback model that contains answers" in {

      val result = csatFeedbackService.buildAuditMap(csatFeedbackModel)

      val expected = testAuditModel

      result shouldBe expected

    }

    "return an audit map with an origin and feedback model that contains no answers" in {

      val result = csatFeedbackService.buildAuditMap(emptyCsatFeedbackModel)

      val expected = testAuditModelNoAnswers

      result shouldEqual expected

    }

  }
}
