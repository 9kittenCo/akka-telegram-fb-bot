package client

import java.sql.Timestamp
import java.util.Date
import fs2.Stream
import cats.effect._
import cats.implicits._
import io.circe.generic.extras.auto._
import model._
import model.dal.{Page, PagesDal}
import model.dao.DoobieCityAlgebra
import service.Config
import utils.DistanceCalculation.getDistance
import utils.RetrieveData

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class FacebookClient[F[_] : Effect](cityClient: CityClient[F], dbClient: DoobieCityAlgebra[F]) extends BaseClient with Config {

  def updatePagesByCity(city: String): Future[Option[Int]] = {
    val pagesIO = getPagesInfoByCity(city).flatMap { pageInfos =>
      pageInfos.map(page => transformToPage(page)).sequence
    }
    pagesIO.unsafeToFuture.flatMap(pages => PagesDal.insert(pages))
  }

  def getPagesInfoByCity(city: String): IO[List[PageInfo]] = {
    findPagesByCity(city).flatMap { pages =>
      pages.toList.map { page =>
        getPageInfo(page.id)
      }.sequence
    }.flatMap { allpages =>
      IO {
        allpages.filter(_.location.isDefined)
      }
    }
  }

  def updatePage(pageInfo: PageInfo): Future[Long] = {
    val loc: PageLocation = pageInfo.location.get
    PagesDal.create(
      Page(
        pageInfo.id, pageInfo.name, pageInfo.phone, loc.city, loc.country, pageInfo.price_range,
        loc.longitude.get, loc.latitude.get, loc.street, loc.zip, new Timestamp(new Date().getTime)
      ))
  }

  def transformToPage(pageInfo: PageInfo): IO[Page] = {
    val loc: Option[PageLocation] = pageInfo.location
    IO(Page(
      pageInfo.id,
      pageInfo.name,
      pageInfo.phone,
      loc.get.city,
      loc.get.country,
      pageInfo.price_range,
      loc.get.latitude.getOrElse(0f),
      loc.get.longitude.getOrElse(0f),
      loc.get.street,
      loc.get.zip,
      new Timestamp(new Date().getTime)
    ))
  }

  def findPagesByCity(city: String): IO[Seq[SearchPagesInfo]] = {
    val response = RetrieveData.request[Response[SearchPagesInfo]](s"$fbServiceUrl/search?q=coworking+$city&type=page")
    response.flatMap { p =>
      p.paging.get.next match {
        case None => IO(p.data)
        case Some(nxt) => fetch(nxt, IO(p.data))
      }
    }
  }

  def getPageInfo(pageId: String): IO[PageInfo] = {
    RetrieveData.request[PageInfo](s"$fbServiceUrl/$pageId?fields=name,phone,location,hours,price_range")
  }

  def getPagesByLocation(latitude: Float, longitude: Float): F[List[PageDistance]] = {
    //todo need refactor to check is that city pages exists in db
    cityClient.getNearestCities(latitude, longitude) map { n_cities =>
      n_cities.map {
        n_city => dbClient.findByName(n_city._1).unsafeRunSync
      }
    } map { n_pages =>
      n_pages.flatten.map { n_page: Page =>
        PageDistance(n_page, getDistance(latitude, longitude, n_page.latitude, n_page.longitude))
      } sortBy (_.distanceKm)
    } map (data => data.take(3))
  }

  private def fetch(url: String, pages: IO[Seq[SearchPagesInfo]]): IO[Seq[SearchPagesInfo]] = {
    val response: IO[Response[SearchPagesInfo]] = RetrieveData.request[Response[SearchPagesInfo]](url)
    response flatMap { p =>
      p.paging.get.next match {
        case None => pages
        case Some(nxt) =>
          val result: IO[Seq[SearchPagesInfo]] = for {
            pa <- pages
          } yield pa ++ p.data
          fetch(nxt, result)
      }
    }
  }
}
