/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.mobileusercontact.config

import java.net.URL

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.config.ServicesConfig

import scala.collection.JavaConverters._

@Singleton
class MobileUserContactConfig @Inject()(
  environment: Environment,
  configuration: Configuration
) extends ServicesConfig
 with DocumentationControllerConfig
  with HelpToSaveConnectorConfig
  with HmrcDeskproConnectorConfig
  with ServiceLocatorRegistrationTaskConfig {

  override protected lazy val mode: Mode = environment.mode
  override protected def runModeConfiguration: Configuration = configuration

  // These are eager vals so that missing or invalid configuration will be detected on startup
  private val accessConfig = configuration.underlying.getConfig("api.access")
  override val apiAccessType: String = accessConfig.getString("type")
  override val apiWhiteListApplicationIds: Seq[String] = accessConfig.getStringList("white-list.applicationIds").asScala

  override val helpToSaveBaseUrl: URL = configBaseUrl("help-to-save")

  override val hmrcDeskproBaseUrl: URL = configBaseUrl("hmrc-deskpro")

  override val serviceLocatorEnabled: Boolean = configBoolean("microservice.services.service-locator.enabled")

  private def configBoolean(path: String): Boolean = configuration.underlying.getBoolean(path)

  private def configBaseUrl(serviceName: String): URL = new URL(baseUrl(serviceName))
}

@ImplementedBy(classOf[MobileUserContactConfig])
trait DocumentationControllerConfig {
  def apiAccessType: String
  def apiWhiteListApplicationIds: Seq[String]
}

@ImplementedBy(classOf[MobileUserContactConfig])
trait HelpToSaveConnectorConfig {
  def helpToSaveBaseUrl: URL
}

@ImplementedBy(classOf[MobileUserContactConfig])
trait HmrcDeskproConnectorConfig {
  def hmrcDeskproBaseUrl: URL
}

@ImplementedBy(classOf[MobileUserContactConfig])
trait ServiceLocatorRegistrationTaskConfig {
  def serviceLocatorEnabled: Boolean
}
