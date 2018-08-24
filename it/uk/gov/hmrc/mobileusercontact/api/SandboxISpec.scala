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

package uk.gov.hmrc.mobileusercontact.api

import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mobileusercontact.stubs.{AuthStub, HmrcDeskproStub}
import uk.gov.hmrc.mobileusercontact.test.{IntegrationTestJson, MobileUserContactClient, OneServerPerSuiteWsClient, WireMockSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future._

class SandboxISpec
  extends WordSpec
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with WireMockSupport
    with OneServerPerSuiteWsClient
    with MobileUserContactClient {

  override implicit lazy val app: Application = appBuilder.build()
  val sandboxUserId1 = "208606423740"
  val sandboxUserId2 = "167927702220"

  import IntegrationTestJson.{feedbackSubmissionJson, supportRequestJson}

  "POST /feedback-submissions with prod account headers" should {

    "return the canned sandbox response" in {

      val responses = Seq(sandboxUserId1, sandboxUserId2) map { id =>
        AuthStub.userIsLoggedIn() // Sandbox doesn't use any retrievals
        postToFeedbackSandboxResource(feedbackSubmissionJson, id).map(_.status)
      }

      await(sequence(responses)).distinct shouldBe Seq(204)
    }

    "return 401 if no user is logged in" in {
      AuthStub.userIsNotLoggedIn()

      val response = await(postToFeedbackSandboxResource(feedbackSubmissionJson, sandboxUserId2))

      response.status shouldBe 401

      HmrcDeskproStub.createFeedbackShouldNotHaveBeenCalled()
    }

    "return 403 Forbidden when the user is logged in with an insufficient confidence level" in {
      AuthStub.userIsLoggedInWithInsufficientConfidenceLevel()

      val response = await(postToFeedbackSandboxResource(feedbackSubmissionJson, sandboxUserId2))

      response.status shouldBe 403

      HmrcDeskproStub.createFeedbackShouldNotHaveBeenCalled()
    }
  }

  "POST /support-requests with prod account headers" should {

    "return the canned sandbox response" in {
      val responses = Seq(sandboxUserId1, sandboxUserId2) map { id =>
        AuthStub.userIsLoggedIn() // Sandbox doesn't use any retrievals
        postToSupportSandboxResource(supportRequestJson, id).map(_.status)
      }

      await(sequence(responses)).distinct shouldBe Seq(204)
    }

    "return 401 if no user is logged in" in {
      AuthStub.userIsNotLoggedIn()

      val response = await(postToSupportSandboxResource(supportRequestJson, sandboxUserId1))

      response.status shouldBe 401

      HmrcDeskproStub.createFeedbackShouldNotHaveBeenCalled()
    }

    "return 403 Forbidden when the user is logged in with an insufficient confidence level" in {
      AuthStub.userIsLoggedInWithInsufficientConfidenceLevel()

      val response = await(postToSupportSandboxResource(supportRequestJson, sandboxUserId1))

      response.status shouldBe 403

      HmrcDeskproStub.createFeedbackShouldNotHaveBeenCalled()
    }
  }
}
