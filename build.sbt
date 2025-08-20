import play.sbt.PlayImport.PlayKeys.playDefaultPort
import scoverage.ScoverageKeys

val appName = "mobile-user-contact"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    play.sbt.PlayScala,
    SbtDistributablesPlugin
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    playDefaultPort := 8250,
    scalaVersion := "3.6.4",
    libraryDependencies ++= AppDependencies(),
    update / evictionWarningOptions := EvictionWarningOptions.default
      .withWarnScalaVersionEviction(false),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    coverageExcludedPackages := "<empty>;.*Routes.*;app.*;.*prod;.*definition;.*testOnlyDoNotUseInAppConf;.*com.kenshoo.*;.*javascript.*;.*BuildInfo;.*Reverse.*;.*binder.*;.*mobileusercontact.config.*;.*mobileusercontact.api.*;.*mobileusercontact.views.*;.*mobileusercontact.connectors.*",
    coverageMinimumStmtTotal := 95.00,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )
  .settings(
    routesImport ++= Seq(
      "uk.gov.hmrc.mobileusercontact.domain.types._",
      "uk.gov.hmrc.mobileusercontact.domain.types.JourneyId._"
    )
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value
  )
  .settings(
    scalacOptions ++= Seq(
      // Suppress warnings matching specific message pattern
      "-Wconf:msg=possible missing interpolator.*\\$date:silent",
      // Suppress warnings in generated files (e.g., target directory)
      "-Wconf:src=target/.*:silent",
      // Default rule to warn on everything else
      "-Wconf:any:warning"
    )
  )
