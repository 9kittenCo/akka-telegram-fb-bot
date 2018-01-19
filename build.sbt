name := "akka-telegtam-bot"

version := "0.1.1"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  object Version {
    val akkaHttp    = "10.0.11"
    val scalaTest   = "3.0.1"
    val scalaMock   = "3.5.0"
    val slick       = "3.2.1"
    val circe       = "0.9.1"

  }

  Seq(
    "com.typesafe.akka"  %% "akka-http"                   % Version.akkaHttp,
    "com.typesafe.akka"  %% "akka-http-core"              % Version.akkaHttp,
    "com.typesafe.akka"  %% "akka-http-testkit"           % Version.akkaHttp % Test,

    "com.typesafe.slick" %% "slick"                       % Version.slick,
    "com.typesafe.slick" %% "slick-hikaricp"              % Version.slick,
    "org.slf4j"           % "slf4j-nop"                   % "1.7.25",
    "org.postgresql"      % "postgresql"                  % "42.1.4",
    "org.flywaydb"        % "flyway-core"                 % "4.2.0",

    "io.circe"           %% "circe-core"                  % Version.circe,
    "io.circe"           %% "circe-jawn"                  % Version.circe,
    "io.circe"           %% "circe-generic"               % Version.circe,
    "io.circe"           %% "circe-generic-extras"        % Version.circe,
    "io.circe"           %% "circe-parser"                % Version.circe,
    "de.heikoseeberger"  %% "akka-http-circe"             % "1.18.0",
//    "de.heikoseeberger"  %% "akka-http-circe"             % "1.20.0-RC1",

    "org.scalatest"      %% "scalatest"                   % Version.scalaTest % Test,
    "org.scalamock"      %% "scalamock-scalatest-support" % Version.scalaMock % Test
  )
}

resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

fork in run := true

//mainClass in (Compile,run) := Some("main/scala/Main")

val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Xlint:_",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Ypartial-unification",
  "-Ywarn-value-discard"
)

scalacOptions in Compile ++= compilerOptions