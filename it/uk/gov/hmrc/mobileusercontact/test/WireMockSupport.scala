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

package uk.gov.hmrc.mobileusercontact.test

import java.net.URL

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.play.it.Port

case class WireMockBaseUrl(value: URL)

trait WireMockSupport extends BeforeAndAfterAll with BeforeAndAfterEach with AppBuilder {
  me: Suite =>

  val wireMockPort: Int = Port.randomAvailable
  val wireMockHost = "localhost"
  val wireMockBaseUrlAsString = s"http://$wireMockHost:$wireMockPort"
  val wireMockBaseUrl = new URL(wireMockBaseUrlAsString)
  protected implicit val implicitWireMockBaseUrl: WireMockBaseUrl = WireMockBaseUrl(wireMockBaseUrl)

  protected def basicWireMockConfig(): WireMockConfiguration = wireMockConfig()

  private val wireMockServer = new WireMockServer(basicWireMockConfig().port(wireMockPort))

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    WireMock.configureFor(wireMockHost, wireMockPort)
    wireMockServer.start()
  }

  override protected def afterAll(): Unit = {
    wireMockServer.stop()
    super.afterAll()
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    WireMock.reset()
  }

  override protected def appBuilder: GuiceApplicationBuilder = super.appBuilder.configure(
    "auditing.enabled" -> false,
    "microservice.services.auth.host" -> wireMockHost,
    "microservice.services.auth.port" -> wireMockPort,
    "microservice.services.help-to-save.host" -> wireMockHost,
    "microservice.services.help-to-save.port" -> wireMockPort,
    "microservice.services.hmrc-deskpro.host" -> wireMockHost,
    "microservice.services.hmrc-deskpro.port" -> wireMockPort,
    "microservice.services.service-locator.host" -> wireMockHost,
    "microservice.services.service-locator.port" -> wireMockPort
  )
}
