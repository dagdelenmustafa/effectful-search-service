import Dependencies.Libraries
import Dependencies.CompilerPlugin

ThisBuild / organization := "com.weonder"
ThisBuild / scalaVersion := "2.13.8"

val versionNumber = "0.0.1"

lazy val root = (project in file(".")).settings(
  name := "search-service",
  scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
  libraryDependencies ++= Seq(
    CompilerPlugin.kindProjector,
    CompilerPlugin.betterMonadicFor,
    Libraries.cats,
    Libraries.catsEffect,
    Libraries.catsEffectStd,
    Libraries.log4cats,
    Libraries.log4catsCore,
    Libraries.logback % Runtime,
    Libraries.http4sDsl,
    Libraries.http4sServer,
    Libraries.http4sClient,
    Libraries.http4sCirce,
    Libraries.cirisCore,
    Libraries.cirisEnum,
    Libraries.cirisRefined,
    Libraries.ip4s,
    Libraries.refinedCats,
    Libraries.refinedCore,
    Libraries.derevoCore,
    Libraries.derevoCats,
    Libraries.derevoCirce,
    Libraries.elastic4sCore,
    Libraries.elastic4sCats,
    Libraries.elastic4sCirce,
    Libraries.elastic4sClient
  )
)

assemblyMergeStrategy in assembly := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    xs map {_.toLowerCase} match {
      case "manifest.mf" :: Nil | "index.list" :: Nil | "dependencies" :: Nil =>
        MergeStrategy.discard
      case ps @ x :: xs if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case "spring.schemas" :: Nil | "spring.handlers" :: Nil =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.last
    }
  case _ => MergeStrategy.last
}

assemblyJarName in assembly := s"search-service-assembly-$versionNumber.jar"

enablePlugins(AssemblyPlugin)
