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
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.{JsObject, JsString, Json}
import uk.gov.hmrc.domain.Nino

object AuthStub {

  private val authoriseBodyWithItmpNameAndAllEnrolmentsRetrievals: String =
    """{
      |  "authorise": [{"confidenceLevel": 200}],
      |  "retrieve": ["optionalItmpName", "allEnrolments"]
      |}""".stripMargin

  private val authoriseBodyWithAllEnrolmentsRetrieval: String =
    """{
      |  "authorise": [{"confidenceLevel": 200}],
      |  "retrieve": ["allEnrolments"]
      |}""".stripMargin

  private val authoriseBodyConfidenceLevelElementOnly: String =
    """{
      |  "authorise": [{"confidenceLevel": 200}]
      |}""".stripMargin

  private val authoriseBodyRetrieveItmpNameElementOnly: String =
    """{
      |  "retrieve": ["optionalItmpName"]
      |}""".stripMargin

  def userIsLoggedIn(
    nino:       Option[Nino]   = None,
    givenName:  Option[String] = Some("Testy"),
    middleName: Option[String] = Some("Bobins"),
    familyName: Option[String] = Some("McTest")
  ): Unit = {

    val allEnrolmentsJson = Json.obj(
      "allEnrolments" -> nino.toSeq.map(n =>
        Json.obj(
          "key" -> "HMRC-NI",
          "identifiers" -> Seq(
            Json.obj(
              "key"   -> "NINO",
              "value" -> n.value
            )
          ),
          "state"           -> "Activated",
          "confidenceLevel" -> 200
        )
      )
    )

    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(authoriseBodyWithAllEnrolmentsRetrieval))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(allEnrolmentsJson.toString)
        )
    )

    val itmpNameAndAllEnrolmentsJson = allEnrolmentsJson ++ Json.obj(
        "optionalItmpName" -> JsObject(
          Seq(
            givenName.map("givenName"   -> JsString(_)),
            middleName.map("middleName" -> JsString(_)),
            familyName.map("familyName" -> JsString(_))
          ).flatten
        )
      )

    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(authoriseBodyWithItmpNameAndAllEnrolmentsRetrievals, true, false))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(itmpNameAndAllEnrolmentsJson.toString)
        )
    )
  }

  def userIsLoggedIn(): Unit = userIsLoggedIn(None, None, None)

  def userIsLoggedInWithInsufficientConfidenceLevel(): Unit =
    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(authoriseBodyConfidenceLevelElementOnly, false, true))
        .willReturn(
          aResponse()
            .withStatus(401)
            .withHeader("WWW-Authenticate", """MDTP detail="InsufficientConfidenceLevel"""")
        )
    )

  def userIsNotLoggedIn(): Unit =
    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .willReturn(
          aResponse()
            .withStatus(401)
            .withHeader("WWW-Authenticate", """MDTP detail="MissingBearerToken"""")
        )
    )

  def itmpNameShouldNotHaveBeenRetrieved(): Unit =
    verify(0,
           postRequestedFor(urlPathEqualTo("/auth/authorise"))
             .withRequestBody(equalToJson(authoriseBodyRetrieveItmpNameElementOnly, false, true)))
}
