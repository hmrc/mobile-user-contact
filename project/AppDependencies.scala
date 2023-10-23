import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  private val bootstrapPlayVersion     = "5.24.0"
  private val playHmrcApiVersion       = "7.0.0-play-28"
  private val scalaTestPlusPlayVersion = "4.0.3"
  private val domainVersion            = "8.1.0-play-28"

  private val scalaTestVersion       = "3.0.8"
  private val pegdownVersion         = "1.6.0"
  private val refinedVersion         = "0.9.4"
  private val wireMockVersion        = "2.21.0"
  private val scalaMockVersion       = "4.1.0"
  private val websocketClientVersion = "9.2.22.v20170606"

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
    "org.scalatest"          %% "scalatest"              % scalaTestVersion         % scope,
    "org.scalatestplus.play" %% "scalatestplus-play"     % scalaTestPlusPlayVersion % scope,
    "org.pegdown"            % "pegdown"                 % pegdownVersion           % scope,
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current      % scope,
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapPlayVersion     % scope
  )

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}
