package model

import java.sql.Timestamp
import java.time.Duration
import java.util.Date

import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.semiauto._
import model.dal.{City, Page}

trait CirceDecoders {

  implicit val TimestampDecoder: Decoder[Timestamp] =
    new Decoder[Timestamp] {
      override def apply(c: HCursor): Result[Timestamp] =
        Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
    }

  // Models
  implicit val dateDecoder: Decoder[Date] = Decoder[Long].map(d => new Date(d))
  implicit val durationDecoder: Decoder[Duration] = Decoder[Int].map(d => Duration.ofSeconds(d.toLong))
  implicit val locationDecoder: Decoder[Location] = deriveDecoder[Location]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val chatDecoder: Decoder[Chat] = deriveDecoder[Chat]
  implicit val messageDecoder: Decoder[Message] = deriveDecoder[Message]

  implicit val updateDecoder: Decoder[Update] = deriveDecoder[Update]

  implicit def telegramResponseDecoder[T](implicit D: Decoder[T]): Decoder[TelegramResponse[T]] = deriveDecoder[TelegramResponse[T]]

  // Models Dal
  implicit val pageInfoDecoder: Decoder[PageInfo] = deriveDecoder[PageInfo]
  implicit val pageLocationDecoder: Decoder[PageLocation] = deriveDecoder[PageLocation]
  implicit val cityDecoder: Decoder[City] = deriveDecoder[City]
  implicit val pageDecoder: Decoder[Page] = deriveDecoder[Page]

  implicit val pageDistanceDecoder: Decoder[PageDistance] = deriveDecoder[PageDistance]

}
