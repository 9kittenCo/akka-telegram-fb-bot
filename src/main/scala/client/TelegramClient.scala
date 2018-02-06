package client

import akka.http.scaladsl.Http
import akka.http.scaladsl.client._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.implicits._
import client.FacebookClient._
import helpers.{DistanceKm, MessageId}
import io.circe.syntax._
import io.circe.{Decoder, Json}
import model._
import service.Config

import scala.concurrent.Future
import scala.util.{Failure, Success}

object TelegramClient extends BaseClient with Config with CirceDecoders with CirceEncoders {

  case class ParsedUserMessage(userId: Int, messageId: Int, name: String, url: String, longitude: Float, latitude: Float, distance: DistanceKm)

  def getUserMessages: Future[List[(User, MessageId, Location)]] = {
    checkUpdates() map { updates =>
      updates.filter(_.message.isDefined) map { update =>
        val mssg = update.message.get
        (mssg.from.get, mssg.message_id, mssg.location)
      } filter (_._3.isDefined) map { usrloc =>
        (usrloc._1, usrloc._2, usrloc._3.get)
      }
    }
  }

  def processMessages(): Unit = {
    val usr_msgsF = parseUserMessages(getUserMessages)

    usr_msgsF.onComplete {
      case Success(usr_msgs) =>
        usr_msgs.sortBy(_.distance).foreach { usr_msg =>
          val text = s"${usr_msg.name} - ${usr_msg.distance} m. \n ${usr_msg.url}"
          sendMessage[Message](SendMessage(usr_msg.userId, text, Some("Markdown"), Some(false), None, Some(usr_msg.messageId.toLong)).asJson, "sendMessage")
          Thread.sleep(500)
          sendMessage[Message](SendLocation(usr_msg.userId, usr_msg.longitude, usr_msg.latitude, None, Some(usr_msg.messageId.toLong)).asJson, "sendLocation")
          //for right order
          Thread.sleep(500)

        }
      case Failure(f) => throw new Exception(f)
    }
  }

  def checkUpdates(): Future[List[Update]] = {
    sendMessage[TelegramResponse[List[Update]]](GetUpdates(Some(1)).asJson, "getUpdates") map {
      case TelegramResponse(true, Some(result), _, _) => result
      case TelegramResponse(false, None, _, _)        => scala.collection.immutable.List.empty[Update]
      case _                                          => scala.collection.immutable.List.empty[Update]
    }

  }

  def parseUserMessages(messagesF: Future[List[(User, MessageId, Location)]]): Future[List[ParsedUserMessage]] = {
    val pglocsF = messagesF flatMap { msgs =>
      Future.sequence {
        msgs map { msg =>
          getPagesByLocation(msg._3.latitude, msg._3.longitude) map { ms =>
            (msg._1, msg._2, ms)
          }
        }
      }
    }

    val parsedUserMessagesF = pglocsF map { tupls =>
      tupls
        .flatMap { upls =>
          upls._3.map { pgloc =>
            val url = s"https://facebook.com/${pgloc.page.fb_id}"
            ParsedUserMessage(upls._1.id, upls._2, pgloc.page.name, url, pgloc.page.latitude, pgloc.page.longitude, pgloc.distanceKm)
          }
        }
    }
    parsedUserMessagesF
  }

  def sendMessage[T: Manifest](message: Json, method: String)(implicit decoder: Decoder[T]): Future[T] = {

    val uri = s"$telegramUrl/$method"
    val body = RequestBuilding.Post(Uri(uri), content = message)
    for {
      response <- Http().singleRequest(body)
      decoded <- Unmarshal(response.entity).to[T]
    } yield {
      decoded
    }
  }
}
