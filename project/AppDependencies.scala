import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % "1.7.0",
    "uk.gov.hmrc" %% "play-hmrc-api" % "3.0.0"
  )

  val test: Seq[ModuleID] = testCommon("test")

  val integrationTest: Seq[ModuleID] = testCommon("it") ++ Seq(
    "com.github.tomakehurst" % "wiremock" % "2.13.0" % "it"
  )

  def testCommon(scope: String) = Seq(
    "uk.gov.hmrc" %% "hmrctest" % "3.0.0" % scope,
    "org.scalatest" %% "scalatest" % "3.0.5" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,

    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
  )

}
