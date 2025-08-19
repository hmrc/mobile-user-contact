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

package uk.gov.hmrc.mobileusercontact

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.ActorMaterializer
import org.slf4j.Logger
import play.api.{LoggerLike, MarkerContext}
import play.api.http.Status.*
import play.api.mvc.Results
import play.api.test.Helpers.{await, contentAsString, status}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{EmptyRetrieval, ItmpName, Retrieval}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobileusercontact.controllers.AuthorisedImpl
import uk.gov.hmrc.mobileusercontact.test.{BaseSpec, LoggerMock}

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedSpec extends BaseSpec with LoggerMock with Retrievals with Results {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  "Authorised" should {
    "retrieve ItmpName when specified and pass it to the block" in {
      val authConnectorStub = mock[AuthConnector]

      val authorised = new AuthorisedImpl(logger, authConnectorStub)
      (authConnectorStub
        .authorise[Option[ItmpName]](_: Predicate, _: Retrieval[Option[ItmpName]])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ConfidenceLevel.L200, *, *, *)
        .returns(Future successful Some(ItmpName(Some("Testgiven"), None, Some("Testfamily"))))

      var capturedItmpName: Option[ItmpName] = None
      val action = authorised.authorise(FakeRequest(), Retrievals.itmpName) { itmpName =>
        capturedItmpName = itmpName
        Future successful Ok
      }

      await(action)    shouldBe Ok
      capturedItmpName shouldBe Some(ItmpName(Some("Testgiven"), None, Some("Testfamily")))
    }

    "retrieve something else when specified and pass it to the block" in {
      val authConnectorMock = mock[AuthConnector]

      val authorised = new AuthorisedImpl(logger, authConnectorMock)
      (authConnectorMock
        .authorise[Option[String]](_: Predicate, _: Retrieval[Option[String]])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ConfidenceLevel.L200, externalId, *, *)
        .returns(Future successful Some("<test-external-id>"))

      var capturedItmpName: Option[Option[String]] = None
      val action = authorised.authorise(FakeRequest(), externalId) { maybeExternalId =>
        capturedItmpName = Some(maybeExternalId)
        Future successful Ok
      }

      await(action)    shouldBe Ok
      capturedItmpName shouldBe Some(Some("<test-external-id>"))
    }

    "return 401 when AuthConnector throws NoActiveSession" in {

      val authConnectorMock = mock[AuthConnector]
      val authorised = new AuthorisedImpl(logger, authConnectorMock)

      (slf4jLoggerMock.info(_: String)).expects("Authorisation failure - NoActiveSession [not logged in]").returning(())
      (authConnectorMock
        .authorise(_: Predicate, _: Retrieval[Any])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ConfidenceLevel.L200, *, *, *)
        .returns(Future.failed(new NoActiveSession("not logged in") {}))

      val action = authorised.authorise(FakeRequest(), EmptyRetrieval) { _ =>
        Future successful Ok
      }
      status(action) shouldBe UNAUTHORIZED

    }

    "return 403 when AuthConnector throws any other AuthorisationException" in {

      val authConnectorStub = mock[AuthConnector]

      val authorised = new AuthorisedImpl(logger, authConnectorStub)

      (authConnectorStub
        .authorise(_: Predicate, _: Retrieval[Any])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ConfidenceLevel.L200, *, *, *)
        .returns(Future failed new AuthorisationException("test not authorised reason") {})
      (slf4jLoggerMock.info(_: String)).expects("Authorisation failure [test not authorised reason]").returning(())
      val action = authorised.authorise(FakeRequest(), EmptyRetrieval) { _ =>
        Future successful Ok
      }

      status(action) shouldBe FORBIDDEN
      contentAsString(action) shouldBe "Authorisation failure [test not authorised reason]"

    }

    "return 403 Forbidden and log a warning when AuthConnector throws InsufficientConfidenceLevel" in {

      val authConnectorStub = mock[AuthConnector]

      val authorised = new AuthorisedImpl(logger, authConnectorStub)

      (authConnectorStub
        .authorise(_: Predicate, _: Retrieval[Any])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ConfidenceLevel.L200, *, *, *)
        .returns(Future failed new InsufficientConfidenceLevel("Insufficient ConfidenceLevel") {})
      (slf4jLoggerMock
        .warn(_: String))
        .expects("Forbidding access due to insufficient confidence level. User will see an error screen. To fix this see NGC-3381.")
        .returning(())
      val action = authorised.authorise(FakeRequest(), EmptyRetrieval) { _ =>
        Future successful Ok
      }

      status(action)          shouldBe FORBIDDEN
      contentAsString(action) shouldBe "Authorisation failure [Insufficient ConfidenceLevel]"
    }
  }

}
