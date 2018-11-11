package utils

import java.util.Date
import java.util.concurrent.TimeUnit
import org.http4s.circe.CirceEntityDecoder._
import cats.effect.{IO, Timer}
import cats.syntax.all._
import org.http4s.Status.{NotFound, Successful, TooManyRequests}
import org.http4s.client.blaze._
import io.circe._
import org.http4s.Uri

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext

object RetrieveData {
  private lazy val httpClient = Http1Client[IO]().unsafeRunSync
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  def request[T](requestUri: String)(implicit decoder: Decoder[T]): IO[T] = {
    //    val httpRequest = HttpRequest(
    //      method = HttpMethods.GET,
    //      uri = requestUri,
    //      entity = HttpEntity(ContentType(MediaTypes.`application/json`), ""),
    //      headers = List(Authorization(OAuth2BearerToken(fbAccessToken)))
    //    )
    val uriFromStr = Uri.fromString(requestUri).toOption.get
    //    val req = GET(uriFromStr)//.withHeaders(Headers(Authorization())) //, headers = Headers(Header("X-Auth-Token", fbAccessToken))

    val resp = httpClient.get(uriFromStr) {
      case Successful(r) => r.as[T]
      case TooManyRequests(r) => //r.as[T]
        r.headers.find(_.name == "X-RateLimit-Reset").map(_.value.toLong * 1000) match {
          case None => IO.raiseError(new Exception(s"Number of retries exceeded: ${r.status} ${r.body}"))
          case Some(timestamp) =>
            val initialDelay = FiniteDuration(timestamp - new Date().getTime, TimeUnit.MILLISECONDS)
            IO.sleep(initialDelay) *> request[T](requestUri)
        }
      case NotFound(r) => IO.raiseError(new Exception(s"Not found: ${r.status}"))
      case r           => IO.raiseError(new Exception(s"Invalid response: ${r.status}"))
    }
    resp
  }
}
