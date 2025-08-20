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

package uk.gov.hmrc.mobileusercontact.test

import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.slf4j.Logger
import play.api.LoggerLike

trait LoggerMock { this: MockFactory & OneInstancePerTest =>
  val slf4jLoggerMock: Logger = mock[Logger]

  (slf4jLoggerMock.isWarnEnabled _).expects().anyNumberOfTimes().returning(true)
  (slf4jLoggerMock.isInfoEnabled _).expects().anyNumberOfTimes().returning(true)

  val logger: LoggerLike = new LoggerLike {
    override val logger: Logger = slf4jLoggerMock
  }
}
