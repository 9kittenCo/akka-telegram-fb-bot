name := "telegtam-bot"

version := "0.1.2"

scalaVersion := "2.12.7"

libraryDependencies ++= {
  object Version {
    val scalaTest   = "3.0.1"
    val scalaMock   = "3.5.0"
    val slick       = "3.2.1"
    val Http4s      = "0.20.0-M2"
    val circe       = "0.10.0"
    val fs2         = "1.0.0"
    val cats        = "1.4.0"
    val doobie      = "0.6.0"
  }

  Seq(
    "org.typelevel"      %% "cats-core"                   % Version.cats,

    "org.http4s"         %% "http4s-dsl"                  % Version.Http4s,
    "org.http4s"         %% "http4s-blaze-server"         % Version.Http4s,
    "org.http4s"         %% "http4s-blaze-client"         % Version.Http4s,
    "org.http4s"         %% "http4s-circe"                % Version.Http4s,

    "co.fs2"             %% "fs2-core"                    % Version.fs2,
    "co.fs2"             %% "fs2-io"                      % Version.fs2,

    "com.typesafe.slick" %% "slick"                       % Version.slick,
    "com.typesafe.slick" %% "slick-hikaricp"              % Version.slick,
    "org.slf4j"           % "slf4j-nop"                   % "1.7.25",
    "org.postgresql"      % "postgresql"                  % "42.1.4",
    "org.flywaydb"        % "flyway-core"                 % "4.2.0",

    "org.tpolecat"       %% "doobie-core"                 % Version.doobie,
    "org.tpolecat"       %% "doobie-postgres"             % Version.doobie, // Postgres driver 42.2.2 + type mappings.
    "org.tpolecat"       %% "doobie-scalatest"            % Version.doobie, // ScalaTest support for typechecking statements.

    "io.circe"           %% "circe-core"                  % Version.circe,
    "io.circe"           %% "circe-jawn"                  % Version.circe,
    "io.circe"           %% "circe-generic"               % Version.circe,
    "io.circe"           %% "circe-generic-extras"        % Version.circe,
    "io.circe"           %% "circe-parser"                % Version.circe,

    "org.scalatest"      %% "scalatest"                   % Version.scalaTest % Test,
    "org.scalamock"      %% "scalamock-scalatest-support" % Version.scalaMock % Test
  )
}

fork        := true
Test / logBuffered := false
Test / javaOptions += "-Xmx8G"
resolvers   += Resolver.sonatypeRepo("releases")

val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint:-missing-interpolator,_",
  "-Yno-adapted-args",
//  "-Ywarn-value-discard",
  "-Ywarn-unused:patvars",
  "-language:existentials",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:postfixOps",
  "-Xfuture",
  "-Ypartial-unification"
)

scalacOptions in Compile ++= compilerOptions

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)