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

package uk.gov.hmrc.mobileusercontact.connectors

import java.net.URL

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json, OWrites}
import uk.gov.hmrc.http.{CorePost, HeaderCarrier}
import uk.gov.hmrc.mobileusercontact.config.HmrcDeskproConnectorConfig

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[HmrcDeskproConnectorImpl])
trait HmrcDeskproConnector {
  def createFeedback(feedback: HmrcDeskproFeedback)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit]
}

@Singleton
class HmrcDeskproConnectorImpl @Inject() (
  http: CorePost,
  config: HmrcDeskproConnectorConfig
) extends HmrcDeskproConnector {

  //TODO convert exceptions to Either?
  def createFeedback(feedback: HmrcDeskproFeedback)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http.POST[HmrcDeskproFeedback, JsValue](createFeedbackUrl.toString, feedback).map(_ => ())

  private val createFeedbackUrl =
    new URL(config.hmrcDeskproBaseUrl, "/deskpro/feedback")

}

case class HmrcDeskproFeedback(
  //TODO
//  name: String,
  email: String,
  subject: String,
  message: String,

  referrer: String,
  javascriptEnabled: String,
  userAgent: String,
  authId: String,
  areaOfTax: String,
  sessionId: String,
//  service: Option[String] //TODO not sent by apps for feedback but would be nice to have for consistency with support

  rating: String
)

object HmrcDeskproFeedback {
  implicit val writes: OWrites[HmrcDeskproFeedback] = Json.writes[HmrcDeskproFeedback]
}
