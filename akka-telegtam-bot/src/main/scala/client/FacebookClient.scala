package client

import java.util.Date
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import akka.stream.ActorMaterializer
import io.circe._
import io.circe.generic.auto._
import model._
import model.dal.Location
import service.Config

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContextExecutor, Future, Promise}

object FacebookClient extends Config with FacebookGraphApi {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //TODO add city choose funtionality
//  val city = "kyiv"

//  val pagesInfoByCity: Future[Seq[PageInfo]] = getPagesInfoByCity(city)

//  val locations: Future[Seq[Object]] = pagesInfoByCity map { pages: Seq[PageInfo] =>
//      pages.map(p => p.location_id match {
//      case Some(loc) => Location(loc.id,loc.city,loc.country,loc.latitude,loc.longitude,loc.street,loc.zip)
//      case None => _
//      })
//      }

//  pagesInfoByCity foreach println

  def getPagesInfoByCity(city: String):Future[Seq[PageInfo]] = {
    val pages: Future[Seq[PageInfo]] = findPagesByCity(city) flatMap { pages =>
      Future.sequence {
        pages map (page => getPageInfo(page.id))
      }
    } //map(pagesInfo => pagesInfo.flatten.groupBy(_.))
    pages
  }

  override def findPagesByCity(city: String): Future[Seq[SearchPagesInfo]] = {
    request[Response[SearchPagesInfo]](s"search?q=coworking+$city&type=page") map (_.data)
  }

  def getPageInfo(pageId: String): Future[PageInfo] = {
    request[PageInfo](s"$pageId?fields=name,phone,location,hours,price_range")
  }

  def getPagesByLocation(location: Location) = ???

  private[this] def request[T: FromResponseUnmarshaller](requestUri: String)(implicit decoder: Decoder[T]): Future[T] = {
    val httpRequest = HttpRequest(
      method = HttpMethods.GET,
      uri = s"$fbServiceUrl/$requestUri",
      entity = HttpEntity(ContentType(MediaTypes.`application/json`), ""),
      headers = List(Authorization(OAuth2BearerToken(fbAccessToken))))

    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    responseFuture flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity.withContentType(ContentTypes.`application/json`)).to[T] //map(_.data)
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
}
