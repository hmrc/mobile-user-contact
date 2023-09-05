package uk.gov.hmrc.mobileusercontact.test

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Application
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.domain.Generator

trait BaseISpec
    extends AnyWordSpec
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with WireMockSupport
    with OneServerPerSuiteWsClient {

  override implicit lazy val app: Application = appBuilder.build()

  private val generator = new Generator(0)
  val nino              = generator.nextNino
  val journeyId:               String           = "3051e9be-e4de-11ed-b5ea-0242ac120002"
  val authorisationJsonHeader: (String, String) = "AUTHORIZATION" -> "Bearer 123"

}
