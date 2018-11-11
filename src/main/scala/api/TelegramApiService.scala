package api

import client.BaseClient
import model.TelegramApiModels._
import service.Config
import cats.effect._
import helpers._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s._
import io.circe.generic.auto._
import org.http4s.dsl.io._

class TelegramApiService[F[_]: Effect](client: Client[F]) extends TelegramApiAlgebra[F] with Http4sClientDsl[F] with BaseClient with Config {

  private implicit val messageDecoder: EntityDecoder[F, TelegramResponse[TgMessage]]   = jsonOf[F, TelegramResponse[TgMessage]]
  private implicit val updateDecoder: EntityDecoder[F, TelegramResponse[Seq[Update]]]  = jsonOf[F, TelegramResponse[Seq[Update]]]

  def getWebhookInfo: F[String] = {
    val uri = telegramUrl / "getWebhookInfo"
    client.expect[String](uri)
  }

  def setWebhook(url: String): F[_] = {
    val uri = telegramUrl / "setWebhook" =? Map("url" -> Seq(url))
    client.expect[String](uri)
  }

  def deleteWebhook(): F[_] = {
    val uri = telegramUrl / "deleteWebhook"
    client.expect[String](uri)
  }

  def sendMessage(chatId: Id, message: String): F[TelegramResponse[TgMessage]] = {
    val uri = telegramUrl / "sendMessage" =?
      Map("chat_id" -> Seq(chatId.toString),
        "text" -> Seq(message))
    client.expect[TelegramResponse[TgMessage]](uri)
  }

  def sendLocation(chatId: Id, lat: Double, lon: Double): F[TelegramResponse[TgMessage]] = {
    val uri = telegramUrl / "sendLocation" =?
      Map("chat_id" -> Seq(chatId.toString),
        "latitude" -> Seq(lat.toString),
        "longitude" -> Seq(lon.toString)
      )
    client.expect[TelegramResponse[TgMessage]](uri)
  }

  def getUpdates(offset: Long): F[TelegramResponse[Seq[Update]]] = {
    val uri = telegramUrl / "getUpdates" =? Map(
      "offset" -> Seq(offset.toString),
      "allowed_updates" -> Seq("""["message"]""")
    )
    client.expect[TelegramResponse[Seq[Update]]](uri)
  }
}
