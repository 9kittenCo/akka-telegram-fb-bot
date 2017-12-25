package client

import java.util.Date
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import akka.stream.{ActorMaterializer, Materializer}
import model.FacebookGraphApiJsonProtocol._
import model.{FacebookGraphApi, Location}
import service.Config

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future, Promise}

object FacebookClient extends App with Config with FacebookGraphApi {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //TODO add city choose funtionality
  val city = "kyiv"

  val pagesByCity: Future[Seq[SearchPagesInfo]] = findPagesByCity(city)

  pagesByCity foreach println

  override def findPagesByCity(city: String): Future[Seq[SearchPagesInfo]] = {
    request[SearchPagesInfo](s"search?q=coworking+$city&type=page") map(_.data)
  }

  def getPageInfo(pageId: String) = ???
  //:Future[Response[PageInfo]] = {
//    request[T](s"$pageId?fields=name,phone,location,hours,price_range")
//  }

  def getPagesByLocation(location: Location)= ???

  private[this] def request[T: FromResponseUnmarshaller](requestUri: String)(implicit ec: ExecutionContext, mat: Materializer): Future[Response[T]] = {
    val httpRequest = HttpRequest(
      method = HttpMethods.GET,
      uri = s"$fbServiceUrl/$requestUri",
      entity = HttpEntity(ContentType(MediaTypes.`application/json`), ""),
      headers = List(Authorization(OAuth2BearerToken(fbAccessToken))))

    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    responseFuture flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity.withContentType(ContentTypes.`application/json`)).to[Response[T]] //map(_.data)
        case StatusCodes.TooManyRequests => response.headers.find(_.name == "X-RateLimit-Reset").map(_.value.toLong * 1000) match {
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

  private[this] def delay[T](delay: FiniteDuration)(action: => Future[T]) = {
    val promise = Promise[T]()

    system.scheduler.scheduleOnce(delay) {
      promise.completeWith(action)
    }(system.dispatcher)

    promise.future
  }
    //    val httpResponse: HttpResponse = Await.result(responseFuture, Duration.Inf)
    //
    //    val resp: Option[Response[SearchPagesInfo]] = if (httpResponse.status.isSuccess()) {
    //      Some(Await.result(Unmarshal(httpResponse.entity.withContentType(ContentTypes.`application/json`)).to[Response[SearchPagesInfo]], Duration.Inf))
    //    } else {
    //      None
    //    }
//    responseFuture.flatMap(resp =>
//      resp.status match {
//        case status if status.isSuccess() => Unmarshal(resp.entity).to[Response[SearchPagesInfo]]
//        case error                        => Future.failed(sys.error(s"GET $requestUri: ${error.intValue()}"))
//      })
    //flatMap {resp: HttpResponse => Unmarshal(resp.entity.withContentType(ContentTypes.`application/json`)).to[T]}

  }
