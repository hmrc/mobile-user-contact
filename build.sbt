import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Tests.{Group, SubProcess}
import scoverage.ScoverageKeys

val appName = "mobile-user-contact"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    play.sbt.PlayScala,
    SbtAutoBuildPlugin,
    SbtDistributablesPlugin
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    playDefaultPort := 8250,
    scalaVersion := "2.12.15",
    libraryDependencies ++= AppDependencies(),
    update / evictionWarningOptions := EvictionWarningOptions.default
      .withWarnScalaVersionEviction(false),
    resolvers += Resolver.jcenterRepo,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimumStmtTotal := 95.00,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
  .settings(
    routesImport ++= Seq(
      "uk.gov.hmrc.mobileusercontact.domain.types._",
      "uk.gov.hmrc.mobileusercontact.domain.types.ModelTypes._"
    )
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    IntegrationTest / testGrouping := oneForkedJvmPerTest(
      (IntegrationTest / definedTests).value
    )
  )

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
  tests map { test =>
    Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}"))))
  }
