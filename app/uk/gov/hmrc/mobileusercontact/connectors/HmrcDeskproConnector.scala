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

package uk.gov.hmrc.mobileusercontact.connectors

import java.net.URL
import com.google.inject.ImplementedBy

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json, OWrites, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpException}
import uk.gov.hmrc.mobileusercontact.config.HmrcDeskproConnectorConfig
import uk.gov.hmrc.mobileusercontact.contactfrontend.UserTaxIdentifiers
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[HmrcDeskproConnectorImpl])
trait HmrcDeskproConnector {

  def createFeedback(
    feedback:    HmrcDeskproFeedback
  )(implicit hc: HeaderCarrier,
    ec:          ExecutionContext
  ): Future[Unit]

  def createSupport(
    ticket:      HmrcDeskproSupport
  )(implicit hc: HeaderCarrier,
    ec:          ExecutionContext
  ): Future[Unit]
}

@Singleton
class HmrcDeskproConnectorImpl @Inject() (
  http:   HttpClientV2,
  config: HmrcDeskproConnectorConfig)
    extends HmrcDeskproConnector {

  def createFeedback(
    ticket:      HmrcDeskproFeedback
  )(implicit hc: HeaderCarrier,
    ec:          ExecutionContext
  ): Future[Unit] = create("feedback", ticket)

  def createSupport(
    ticket:      HmrcDeskproSupport
  )(implicit hc: HeaderCarrier,
    ec:          ExecutionContext
  ): Future[Unit] = create("get-help-ticket", ticket)

  private def create[T](
    resource:    String,
    ticket:      T
  )(implicit hc: HeaderCarrier,
    ec:          ExecutionContext,
    wts:         Writes[T]
  ): Future[Unit] =
    http
      .post(deskproUrl(resource))
      .withBody(Json.toJson(ticket))
      .execute[JsValue]
      .map(_ => ())
      .recover {
        case e: HttpException =>
          new HttpException("", e.responseCode)
      }

  private val deskproUrl = (resource: String) => new URL(config.hmrcDeskproBaseUrl, s"/deskpro/$resource")
}

case class HmrcDeskproFeedback(
  name:               String,
  email:              String,
  subject:            String,
  message:            String,
  referrer:           String,
  javascriptEnabled:  String,
  userAgent:          String,
  authId:             String,
  areaOfTax:          String,
  sessionId:          String,
  rating:             String,
  userTaxIdentifiers: UserTaxIdentifiers)

object HmrcDeskproFeedback {
  implicit val writes: OWrites[HmrcDeskproFeedback] = Json.writes[HmrcDeskproFeedback]
}

case class HmrcDeskproSupport(
  name:               String,
  email:              String,
  subject:            String,
  message:            String,
  referrer:           String,
  javascriptEnabled:  String,
  userAgent:          String,
  authId:             String,
  areaOfTax:          String,
  sessionId:          String,
  service:            Option[String],
  userTaxIdentifiers: UserTaxIdentifiers)

object HmrcDeskproSupport {
  implicit val writes: OWrites[HmrcDeskproSupport] = Json.writes[HmrcDeskproSupport]
}
