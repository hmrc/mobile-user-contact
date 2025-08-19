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

import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.mobileusercontact.domain.types.JourneyId
import uk.gov.hmrc.mobileusercontact.services.CSATFeedbackService
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import eu.timepit.refined.auto.*

import scala.concurrent.ExecutionContext

trait BaseSpec extends AnyWordSpecLike with MockFactory with OneInstancePerTest with Matchers with DefaultAwaitTimeout {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val journeyId: JourneyId = JourneyId.from("27d3c283-a8e9-43f8-bb0b-65c42027494a").toOption.get
  val mockService: CSATFeedbackService = mock[CSATFeedbackService]
  val auditConnector: AuditConnector = mock[AuditConnector]
  val csatFeedbackService: CSATFeedbackService = new CSATFeedbackService(auditConnector)

}
