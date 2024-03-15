import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  private val bootstrapPlayVersion     = "7.19.0"
  private val playHmrcApiVersion       = "7.2.0-play-28"
  private val domainVersion            = "8.1.0-play-28"

  private val pegdownVersion         = "1.6.0"
  private val refinedVersion          = "0.9.26"
  private val wireMockVersion        = "2.21.0"
  private val scalaMockVersion       = "5.1.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "domain"                    % domainVersion,
    "uk.gov.hmrc" %% "play-hmrc-api"             % playHmrcApiVersion,
    "eu.timepit"  %% "refined"                    % refinedVersion
  )

  trait TestDependencies {
    lazy val scope: String        = "test"
    lazy val test:  Seq[ModuleID] = ???
  }

  object Test {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val test: Seq[ModuleID] = testCommon("test") ++ Seq(
            "org.scalamock" %% "scalamock" % scalaMockVersion % scope
          )
      }.test
  }

  object IntegrationTest {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val scope = "it"

        override lazy val test: Seq[ModuleID] = testCommon(scope) ++ Seq(
            "com.github.tomakehurst" % "wiremock" % wireMockVersion % scope
          )
      }.test
  }

  def testCommon(scope: String): Seq[ModuleID] = Seq(
    "org.pegdown"            % "pegdown"                 % pegdownVersion           % scope,
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapPlayVersion     % scope
  )

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}
