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

package uk.gov.hmrc.mobileusercontact

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.ActorMaterializer
import play.api.http.Status._
import play.api.mvc.Results
import play.api.test.Helpers.{await, contentAsString, status}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{EmptyRetrieval, ItmpName, Retrieval}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.controllers.AuthorisedImpl
import uk.gov.hmrc.mobileusercontact.test.{BaseSpec, LoggerStub}

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedSpec extends BaseSpec with LoggerStub with Retrievals with Results {

  implicit val system:       ActorSystem       = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  "Authorised" should {
    "retrieve ItmpName when specified and pass it to the block" in {
      val authConnectorStub = stub[AuthConnector]
      (authConnectorStub
        .authorise[Option[ItmpName]](_: Predicate, _: Retrieval[Option[ItmpName]])(_: HeaderCarrier,
                                                                                   _: ExecutionContext))
        .when(ConfidenceLevel.L200, *, *, *)
        .returns(Future successful Some(ItmpName(Some("Testgiven"), None, Some("Testfamily"))))

      val authorised = new AuthorisedImpl(logger, authConnectorStub)

      var capturedItmpName: Option[ItmpName] = None
      val action = authorised.authorise(FakeRequest(), Retrievals.itmpName) { itmpName =>
        capturedItmpName = itmpName
        Future successful Ok
      }

      await(action)    shouldBe Ok
      capturedItmpName shouldBe Some(ItmpName(Some("Testgiven"), None, Some("Testfamily")))
    }

    "retrieve something else when specified and pass it to the block" in {
      val authConnectorStub = stub[AuthConnector]
      (authConnectorStub
        .authorise[Option[String]](_: Predicate, _: Retrieval[Option[String]])(_: HeaderCarrier, _: ExecutionContext))
        .when(ConfidenceLevel.L200, externalId, *, *)
        .returns(Future successful Some("<test-external-id>"))

      val authorised = new AuthorisedImpl(logger, authConnectorStub)

      var capturedItmpName: Option[Option[String]] = None
      val action = authorised.authorise(FakeRequest(), externalId) { maybeExternalId =>
        capturedItmpName = Some(maybeExternalId)
        Future successful Ok
      }

      await(action)    shouldBe Ok
      capturedItmpName shouldBe Some(Some("<test-external-id>"))
    }

    "return 401 when AuthConnector throws NoActiveSession" in {
      val authConnectorStub = authConnectorStubThatWillFail(new NoActiveSession("not logged in") {})

      val authorised = new AuthorisedImpl(logger, authConnectorStub)

      val action = authorised.authorise(FakeRequest(), EmptyRetrieval) { _ =>
        Future successful Ok
      }

      status(action) shouldBe UNAUTHORIZED
      (slf4jLoggerStub.info(_: String)) verify "Authorisation failure - NoActiveSession [not logged in]"
    }

    "return 403 when AuthConnector throws any other AuthorisationException" in {
      val authConnectorStub = authConnectorStubThatWillFail(new AuthorisationException("test not authorised reason") {})

      val authorised = new AuthorisedImpl(logger, authConnectorStub)

      val action = authorised.authorise(FakeRequest(), EmptyRetrieval) { _ =>
        Future successful Ok
      }

      status(action) shouldBe FORBIDDEN
      (slf4jLoggerStub.info(_: String)) verify "Authorisation failure [test not authorised reason]"
    }

    "return 403 Forbidden and log a warning when AuthConnector throws InsufficientConfidenceLevel" in {
      val authConnectorStub =
        authConnectorStubThatWillFail(new InsufficientConfidenceLevel("Insufficient ConfidenceLevel") {})

      val authorised = new AuthorisedImpl(logger, authConnectorStub)

      val action = authorised.authorise(FakeRequest(), EmptyRetrieval) { _ =>
        Future successful Ok
      }

      status(action)          shouldBe FORBIDDEN
      contentAsString(action) shouldBe "Authorisation failure [Insufficient ConfidenceLevel]"
      (slf4jLoggerStub
        .warn(_: String)) verify "Forbidding access due to insufficient confidence level. User will see an error screen. To fix this see NGC-3381."
    }
  }

  private def authConnectorStubThatWillFail(e: Throwable): AuthConnector = {
    val authConnectorStub = stub[AuthConnector]
    (authConnectorStub
      .authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
      .when(ConfidenceLevel.L200, *, *, *)
      .returns(Future failed e)
    authConnectorStub
  }
}
