package com.weonder.searchapp

import cats.effect.std.Supervisor
import cats.effect.{ IO, IOApp}
import com.weonder.searchapp.config.Config
import com.weonder.searchapp.modules.{AppResources, HttpApi, Services}
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    Config.load[IO].flatMap { cfg =>
      Logger[IO].info(s"Loaded config $cfg") >>
        Supervisor[IO].use { implicit sp =>
          AppResources
            .make[IO](cfg)
            .evalMap { res =>
              val services = Services.make[IO](res)
              val api = HttpApi.make[IO](services)

              services.elasticsearchService.createEsIndex("products", cfg.elasticsearchConfig.mappingPath.value)
                .flatMap {
                  case Left(value) =>
                    Logger[IO].error(s"Exception on initial operations: $value") >>
                      IO.raiseError(new RuntimeException(s"Exception on initial operations: $value"))
                  case Right(value) =>
                    Logger[IO].info(s"Initial operations finished successfully: $value")
                } >> IO(api.httpApp)
            }.flatMap(httpApp => MkHttpServer[IO].newEmber(cfg.httpServerConfig, httpApp)).useForever
      }
    }
  }
}
