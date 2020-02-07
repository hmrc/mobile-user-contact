import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Tests.{Group, SubProcess}
import scoverage.ScoverageKeys
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "mobile-user-contact"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    play.sbt.PlayScala,
    SbtAutoBuildPlugin,
    SbtGitVersioning,
    SbtDistributablesPlugin,
    SbtArtifactory
  )
  .settings(
    majorVersion := 0,
    playDefaultPort := 8250,
    scalaVersion := "2.12.8",
    libraryDependencies ++= AppDependencies(),
    dependencyOverrides ++= AppDependencies.overrides,
    evictionWarningOptions in update := EvictionWarningOptions.default
      .withWarnScalaVersionEviction(false),
    resolvers += Resolver.jcenterRepo,
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimum := 95.00,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
  .settings(
    routesImport ++= Seq(
      "uk.gov.hmrc.mobileusercontact.domain.types._",
      "uk.gov.hmrc.mobileusercontact.domain.types.ModelTypes._"
    )
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value,
    testGrouping in IntegrationTest := oneForkedJvmPerTest(
      (definedTests in IntegrationTest).value
    )
  )

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
  tests map { test =>
    Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}"))))
  }
