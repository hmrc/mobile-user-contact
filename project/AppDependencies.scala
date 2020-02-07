import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  private val bootstrapPlayVersion     = "1.3.0"
  private val playHmrcApiVersion       = "4.1.0-play-26"
  private val scalaTestPlusPlayVersion = "3.1.2"
  private val domainVersion            = "5.6.0-play-26"

  private val scalaTestVersion       = "3.0.8"
  private val pegdownVersion         = "1.6.0"
  private val refinedVersion         = "0.9.4"
  private val wireMockVersion        = "2.21.0"
  private val scalaMockVersion       = "4.1.0"
  private val websocketClientVersion = "9.2.22.v20170606"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "domain"            % domainVersion,
    "uk.gov.hmrc" %% "play-hmrc-api"     % playHmrcApiVersion,
    "eu.timepit"  %% "refined"           % refinedVersion
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
    "org.scalatest"          %% "scalatest"          % scalaTestVersion         % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % scope,
    "org.pegdown"            % "pegdown"             % pegdownVersion           % scope,
    "com.typesafe.play"      %% "play-test"          % PlayVersion.current      % scope,
    // workaround for version clash in IntelliJ where without this line both jetty-util-9.2.15.v20160210 and jetty-util-9.2.22.v20170606 are brought in
    // which results in a NoSuchMethodError when running FeedbackISpec
    "org.eclipse.jetty.websocket" % "websocket-client" % websocketClientVersion % scope
  )

  // Transitive dependencies in scalatest/scalatestplusplay drag in a newer version of jetty that is not
  // compatible with wiremock, so we need to pin the jetty stuff to the older version.
  // see https://groups.google.com/forum/#!topic/play-framework/HAIM1ukUCnI
  val jettyVersion = "9.2.13.v20150730"

  val overrides: Seq[ModuleID] = Seq(
    "org.eclipse.jetty"           % "jetty-server"       % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-servlet"      % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-security"     % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-servlets"     % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-continuation" % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-webapp"       % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-xml"          % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-client"       % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-http"         % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-io"           % jettyVersion % "it",
    "org.eclipse.jetty"           % "jetty-util"         % jettyVersion % "it",
    "org.eclipse.jetty.websocket" % "websocket-api"      % jettyVersion % "it",
    "org.eclipse.jetty.websocket" % "websocket-common"   % jettyVersion % "it",
    "org.eclipse.jetty.websocket" % "websocket-client"   % jettyVersion % "it"
  )

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}
