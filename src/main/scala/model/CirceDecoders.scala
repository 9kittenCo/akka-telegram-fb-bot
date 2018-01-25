package model

import java.time.Duration
import java.util.Date

import io.circe.Decoder
import io.circe.generic.semiauto._
import model.dal.{City, Page}

trait CirceDecoders {

  // Models
  implicit val dateDecoder: Decoder[Date]         = Decoder[Long].map(d => new Date(d))
  implicit val durationDecoder: Decoder[Duration] = Decoder[Int].map(d => Duration.ofSeconds(d.toLong))

  implicit val locationDecoder: Decoder[Location]                         = deriveDecoder[Location]

  implicit val userDecoder: Decoder[User]                           = deriveDecoder[User]


  implicit val messageDecoder: Decoder[Message]             = deriveDecoder[Message]
  implicit val callbackQueryDecoder: Decoder[CallbackQuery] = deriveDecoder[CallbackQuery]

  // Inline
  implicit val inlineQueryDecoder: Decoder[InlineQuery]             = deriveDecoder[InlineQuery]
  implicit val chosenInlineResultDecoder: Decoder[ChosenInlineResult] = deriveDecoder[ChosenInlineResult]

  implicit def eitherResponseDecoder[A, B](implicit D: Decoder[A], DD: Decoder[B]): Decoder[Either[A, B]] =
    deriveDecoder[Either[A, B]]

  implicit val updateDecoder: Decoder[Update]                                   = deriveDecoder[Update]
  implicit def responseDecoder[T](implicit D: Decoder[T]): Decoder[TelegramResponse[T]] = deriveDecoder[TelegramResponse[T]]

  // Models Dal
  implicit val pageInfoDecoder: Decoder[PageInfo] = deriveDecoder[PageInfo]
  implicit val pageLocationDecoder: Decoder[PageLocation] = deriveDecoder[PageLocation]
  implicit val cityDecoder: Decoder[City] = deriveDecoder[City]
  implicit val pageDecoder: Decoder[Page] = deriveDecoder[Page]

  implicit val pageDistanceDecoder: Decoder[PageDistance] = deriveDecoder[PageDistance]

}