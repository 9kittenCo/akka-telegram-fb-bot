package bot

import api.TelegramApiService
import cats.effect.Effect
import cats.implicits._
import client.FacebookClient
import model.PageDistance

class CowobotService[F[_] : Effect](tgClient: TelegramApiService[F], fbClient: FacebookClient[F]) extends CowobotServiceAlgebra[F] {
  override def parseUserMessages(messagesF: F[List[ProcessedMessage]]): F[List[ParsedUserMessage]] = {
    val r:F[List[ParsedUserMessage]] = for {
      messagesList <- messagesF
      message      <- messagesList
      pagesByLoc   <- fbClient.getPagesByLocation(message.location.latitude, message.location.longitude)
      pg           <- pagesByLoc
    } yield toParsedUserMessage(message, pg)
    r
  }

  override def parseUserMessage(messageF: F[ProcessedMessage]): F[List[ParsedUserMessage]] = {
    for {
      message      <- messageF
      pagesByLoc   <- fbClient.getPagesByLocation(message.location.latitude, message.location.longitude)
      pg           <- pagesByLoc

    } yield toParsedUserMessage(message, pg)
  }

  def toParsedUserMessage(message: ProcessedMessage, pageDistance: PageDistance): ParsedUserMessage =
    ParsedUserMessage(message.user.id,
      message.message.messageId,
      pageDistance.page.name,
      s"https://facebook.com/${pageDistance.page.fb_id}",
      pageDistance.page.latitude,
      pageDistance.page.longitude,
      pageDistance.distanceKm)
}
