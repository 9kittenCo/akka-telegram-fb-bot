package api

import helpers._
import model.TelegramApiModels._

trait TelegramApiAlgebra[F[_]] {
  def getWebhookInfo: F[String]

  def setWebhook(url: String): F[_]

  def deleteWebhook(): F[_]

  def sendMessage(chatId: Id, message: String): F[TelegramResponse[TgMessage]]

  def getUpdates(offset: Long): F[TelegramResponse[Seq[Update]]]
}
