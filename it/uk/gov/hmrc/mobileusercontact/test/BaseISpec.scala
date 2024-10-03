package uk.gov.hmrc.mobileusercontact.test

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.libs.ws.WSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.domain.Generator

trait BaseISpec
    extends AnyWordSpec
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with WireMockSupport
    with WsScalaTestClient
    with GuiceOneServerPerSuite {

  def configuration: Map[String, Any] =
    Map(
      "auditing.enabled" -> false,
      "metrics.enabled"  -> false
    )

  override implicit lazy val app: Application = appBuilder.configure(configuration).build()

  protected implicit lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]

  private val generator = new Generator(0)
  val nino              = generator.nextNino
  val journeyId:               String           = "3051e9be-e4de-11ed-b5ea-0242ac120002"
  val authorisationJsonHeader: (String, String) = "AUTHORIZATION" -> "Bearer 123"

}
