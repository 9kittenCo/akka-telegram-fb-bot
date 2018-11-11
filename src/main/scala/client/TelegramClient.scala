//package client
//
//import bot.ParsedUserMessage
//import cats.implicits._
//import cats.effect.{Effect, IO}
//import client.FacebookClient._
//import fs2.Stream
//import model._
//import helpers.{DistanceKm, MessageId}
//import org.http4s._
//import org.http4s.circe._
//import service.Config
//import org.http4s.dsl.Http4sDsl
//import io.circe.Json
//import io.circe.generic.auto._
//import org.http4s.client.Client
//
//
//class TelegramClient[F[_]: Effect](client: Client[F]) extends Http4sDsl[F] with BaseClient with Config with CirceDecoders with CirceEncoders {
//
//  private implicit val messageDecoder: EntityDecoder[F, TgMessage]                      = jsonOf[F, TgMessage]
//  private implicit val listUpdateDecoder: EntityDecoder[F, List[Update]]                = jsonOf[F, List[Update]]
//  private implicit val listUpdateEncoder: EntityEncoder[F, List[Update]]                = jsonEncoderOf[F, List[Update]]
//  private implicit val updateDecoder: EntityDecoder[F, TelegramResponse[List[Update]]]  = jsonOf[F, TelegramResponse[List[Update]]]
//
//  val service: HttpService[F] = HttpService[F] {
//    case GET -> Root => checkUpdates(0).flatMap(Ok(_))
//  }
//
//  def getUserMessages: F[List[(User, MessageId, Location)]] = {
//    getUpdates(0) map { updates =>
//      updates.filter(_.message.isDefined) map { update =>
//        val mssg = update.message.get
//        (mssg.from.get, mssg.message_id, mssg.location)
//      } filter (_._3.isDefined) map { usrloc =>
//        (usrloc._1, usrloc._2, usrloc._3.get)
//      }
//    }
//  }
//
//  def processMessages(): Unit = {
//    val usr_msgsF: F[List[ParsedUserMessage]] = parseUserMessages(getUserMessages)//.unsafeToFuture
//
////    usr_msgsF.onComplete {
////      case Success(usr_msgs) =>
////        usr_msgs.sortBy(_.distance).foreach { usr_msg =>
////          val text = s"${usr_msg.name} - ${usr_msg.distance} m. \n ${usr_msg.url}"
////          sendMessage[TgMessage](SendMessage(usr_msg.userId, text, Some("Markdown"), Some(false), None, Some(usr_msg.messageId.toLong)).asJson, "sendMessage").unsafeRunTimed(500.millis)
////          //Thread.sleep(500)
////          sendMessage[TgMessage](SendLocation(usr_msg.userId, usr_msg.longitude, usr_msg.latitude, None, Some(usr_msg.messageId.toLong)).asJson, "sendLocation").unsafeRunTimed(500.millis)
////          //for right order
////          //Thread.sleep(500)
////
////        }
////      case Failure(f) => throw new Exception(f)
////    }
//  }
//
//  def checkUpdates(offset: Long): F[List[Update]] = {
//    getUpdates(offset)
//  }
//
//  def getUpdates(offset: Long): F[List[Update]] = {
//    val uri = telegramUrl / "getUpdates" =? Map(
//      "offset" -> Seq(offset.toString),
//      "allowed_updates" -> Seq("""["message"]""")
//    )
//    client.expect[TelegramResponse[List[Update]]](uri).map(_.result)
//  }
//
//  def parseUserMessages(messagesF: F[List[(User, MessageId, Location)]]): F[List[ParsedUserMessage]] = {
//    for {
//      msgs <- messagesF
//      msg <- msgs
//      pgs <- getPagesByLocation(msg._3.latitude, msg._3.longitude)
//      pg <- pgs
//      url = s"https://facebook.com/${pg.page.fb_id}"
//    } yield ParsedUserMessage(msg._1.id, msg._2, pg.page.name, url, pg.page.latitude, pg.page.longitude, pg.distanceKm)
//  }
//
////  def sendMessage[T](message: Json, method: String): F[T] = {
////
////    val uriName = telegramUrl / s"$method"
////    //val req = POST(uriName, message)
////    client.expect[T](uriName)
////  }
//}
