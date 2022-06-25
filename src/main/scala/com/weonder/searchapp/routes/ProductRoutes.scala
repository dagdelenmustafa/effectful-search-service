package com.weonder.searchapp.routes

import cats._
import cats.effect._
import cats.implicits._
import com.sksamuel.elastic4s.circe._
import com.weonder.searchapp.models.Product
import com.weonder.searchapp.services.ElasticsearchService
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class ProductRoutes[F[_]: JsonDecoder: Concurrent](elasticsearchService: ElasticsearchService[F]) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/product"

  implicit val decoder: EntityDecoder[F, Product] = jsonOf[F, Product]
  object query extends QueryParamDecoderMatcher[String]("q")

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "search" :? query(q) =>
      elasticsearchService.searchForQ[Product]("products", q).flatMap {
        case Left(value) => InternalServerError(value.reason)
        case Right(value) => Ok(value)
      }

    case req @ POST -> Root / "add" => for {
      product <- req.as[Product]
      resp <- elasticsearchService.insertInto("products", product).flatMap {
        case Left(value) => InternalServerError(value.reason)
        case Right(value) => Ok(value)
      }
    } yield resp

  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
