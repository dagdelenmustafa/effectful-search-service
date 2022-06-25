import sbt._

object Dependencies {

  object V {
    val cats: String          = "2.7.0"
    val catsEffect: String    = "3.3.5"
    val catsEffectStd: String = "3.3.5"
    val ciris: String         = "2.1.1"
    val ip4s: String          = "3.1.2"
    val derevo: String        = "0.13.0"
    val log4cats: String      = "2.2.0"
    val logback: String       = "1.2.10"
    val http4s: String        = "0.23.1"
    val skunk: String         = "0.2.3"
    val refined: String       = "0.9.28"
    val newtype: String       = "0.4.4"
    val monocle: String       = "3.1.0"
    val doobie: String        = "1.0.0-RC1"
    val nscalaTime: String    = "2.30.0"
    val elastic4s: String     = "7.17.2"

    val betterMonadicFor: String = "0.3.1"
    val kindProjector: String    = "0.13.2"

  }

  object Libraries {
    def ciris(artifact: String): ModuleID  = "is.cir"       %% artifact            % V.ciris
    def derevo(artifact: String): ModuleID = "tf.tofu"      %% s"derevo-$artifact" % V.derevo
    def http4s(artifact: String): ModuleID = "org.http4s"   %% s"http4s-$artifact" % V.http4s
    def doobie(artifact: String): ModuleID = "org.tpolecat" %% s"doobie-$artifact" % V.doobie
    def elastic(artifact: String): ModuleID = "com.sksamuel.elastic4s" %% s"elastic4s-$artifact" % V.elastic4s

    val cats          = "org.typelevel"          %% "cats-core"               % V.cats
    val catsEffect    = "org.typelevel"          %% "cats-effect"             % V.catsEffect
    val catsEffectStd = "org.typelevel"          %% "cats-effect-std"         % V.catsEffectStd
    val ip4s          = "com.comcast"            %% "ip4s-core"               % V.ip4s
    val log4cats      = "org.typelevel"          %% "log4cats-slf4j"          % V.log4cats
    val log4catsCore  = "org.typelevel"          %% "log4cats-core"           % V.log4cats
    val skunkCore     = "org.tpolecat"           %% "skunk-core"              % V.skunk
    val skunkCirce    = "org.tpolecat"           %% "skunk-circe"             % V.skunk
    val refinedCore   = "eu.timepit"             %% "refined"                 % V.refined
    val refinedCats   = "eu.timepit"             %% "refined-cats"            % V.refined
    val newtype       = "io.estatico"            %% "newtype"                 % V.newtype
    val monocleCore   = "dev.optics"             %% "monocle-core"            % V.monocle
    val nscalaTime    = "com.github.nscala-time" %% "nscala-time"             % V.nscalaTime

    // Runtime
    val logback = "ch.qos.logback" % "logback-classic" % V.logback

    val cirisCore: ModuleID    = ciris("ciris")
    val cirisEnum: ModuleID    = ciris("ciris-enumeratum")
    val cirisRefined: ModuleID = ciris("ciris-refined")

    val derevoCore  = derevo("core")
    val derevoCats  = derevo("cats")
    val derevoCirce = derevo("circe-magnolia")

    val http4sDsl    = http4s("dsl")
    val http4sServer = http4s("ember-server")
    val http4sClient = http4s("ember-client")
    val http4sCirce  = http4s("circe")

    val doobieCore     = doobie("core")
    val doobiePostgres = doobie("postgres")
    val doobieHikari   = doobie("hikari")

    val elastic4sCore    = elastic("core")
    val elastic4sClient  = elastic("client-esjava")
    val elastic4sCats    = elastic("effect-cats")
    val elastic4sCirce   = elastic("json-circe")
  }

  object CompilerPlugin {
    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % V.betterMonadicFor
    )
    val kindProjector = compilerPlugin(
      "org.typelevel" % "kind-projector" % V.kindProjector cross CrossVersion.full
    )
  }

}
