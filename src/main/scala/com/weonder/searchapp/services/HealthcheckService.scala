package com.weonder.searchapp.services

import cats.effect.Temporal
import com.weonder.searchapp.models.AppStatus

trait HealthcheckService[F[_]] {
  def status: F[AppStatus]
}

object HealthcheckService {
  def make[F[_]](implicit F: Temporal[F]): HealthcheckService[F] = new HealthcheckService[F] {
      val status: F[AppStatus] = F.pure(AppStatus(true))
    }
}
