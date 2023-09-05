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
import com.github.tomakehurst.wiremock.client.WireMock.{configureFor, reset}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import play.api.inject.guice.GuiceApplicationBuilder

case class WireMockBaseUrl(value: URL)

trait WireMockSupport extends BeforeAndAfterAll with BeforeAndAfterEach with AppBuilder {
  me: Suite =>

  val wireMockPort: Int = wireMockServer.port()
  val wireMockHost            = "localhost"
  val wireMockBaseUrlAsString = s"http://$wireMockHost:$wireMockPort"
  val wireMockBaseUrl         = new URL(wireMockBaseUrlAsString)
  protected implicit val implicitWireMockBaseUrl: WireMockBaseUrl = WireMockBaseUrl(wireMockBaseUrl)

  protected def basicWireMockConfig(): WireMockConfiguration = wireMockConfig()

  protected implicit lazy val wireMockServer: WireMockServer = {
    val server = new WireMockServer(basicWireMockConfig().dynamicPort())
    server.start()
    server
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    configureFor(wireMockHost, wireMockPort)
    wireMockServer.start()
  }

  override def afterAll(): Unit = {
    wireMockServer.stop()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset()
  }

  override protected def appBuilder: GuiceApplicationBuilder = super.appBuilder.configure(
    "auditing.enabled"                        -> false,
    "microservice.services.auth.host"         -> wireMockHost,
    "microservice.services.auth.port"         -> wireMockPort,
    "microservice.services.help-to-save.host" -> wireMockHost,
    "microservice.services.help-to-save.port" -> wireMockPort,
    "microservice.services.hmrc-deskpro.host" -> wireMockHost,
    "microservice.services.hmrc-deskpro.port" -> wireMockPort,
    "api.access.white-list.applicationIds" -> Seq("00010002-0003-0004-0005-000600070008",
                                                  "00090002-0003-0004-0005-000600070008"),
    "api.access.type" -> "TEST_ACCESS_TYPE"
  )
}
