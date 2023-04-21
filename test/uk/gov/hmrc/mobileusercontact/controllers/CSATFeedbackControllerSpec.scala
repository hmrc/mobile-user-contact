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

package uk.gov.hmrc.mobileusercontact.controllers

import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.PlaySpec
import play.api.test.{DefaultAwaitTimeout, FakeRequest, Helpers}
import uk.gov.hmrc.mobileusercontact.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobileusercontact.test.FeedbackTestData
import eu.timepit.refined.auto._
import org.scalamock.handlers.CallHandler2
import play.api.http.Status
import play.api.mvc.Headers
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.domain.CSATFeedback
import uk.gov.hmrc.mobileusercontact.services.CSATFeedbackService

import scala.concurrent.{ExecutionContext, Future}

class CSATFeedbackControllerSpec extends PlaySpec with DefaultAwaitTimeout with MockFactory with FeedbackTestData{

  private val journeyId: JourneyId = "27d3c283-a8e9-43f8-bb0b-65c42027494a"
  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val mockService: CSATFeedbackService = mock[CSATFeedbackService]

  def mockSendAudit(f: Future[Unit]): CallHandler2[CSATFeedback, HeaderCarrier, Future[Unit]] =
    (mockService.sendAudit(_: CSATFeedback)(_: HeaderCarrier)).expects(*, *).returning(f)

  private val fakeRequest = FakeRequest("POST", "/", Headers((CONTENT_TYPE, JSON)), csatFeedbackJson)
  private val controller = new CSATFeedbackController(Helpers.stubControllerComponents(), mockService)

  "POST /feedback" should {
    "return 204" in {
      mockSendAudit(Future.successful(()))
      val result = controller.feedback(journeyId)(fakeRequest)

      status(result) mustBe Status.NO_CONTENT
    }
  }
}
