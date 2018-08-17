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

package uk.gov.hmrc.mobileusercontact.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.libs.json.JsValue

object HmrcDeskproStub {

  private val createFeedbackPath = "/deskpro/feedback"

  def createFeedbackWillSucceed(): Unit =
    stubFor(post(urlPathEqualTo(createFeedbackPath))
      .willReturn(aResponse()
        .withStatus(200)
        .withBody("{}")))

  def createFeedbackWillRespondWithInternalServerError(): Unit =
    stubFor(post(urlPathEqualTo(createFeedbackPath))
      .willReturn(aResponse()
        .withStatus(500)))

  def createFeedbackShouldHaveBeenCalled(body: JsValue): Unit =
    verify(1, postRequestedFor(urlPathEqualTo(createFeedbackPath))
      .withHeader("Content-Type", equalTo("application/json"))
      .withRequestBody(equalToJson(body.toString))
    )

  def createFeedbackShouldNotHaveBeenCalled(): Unit =
    verify(0, postRequestedFor(urlPathEqualTo(createFeedbackPath)))

}
