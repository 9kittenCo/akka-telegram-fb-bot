package client

import java.sql.Timestamp
import java.util.Date

import client.CityClient._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import helpers.Distance_km
import io.circe.generic.extras.auto._
import model._
import model.dal.{Page, PagesDal}
import service.Config
import utils.DistanceCalculation.getDistance
import utils.RetrieveData

import scala.concurrent.Future

object FacebookClient extends BaseClient with Config with FailFastCirceSupport {

  case class PageDistance(page:Page, distance_km: Distance_km)

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
      loc.get.latitude.getOrElse(0f),
      loc.get.longitude.getOrElse(0f),
      loc.get.street,
      loc.get.zip,
      new Timestamp(new Date().getTime)))
  }

  def findPagesByCity(city: String): Future[Seq[SearchPagesInfo]] = {
    val response = RetrieveData.request[Response[SearchPagesInfo]](s"$fbServiceUrl/search?q=coworking+$city&type=page")
    response flatMap { p =>
      p.paging.get.next match {
        case None => Future(p.data)
        case Some(nxt) => fetch(nxt, Future(p.data))
      }
    }
  }

  def getPageInfo(pageId: String): Future[PageInfo] = {
    RetrieveData.request[PageInfo](s"$fbServiceUrl/$pageId?fields=name,phone,location,hours,price_range")
  }

  def getPagesByLocation(latitude: Float, longitude: Float): Future[List[PageDistance]] = {
    //todo need refactor to check is that city pages exsist in db
    getNearestCities(latitude, longitude) flatMap { n_cities =>
      Future.sequence {
        n_cities map (n_city => PagesDal.getByCity(n_city._1))
      }
    } map { n_pages =>
      n_pages.flatten.map { n_page: Page =>
        PageDistance(n_page, getDistance(latitude, longitude, n_page.latitude, n_page.longitude))
      } sortBy (_.distance_km)
    } map (data => data.take(3))
  }

  private def fetch(url: String, pages: Future[Seq[SearchPagesInfo]]): Future[Seq[SearchPagesInfo]] = {
    val response: Future[Response[SearchPagesInfo]] = RetrieveData.request[Response[SearchPagesInfo]](url)
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
}
