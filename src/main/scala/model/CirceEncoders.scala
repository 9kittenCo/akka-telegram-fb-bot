package model

import java.time.Duration
import java.util.Date

import io.circe.Encoder
import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import model.dal.{City, Page}

trait CirceEncoders {

  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

  // Models
  implicit val dateEncoder: Encoder[Date] = Encoder[Long].contramap[Date](d ⇒ d.getTime)
  implicit val durationEncoder: Encoder[Duration] = Encoder[Int].contramap[Duration](d ⇒ d.getSeconds.toInt)

  implicit val callbackQueryEncoder: Encoder[CallbackQuery] = deriveEncoder[CallbackQuery]

  implicit val chatEncoder: Encoder[Chat] = deriveEncoder[Chat]

  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val updateEncoder: Encoder[Update] = deriveEncoder[Update]

  // Inline
  implicit val chosenInlineResultEncoder: Encoder[ChosenInlineResult] = deriveEncoder[ChosenInlineResult]
  implicit val inlineQueryEncoder: Encoder[InlineQuery] = deriveEncoder[InlineQuery]

  // Methods
  implicit val sendMessageJsonEncoder: Encoder[SendMessage] = deriveEncoder[SendMessage]
  implicit val getUpdatesJsonEncoder: Encoder[GetUpdates] = deriveEncoder[GetUpdates]
  implicit val sendLocationEncoder: Encoder[SendLocation] = deriveEncoder[SendLocation]

  // Models Dal
  implicit val pageInfoEncoder: Encoder[PageInfo] = deriveEncoder[PageInfo]
  implicit val pageLocationEncoder: Encoder[PageLocation] = deriveEncoder[PageLocation]
  implicit val cityEncoder: Encoder[City] = deriveEncoder[City]
  implicit val pageEncoder: Encoder[Page] = deriveEncoder[Page]

  implicit val pageDistanceEncoder: Encoder[PageDistance] = deriveEncoder[PageDistance]
}