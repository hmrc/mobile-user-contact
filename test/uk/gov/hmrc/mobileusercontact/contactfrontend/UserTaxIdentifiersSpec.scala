/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.mobileusercontact.contactfrontend

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.*

class UserTaxIdentifiersSpec extends AnyWordSpec with Matchers {

  "UserTaxIdentifiers JSON format" should {

    "serialize to JSON correctly" in {
      val identifiers = UserTaxIdentifiers(
        nino   = Some("AB123456C"),
        ctUtr  = Some("1234567890"),
        utr    = None,
        vrn    = Some("987654321"),
        empRef = None
      )

      val json = Json.toJson(identifiers)

      (json \ "nino").as[String]  shouldBe "AB123456C"
      (json \ "ctUtr").as[String] shouldBe "1234567890"
      (json \ "utr").toOption     shouldBe None
      (json \ "vrn").as[String]   shouldBe "987654321"
      (json \ "empRef").toOption  shouldBe None
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "nino"  -> "AB123456C",
        "ctUtr" -> "1234567890",
        "vrn"   -> "987654321"
        // utr and empRef omitted
      )

      val identifiers = json.as[UserTaxIdentifiers]

      identifiers.nino   shouldBe Some("AB123456C")
      identifiers.ctUtr  shouldBe Some("1234567890")
      identifiers.utr    shouldBe None
      identifiers.vrn    shouldBe Some("987654321")
      identifiers.empRef shouldBe None
    }

    "perform round-trip JSON serialization and deserialization" in {
      val identifiers = UserTaxIdentifiers(
        nino   = Some("AB123456C"),
        ctUtr  = None,
        utr    = Some("4444444444"),
        vrn    = None,
        empRef = Some("123/AB456")
      )

      val json = Json.toJson(identifiers)
      val parsed = json.as[UserTaxIdentifiers]

      parsed shouldBe identifiers
    }
  }
}
