package client

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import helpers.Distance_km
import io.circe._
import model._
import service.Config

import scala.concurrent.Future

object TelegramClient extends BaseClient with Config with CirceDecoders {

  case class ParsedUserMessage(userId: String,
                               name: String,
                               ulr: String,
                               longitude: Float,
                               latitude: Float,
                               distance: Distance_km)

  //val msgsF: Future[Seq[Update]] = checkUpdates()

  //  lazy val usrmsges: Future[Seq[(User,Location)]] = for {
  //    msgs: List[Update] <- msgsF
  //    msg <- msgs.filter(_.message.isDefined).flatMap(_.message)
  //    user = msg.from
  //    loc = msg.location
  //  } yield (user.get, loc.get)

  //  lazy val usr_msgsF: Future[Seq[ParsedUserMessage]] = processMessages(usrmsges)
  //
  //  usr_msgsF.onComplete {
  //    case Success(usr_msgs) => usr_msgs.foreach { usr_msg =>
  //      sendMessage(SendMessage(usr_msg.userId, s"${usr_msg.name} - ${usr_msg.distance} km. \n ${usr_msg.ulr}").asJson, "sendMessage")
  //      sendMessage(SendLocation(usr_msg.userId, usr_msg.latitude, usr_msg.longitude).asJson, "sendLocation")
  //    }
  //    case Failure(f) => throw new Exception(f)
  //  }

  def checkUpdates(): Future[Seq[Update]] = {
    request[TelegramResponse[Seq[Update]]](s"$telegramUrl/getUpdates") flatMap  {
      case TelegramResponse(true, Some(result), _, _, _) => Future(result)
      case TelegramResponse(false, _, description, Some(errorCode), parameters) => Future.successful(Seq.empty)
      case _ => Future.failed(throw new Exception(""))
    }
  }

  //
  //  def processMessages(messagesF: Future[Seq[(User, Location)]])
  //  : Future[Seq[ParsedUserMessage]] = {
  //    for {
  //      msgs <- messagesF
  //      msg: (User, Location) <- msgs
  //      pglocs: Seq[PageDistance] <- getPagesByLocation(msg._2.latitude,
  //        msg._2.longitude)
  //      pgloc: PageDistance <- pglocs
  //    } yield {
  //      ParsedUserMessage(msg._1.id.toString,
  //        pgloc.page.name,
  //        s"https://facebook/$pgloc.page.fb_id)",
  //        pgloc.page.latitude,
  //        pgloc.page.longitude,
  //        pgloc.distance_km)
  //    }
  //  }
  //
  //  def sendMessage(message: Json, method: String): Future[Message] = {
  //
  //    val uri = s"$telegramUrl/$method"
  //    val body = RequestBuilding.Post(Uri(uri), content = message)
  //    for {
  //      response <- Http().singleRequest(body)
  //      decoded <- Unmarshal(response.entity).to[Message]
  //    } yield {
  //      decoded
  //    }
  //  }

  def request[T](requestUri: String)(
    implicit decoder: Decoder[T]): Future[T] = {
    val httpRequest = HttpRequest(uri = requestUri)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    responseFuture flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(
            response.entity.withContentType(ContentTypes.`application/json`))
            .to[T]
        case _ =>
          Future.failed(new Exception(s"Invalid response: ${response.status}"))
      }
    }
  }
}
