package bot

import api.TelegramApiService
import cats.effect.Effect
import fs2.Stream
import model.TelegramApiModels.{TelegramResponse, Update}
import cats.implicits._


trait EventSource[F[_]] {
  def events(): Stream[F, Update]
}

class EventSourceImpl[F[_] : Effect](api: TelegramApiService[F], initialOffset: Long) extends EventSource[F] {

  override def events(): Stream[F, Update] = {
    Stream(()).repeat.covary[F]
      .evalMapAccumulate[Long, TelegramResponse[Seq[Update]]](initialOffset) { case (offset, _) =>
      api.getUpdates(offset).map((response: TelegramResponse[Seq[Update]]) => (if (response.result.nonEmpty) response.result.maxBy(_.updateId).updateId + 1 else offset, response))
    }.flatMap { case (_, response) => Stream.emits(response.result) }
  }
}

object EventSourceImpl {
  def apply[F[_] : Effect](api: TelegramApiService[F], initialOffset: Long = 0): EventSourceImpl[F] =
    new EventSourceImpl[F](api, initialOffset)
}
