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

package uk.gov.hmrc.mobileusercontact.config

import java.net.URL

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class MobileUserContactConfig @Inject() (
  environment:   Environment,
  configuration: Configuration)
    extends HelpToSaveConnectorConfig
    with HmrcDeskproConnectorConfig {

  val servicesConfig = new ServicesConfig(configuration)

  override val helpToSaveBaseUrl: URL = configBaseUrl("help-to-save")

  override val hmrcDeskproBaseUrl: URL = configBaseUrl("deskpro-ticket-queue")

  private def configBaseUrl(serviceName: String): URL = new URL(servicesConfig.baseUrl(serviceName))
}

@ImplementedBy(classOf[MobileUserContactConfig])
trait HelpToSaveConnectorConfig {
  def helpToSaveBaseUrl: URL
}

@ImplementedBy(classOf[MobileUserContactConfig])
trait HmrcDeskproConnectorConfig {
  def hmrcDeskproBaseUrl: URL
}
