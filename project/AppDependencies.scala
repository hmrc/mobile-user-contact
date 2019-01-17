import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % "0.35.0",
    "uk.gov.hmrc" %% "domain"            % "5.3.0",
    "uk.gov.hmrc" %% "play-hmrc-api"     % "3.4.0-play-26"
  )

  private val scalatestPlusPlayVersion = "3.1.2"

  val test: Seq[ModuleID] = testCommon("test") ++ Seq(
    "org.scalamock" %% "scalamock" % "4.1.0" % "test"
  )

  val integrationTest: Seq[ModuleID] = testCommon("it") ++ Seq(
    "com.github.tomakehurst" % "wiremock" % "2.20.0" % "it"
  )

  def testCommon(scope: String): Seq[ModuleID] = Seq(
    "org.scalatest"          %% "scalatest"          % "3.0.5"                  % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
    "org.pegdown"            % "pegdown"             % "1.6.0"                  % scope,
    "com.typesafe.play"      %% "play-test"          % PlayVersion.current      % scope,
    // workaround for version clash in IntelliJ where without this line both jetty-util-9.2.15.v20160210 and jetty-util-9.2.22.v20170606 are brought in
    // which results in a NoSuchMethodError when running FeedbackISpec
    "org.eclipse.jetty.websocket" % "websocket-client" % "9.2.22.v20170606" % scope
  )

  // Transitive dependencies in scalatest/scalatestplusplay drag in a newer version of jetty that is not
  // compatible with wiremock, so we need to pin the jetty stuff to the older version.
  // see https://groups.google.com/forum/#!topic/play-framework/HAIM1ukUCnI
  val jettyVersion = "9.2.13.v20150730"
  def overrides(): Set[ModuleID] = Set(
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
}
