package client

import java.sql.Timestamp
import java.util.Date
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import io.circe._
import io.circe.generic.extras.auto._
import model._
import model.dal.{Page, PagesDal}
import service.Config

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}

object FacebookClient extends Config with FacebookGraphApi with BaseClient {

  def updatePagesByCity(city: String): Future[Option[Int]] = {
    getPagesInfoByCity(city) flatMap { pageInfos =>
      Future.sequence(pageInfos map (page => transformToPage(page)))
    } flatMap (pages => PagesDal.insert(pages))
  }

  def getPagesInfoByCity(city: String): Future[Seq[PageInfo]] = {
    findPagesByCity(city) flatMap { pages =>
      Future.sequence {
        pages map (page => getPageInfo(page.id))
      }
    } flatMap { allpages =>
      Future(allpages.filter(_.location.isDefined))
    }
  }

  def updatePage(pageInfo: PageInfo): Future[Long] = {
    val loc: PageLocation = pageInfo.location.get
    PagesDal.create(Page(pageInfo.id,
      pageInfo.name,
      pageInfo.phone,
      loc.city,
      loc.country,
      pageInfo.price_range,
      loc.longitude.get,
      loc.latitude.get,
      loc.street,
      loc.zip,
      new Timestamp(new Date().getTime)))
  }

  def transformToPage(pageInfo: PageInfo): Future[Page] = {
    val loc: Option[PageLocation] = pageInfo.location
    Future(Page(pageInfo.id,
      pageInfo.name,
      pageInfo.phone,
      loc.get.city,
      loc.get.country,
      pageInfo.price_range,
      loc.get.longitude.getOrElse(0f),
      loc.get.latitude.getOrElse(0f),
      loc.get.street,
      loc.get.zip,
      new Timestamp(new Date().getTime)))
  }

  override def findPagesByCity(city: String): Future[Seq[SearchPagesInfo]] = {
    val response = request[Response[SearchPagesInfo]](s"$fbServiceUrl/search?q=coworking+$city&type=page") //map (_.data)
    response flatMap { p =>
      p.paging.get.next match {
        case None => Future(p.data)
        case Some(nxt) => fetch(nxt, Future(p.data))
      }
    }
  }

  def getPageInfo(pageId: String): Future[PageInfo] = {
    request[PageInfo](s"$fbServiceUrl/$pageId?fields=name,phone,location,hours,price_range")
  }

  override def getPagesByLocation(latitude: Float, longitude: Float) = ??? //{
  //      val city: String = getNearestCity(latitude, longitude)
  //      val ll = getPagesInfoByCity(city) flatMap { pages =>
  //  //      Future.sequence {
  //          pages map {page: PageInfo =>
  //            page.location match {
  //              case Some(loc) => (page, getCoordinatesDistance(loc.latitude,loc.longitude, latitude, longitude))
  //              case None => (page,None)
  //            }
  //          }
  //  //      }
  //      }
  //    }

  def getCoordinatesDistance(latitude1: Float, longitude1: Float, latitude2: Float, longitude2: Float): Option[Long] = ???

  def getNearestCity(latitude: Float, longitude: Float): String = ???

  private def fetch(url: String, pages: Future[Seq[SearchPagesInfo]]): Future[Seq[SearchPagesInfo]] = {
    val response: Future[Response[SearchPagesInfo]] = request[Response[SearchPagesInfo]](url)
    response flatMap { p =>
      p.paging.get.next match {
        case None => pages
        case Some(nxt) =>
          val result: Future[Seq[SearchPagesInfo]] = for {
            pa <- pages
          } yield pa ++ p.data
          fetch(nxt, result)
      }
    }
  }

  private[this] def request[T: FromResponseUnmarshaller](requestUri: String)(implicit decoder: Decoder[T]): Future[T] = {
    val httpRequest = HttpRequest(
      method = HttpMethods.GET,
      uri = requestUri,
      entity = HttpEntity(ContentType(MediaTypes.`application/json`), ""),
      headers = List(Authorization(OAuth2BearerToken(fbAccessToken))))

    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    responseFuture flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity.withContentType(ContentTypes.`application/json`)).to[T]
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
