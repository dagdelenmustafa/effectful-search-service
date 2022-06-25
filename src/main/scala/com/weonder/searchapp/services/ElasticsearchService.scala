package com.weonder.searchapp.services

import cats.effect._
import cats.implicits._
import com.sksamuel.elastic4s._
import org.typelevel.log4cats.Logger

import java.io.FileNotFoundException
import scala.reflect.ClassTag

trait ElasticsearchService[F[_]] {
  def searchForQ[A: HitReader: ClassTag](index: String, q: String): F[Either[ElasticError, List[A]]]
  def createEsIndex(indexName: String, mappingPath: String): F[Either[String, String]]
  def insertInto[A: Indexable](indexName: String, data: A): F[Either[ElasticError, A]]
}

object ElasticsearchService {
  import com.sksamuel.elastic4s.ElasticDsl._

  def make[F[_]: Async: Functor: Executor: Logger](esClient: ElasticClient): ElasticsearchService[F] = {
    new ElasticsearchService[F] {
      override def searchForQ[A: HitReader: ClassTag](index: String, q: String): F[Either[ElasticError, List[A]]] = esClient.execute {
        search(index).query(q)
      }.map {
        case RequestFailure(_, _, _, e) => Left(e)
        case RequestSuccess(_, _, _, r) => Right(r.to[A].toList)
      }

      override def createEsIndex(indexName: String, mappingPath: String): F[Either[String, String]] = {
        val indexSourceIO = Async[F].bracket {
          Async[F].delay(scala.io.Source.fromFile(mappingPath))
        } { file =>
          Async[F].delay(file.mkString)
        } { file => Async[F].delay(file.close())}

        (for {
          indexSource <- indexSourceIO
          a <- esClient.execute(createIndex(indexName).source(indexSource)).map {
            case RequestFailure(_, _, _, e) =>
              s"Index creation is skipping: ${e.reason}".asRight
            case RequestSuccess(_, _, _, _) =>
              s"Index('$indexName') successfully created".asRight
            case _ => s"Index creation is failed.".asLeft
          }
        } yield a).handleErrorWith {
          case _: FileNotFoundException =>
            Async[F].delay(s"Index creation is failed: Given mapping path is incorrect.".asLeft)
          case e => Async[F].delay(s"Index creation is failed: ${e.getMessage}".asLeft)
        }
      }

      override def insertInto[A: Indexable](indexName: String, data: A): F[Either[ElasticError, A]] = {
        esClient.execute {
          indexInto(indexName).source(data).refreshImmediately
        }.map {
          case RequestFailure(_, _, _, e) => Left(e)
          case RequestSuccess(_, _, _, _) => Right(data)
        }
      }
    }
  }

}
