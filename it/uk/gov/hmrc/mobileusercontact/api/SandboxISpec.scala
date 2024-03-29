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

import uk.gov.hmrc.mobileusercontact.stubs.AuthStub
import uk.gov.hmrc.mobileusercontact.test.BaseISpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future._

class SandboxISpec extends BaseISpec {

  val sandboxUserId1 = "208606423740"
  val sandboxUserId2 = "167927702220"

  "POST /feedback-submissions with prod test account headers" should {

    "return the canned sandbox response" in {

      val feedbackSubmissionJson =
        """
          |{
          |  "email": "testy@example.com",
          |  "message": "I think the app is great",
          |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
          |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)"
          |}
      """.stripMargin

      val responses = Seq(sandboxUserId1, sandboxUserId2) map { id =>
          AuthStub.userIsLoggedIn() // Sandbox doesn't use any retrievals

          wsUrl(s"/feedback-submissions?journeyId=$journeyId")
            .addHttpHeaders("Content-Type" -> "application/json", "X-MOBILE-USER-ID" -> id)
            .post(feedbackSubmissionJson)
            .map(_.status)
        }

      await(sequence(responses)).distinct shouldBe Seq(202)
    }

    "return 400 if journeyId not supplied" in {

      val feedbackSubmissionJson =
        """
          |{
          |  "email": "testy@example.com",
          |  "message": "I think the app is great",
          |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
          |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)"
          |}
        """.stripMargin

      val responses = Seq(sandboxUserId1, sandboxUserId2) map { id =>
          AuthStub.userIsLoggedIn() // Sandbox doesn't use any retrievals

          wsUrl(s"/feedback-submissions")
            .addHttpHeaders("Content-Type" -> "application/json", "X-MOBILE-USER-ID" -> id)
            .post(feedbackSubmissionJson)
            .map(_.status)
        }

      await(sequence(responses)).distinct shouldBe Seq(400)
    }
  }

  "POST /support-requests with prod test account headers" should {

    "return the canned sandbox response" in {

      val supportRequestJson =
        """
          |{
          |  "name": "John Smith",
          |  "email": "testy@example.com",
          |  "message": "I can't find my latest payment",
          |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
          |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
          |  "service": "HTS"
          |}
        """.stripMargin

      val responses = Seq(sandboxUserId1, sandboxUserId2) map { id =>
          AuthStub.userIsLoggedIn() // Sandbox doesn't use any retrievals

          wsUrl(s"/support-requests?journeyId=$journeyId")
            .addHttpHeaders("Content-Type" -> "application/json", "X-MOBILE-USER-ID" -> id)
            .post(supportRequestJson)
            .map(_.status)
        }

      await(sequence(responses)).distinct shouldBe Seq(202)
    }

    "return 400 if journeyId not supplied" in {

      val supportRequestJson =
        """
          |{
          |  "name": "John Smith",
          |  "email": "testy@example.com",
          |  "message": "I can't find my latest payment",
          |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
          |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
          |  "service": "HTS"
          |}
        """.stripMargin

      val responses = Seq(sandboxUserId1, sandboxUserId2) map { id =>
          AuthStub.userIsLoggedIn() // Sandbox doesn't use any retrievals

          wsUrl(s"/support-requests")
            .addHttpHeaders("Content-Type" -> "application/json", "X-MOBILE-USER-ID" -> id)
            .post(supportRequestJson)
            .map(_.status)
        }

      await(sequence(responses)).distinct shouldBe Seq(400)
    }

    "return 400 if invalid journeyId supplied" in {

      val supportRequestJson =
        """
          |{
          |  "name": "John Smith",
          |  "email": "testy@example.com",
          |  "message": "I can't find my latest payment",
          |  "journeyId": "eaded345-4ccd-4c27-9285-cde938bd896d",
          |  "userAgent": "HMRCNextGenConsumer/uk.gov.hmrc.TaxCalc 5.5.1 (iOS 10.3.3)",
          |  "service": "HTS"
          |}
        """.stripMargin

      val responses = Seq(sandboxUserId1, sandboxUserId2) map { id =>
          AuthStub.userIsLoggedIn() // Sandbox doesn't use any retrievals

          wsUrl(s"/support-requests?journeyId=InvalidJourneyId")
            .addHttpHeaders("Content-Type" -> "application/json", "X-MOBILE-USER-ID" -> id)
            .post(supportRequestJson)
            .map(_.status)
        }

      await(sequence(responses)).distinct shouldBe Seq(400)
    }
  }
}
