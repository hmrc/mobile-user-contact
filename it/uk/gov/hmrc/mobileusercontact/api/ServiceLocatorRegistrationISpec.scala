/*
 * Copyright 2016 HM Revenue & Customs
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

import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.PlayRunners
import uk.gov.hmrc.mobileusercontact.stubs.ServiceLocatorStub
import uk.gov.hmrc.mobileusercontact.test.WireMockSupport

class ServiceLocatorRegistrationISpec
  extends WordSpec with Matchers with Eventually
    with WireMockSupport with PlayRunners {

  lazy val app: Application = appBuilder.build()

  override protected def appBuilder: GuiceApplicationBuilder = super.appBuilder.configure(
    "microservice.services.service-locator.enabled" -> true
  )

  "microservice" should {
    "register itself with the api platform automatically at start up" in {
      ServiceLocatorStub.registrationSucceeds()

      ServiceLocatorStub.registerShouldNotHaveBeenCalled("mobile-user-contact", "https://mobile-user-contact.protected.mdtp")

      running(app) {
        eventually(Timeout(Span(20, Seconds))) {
          ServiceLocatorStub.registerShouldHaveBeenCalled("mobile-user-contact", "https://mobile-user-contact.protected.mdtp")
        }
      }
    }
  }
}
