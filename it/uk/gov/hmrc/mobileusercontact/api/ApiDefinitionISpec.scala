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

package uk.gov.hmrc.mobileusercontact.api

import org.scalatest.concurrent.Eventually
import uk.gov.hmrc.mobileusercontact.test.BaseISpec

class ApiDefinitionISpec extends BaseISpec with Eventually {

  "GET /api/definition" should {

    "provide definition with configurable whitelist" in {
      val response = await(wsUrl("/api/definition").get())
      response.status shouldBe 200

      response.header("Content-Type") shouldBe Some("application/json")

      val definition = response.json
      (definition \\ "version").map(_.as[String]).head shouldBe "1.0"

      val accessConfigs = definition \ "api" \ "versions" \\ "access"
      accessConfigs.length should be > 0
      accessConfigs.foreach { accessConfig =>
        (accessConfig \ "type").as[String]                           shouldBe "TEST_ACCESS_TYPE"
        (accessConfig \ "whitelistedApplicationIds").head.as[String] shouldBe "00010002-0003-0004-0005-000600070008"
        (accessConfig \ "whitelistedApplicationIds")(1).as[String]   shouldBe "00090002-0003-0004-0005-000600070008"
      }
    }
  }

  "GET /api/conf/1.0/application.yaml" should {
    "return YAML" in {
      val response = await(wsUrl("/api/conf/1.0/application.yaml").get())
      response.status shouldBe 200
    }
  }
}
