import sbt._

object Dependencies {

  lazy val catsEffectVersion = "2.1.3"
  lazy val pprintVersion     = "0.5.9"
  lazy val munitVersion      = "0.7.9"
  lazy val scalaCheckVersion = "1.14.3"

  lazy val catsEffect = "org.typelevel"  %% "cats-effect" % catsEffectVersion
  lazy val pprint     = "com.lihaoyi"    %% "pprint"      % pprintVersion
  lazy val munit      = "org.scalameta"  %% "munit"       % munitVersion
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck"  % scalaCheckVersion

  lazy val kindProjectorVersion    = "0.11.0"
  lazy val betterMonadicForVersion = "0.3.1"

  // https://github.com/typelevel/kind-projector
  lazy val kindProjectorPlugin    = compilerPlugin(
    compilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  )
  // https://github.com/oleg-py/better-monadic-for
  lazy val betterMonadicForPlugin = compilerPlugin(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
  )

  def scalaCompiler(scalaVersion: String) = "org.scala-lang" % "scala-compiler" % scalaVersion
  def scalaReflect(scalaVersion: String)  = "org.scala-lang" % "scala-reflect"  % scalaVersion

  def coreDependencies(scalaVersion: String)  =
    Seq(
      pprint,
      munit,
      kindProjectorPlugin,
      betterMonadicForPlugin
    ) ++ Seq(
      scalaCheck
    ).map(_ % Test)

  def hutilDependencies(scalaVersion: String) =
    Seq(
      scalaCompiler(scalaVersion),
      scalaReflect(scalaVersion),
      catsEffect
    )
}
