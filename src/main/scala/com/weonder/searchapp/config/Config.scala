package com.weonder.searchapp.config

import cats.effect.Async
import cats.syntax.all._
import ciris._
import ciris.refined._
import com.comcast.ip4s._
import com.weonder.searchapp.config.AppEnvironment._
import com.weonder.searchapp.config.types.{AppConfig, ElasticsearchConfig, HttpServerConfig}
import eu.timepit.refined.types.string.NonEmptyString

object Config {
  def load[F[_]: Async]: F[AppConfig] =
    env("ENV")
      .as[AppEnvironment]
      .flatMap {
        case Dev =>
          default[F]
        case Test =>
          default[F]
        case Prod =>
          default[F]
      }
      .load[F]

  private def default[F[_]]: ConfigValue[F, AppConfig] = {
    (
      env("APP_NAME").as[String].secret,
      env("HOST").as[String],
      env("PORT").as[String],
      env("ES_CONN_STRING").as[NonEmptyString],
      env("ES_MAPPING_PATH").as[NonEmptyString]
    ).parMapN { (
                  appName,
                  httpHost,
                  httpPort,
                  esConnectionString,
                  esMappingPath
                ) =>
      AppConfig(
        appName = appName,
        httpServerConfig = HttpServerConfig(
          host = Host.fromString(httpHost).getOrElse(host"0.0.0.0"),
          port = Port.fromString(httpPort).getOrElse(port"9090")
        ),
        elasticsearchConfig = ElasticsearchConfig(esConnectionString, esMappingPath)
      )
    }
  }
}
