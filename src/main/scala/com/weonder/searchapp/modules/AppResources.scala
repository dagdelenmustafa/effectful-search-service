package com.weonder.searchapp.modules

import cats.effect._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.http.JavaClient
import com.weonder.searchapp.config.types.{AppConfig, ElasticsearchConfig}

sealed abstract class AppResources[F[_]](val elasticsearchClient: ElasticClient)

object AppResources {
  def make[F[_]: Async](cfg: AppConfig): Resource[F, AppResources[F]] = {

    def mkElasticsearchResource(c: ElasticsearchConfig): Resource[F, ElasticClient] = {
      Resource.make {
        Async[F].delay(ElasticClient(JavaClient(ElasticProperties(c.connectionUrl.value))))
      } (c =>  Async[F].delay(c.close()))
    }

    mkElasticsearchResource(cfg.elasticsearchConfig).map(new AppResources[F](_) {})
  }
}
