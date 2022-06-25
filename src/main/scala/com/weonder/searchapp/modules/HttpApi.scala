package com.weonder.searchapp.modules

import cats.effect.Async
import cats.implicits._
import com.weonder.searchapp.routes.{HealthcheckRoutes, ProductRoutes}
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.middleware._

import scala.concurrent.duration.DurationInt

object HttpApi {
  def make[F[_]: Async](services: Services[F]): HttpApi[F] =
    new HttpApi[F](services) {}
}

sealed abstract class HttpApi[F[_]: Async] private (services: Services[F]) {

  private val healthRoutes = HealthcheckRoutes[F](services.healthcheckService).routes
  private val productRoutes = ProductRoutes[F](services.elasticsearchService).routes

  private val openRoutes: HttpRoutes[F] = healthRoutes <+> productRoutes

  private val routes: HttpRoutes[F] = openRoutes

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60.seconds)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(logHeaders = true, logBody = true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(logHeaders = true, logBody = true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)
}
