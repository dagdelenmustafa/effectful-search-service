package com.weonder.searchapp.modules

import cats.effect.Async
import com.weonder.searchapp.services.{ElasticsearchService, HealthcheckService}
import org.typelevel.log4cats.Logger

object Services {
  import com.sksamuel.elastic4s.cats.effect.instances._

  def make[F[_]: Async: Logger](resources: AppResources[F]): Services[F] = {
    new Services[F](
      healthcheckService = HealthcheckService.make[F],
      elasticsearchService = ElasticsearchService.make[F](resources.elasticsearchClient)
    ) {}
  }
}

sealed abstract class Services[F[_]] private (val healthcheckService: HealthcheckService[F],
                                              val elasticsearchService: ElasticsearchService[F])