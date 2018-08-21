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

package uk.gov.hmrc.mobileusercontact.controllers

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import play.api.mvc._
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.mobileusercontact.test.SupportTestData

class SupportControllerSpec extends WordSpec with Matchers
  with DefaultAwaitTimeout
  with MockFactory
  with Retrievals
  with ScalaFutures
  with SupportTestData
  with Results {

  "submitSupport" should {

    "reject support requests for users that have an insufficient confidence level" in {

//      val service: Support = stub[Support]
//      val auth: AuthWith200 = stub[AuthWith200]
//
//      (auth.checkConfidenceLevel.async(_:BodyParser[SupportRequest])(_:Request[_] => Future[Result]))
//        .when()
//
//
//      val controller = new SupportController(service, auth, checkFunction)
//
//      status(controller.submitSupport(FakeRequest().withBody[SupportRequest](supportTicket))) shouldBe FORBIDDEN
    }
  }
}

/*
(authConnectorStub.authorise[ItmpName](_: Predicate, _: Retrieval[ItmpName])(_: HeaderCarrier, _: ExecutionContext))
      .when(ConfidenceLevel.L200, itmpName, *, *)
      .returns(itmpNameF)
 */