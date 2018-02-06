package utils

import java.util.Date
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import client.BaseClient
import client.FacebookClient.fbAccessToken
import io.circe._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}

object RetrieveData extends BaseClient {
  def request[T: FromResponseUnmarshaller](requestUri: String)(implicit decoder: Decoder[T]): Future[T] = {
    val httpRequest = HttpRequest(
      method = HttpMethods.GET,
      uri = requestUri,
      entity = HttpEntity(ContentType(MediaTypes.`application/json`), ""),
      headers = List(Authorization(OAuth2BearerToken(fbAccessToken)))
    )

    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    responseFuture flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity.withContentType(ContentTypes.`application/json`)).to[T]
        case StatusCodes.TooManyRequests =>
          response.headers.find(_.name == "X-RateLimit-Reset").map(_.value.toLong * 1000) match {
            case None => Future.failed(new Exception(s"Number of retries exceeded: ${response.status} ${response.entity}"))
            case Some(timestamp) =>
              delay(FiniteDuration(timestamp - new Date().getTime, TimeUnit.MILLISECONDS)) {
                request[T](requestUri)
              }
          }
        case _ => Future.failed(new Exception(s"Invalid response: ${response.status}"))
      }
    }
  }

  def delay[T](delay: FiniteDuration)(action: => Future[T]): Future[T] = {
    val promise = Promise[T]()

    system.scheduler.scheduleOnce(delay) {
      promise.completeWith(action)
    }(system.dispatcher)

    promise.future
  }

}
