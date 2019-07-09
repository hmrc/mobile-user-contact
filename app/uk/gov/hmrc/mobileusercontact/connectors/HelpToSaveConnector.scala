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

package uk.gov.hmrc.mobileusercontact.connectors

import java.net.URL

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.LoggerLike
import play.api.libs.json.JsValue
import uk.gov.hmrc.http._
import uk.gov.hmrc.mobileusercontact.config.HelpToSaveConnectorConfig

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@ImplementedBy(classOf[HelpToSaveConnectorImpl])
trait HelpToSaveConnector {
  def enrolmentStatus()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean]
}

@Singleton
class HelpToSaveConnectorImpl @Inject() (
  logger: LoggerLike,
  config: HelpToSaveConnectorConfig,
  http: CoreGet)
  extends HelpToSaveConnector {

  override def enrolmentStatus()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    http.GET[JsValue](enrolmentStatusUrl.toString) map { json: JsValue =>
     (json \ "enrolled").as[Boolean]
    }
  }

  private lazy val enrolmentStatusUrl: URL = new URL(config.helpToSaveBaseUrl, "/help-to-save/enrolment-status")
}
