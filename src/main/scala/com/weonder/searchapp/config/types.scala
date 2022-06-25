package com.weonder.searchapp.config

import ciris.Secret
import com.comcast.ip4s.{Host, Port}
import eu.timepit.refined.types.string.NonEmptyString

object types {
  case class AppConfig(appName: Secret[String], httpServerConfig: HttpServerConfig, elasticsearchConfig: ElasticsearchConfig)
  case class HttpServerConfig(host: Host, port: Port)
  case class ElasticsearchConfig(connectionUrl: NonEmptyString, mappingPath: NonEmptyString)
}
