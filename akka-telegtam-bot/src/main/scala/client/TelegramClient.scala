package client

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import api.TelegramApiException
import client.FacebookClient._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import helpers.CityName
import io.circe.Decoder
import io.circe.generic.extras.auto._
import model.dal.Page
import model.{Message, TelegramResponse, Update, User}
import service.Config

import scala.concurrent.Future

object TelegramClient extends Config with BaseClient with FailFastCirceSupport {

  val msgs: Future[List[(User, Future[List[(CityName, Page, Int)]])]] = checkUpdates() map { upds: List[Update] =>
    upds.filter(_.message.isDefined == true) flatMap (_.message)
  } map { msgs: List[Message] =>
    msgs.filter(_.location.isDefined) map { msg =>
      val loc = msg.location.get
      (msg.from.get, getPagesByLocation(loc.latitude, loc.longitude))
    }
  }


  def checkUpdates(): Future[List[Update]] = {
    request[TelegramResponse[List[Update]]](s"$telegramUrl/getUpdates") flatMap {
      case TelegramResponse(true, Some(result), _, _, _) => Future.successful(result)
      case TelegramResponse(false, _, description, Some(errorCode), parameters) =>
        val e = TelegramApiException(description.getOrElse("Unexpected/invalid/empty response"), errorCode, None, parameters)
        Future.failed(e)
      case _ =>
        val msg = "Error on request response"
        Future.failed(new Exception(msg))
    }
  }

  def request[T](requestUri: String)(implicit decoder: Decoder[T]): Future[T] = {
    val httpRequest = HttpRequest(uri = requestUri)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    responseFuture flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity.withContentType(ContentTypes.`application/json`)).to[T]
        case _ => Future.failed(new Exception(s"Invalid response: ${response.status}"))
      }
    }
  }
}