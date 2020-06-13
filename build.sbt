val projectName        = "typeclasses-from-the-ground-up"
val projectDescription = "Type Classes from the Ground up (according to Nicolas Rinaudo's Talk)"
val projectVersion     = "0.1.0"

val scala213 = "2.13.2"

lazy val commonSettings =
  Seq(
    version := projectVersion,
    scalaVersion := scala213,
    publish / skip := true,
    scalacOptions ++= ScalacOptions.defaultScalacOptions,
    Compile / console / scalacOptions := ScalacOptions.consoleScalacOptions,
    Test / console / scalacOptions := ScalacOptions.consoleScalacOptions,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixDependencies ++= Seq("com.github.liancheng" %% "organize-imports" % "latest.release"),
    Test / parallelExecution := false,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-s", "100"), // -s = -minSuccessfulTests
    testFrameworks += new TestFramework("munit.Framework"),
    initialCommands :=
      s"""|
         |import scala.util.chaining._
         |import hutil.stringformat._
         |import pprint._
         |println
         |""".stripMargin
  )

lazy val root = (project in file("."))
  .aggregate(core)
  .settings(commonSettings)
  .settings(
    name := "root",
    description := "root project",
    sourceDirectories := Seq.empty
  )

lazy val core = (project in file("core"))
  .dependsOn(hutil)
  .settings(commonSettings)
  .settings(
    name := projectName,
    description := projectDescription,
    libraryDependencies ++= Dependencies.coreDependencies(scalaVersion.value)
  )

lazy val hutil = (project in file("hutil"))
  .settings(commonSettings)
  .settings(
    name := "hutil",
    description := "Hermann's Utilities",
    libraryDependencies ++= Dependencies.hutilDependencies(scalaVersion.value)
  )

// GraphBuddy support
// resolvers += Resolver.bintrayRepo("virtuslab", "graphbuddy")
// addCompilerPlugin("com.virtuslab.semanticgraphs" % "scalac-plugin" % "0.0.10" cross CrossVersion.full)
// scalacOptions += "-Yrangepos"
