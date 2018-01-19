package model.dal

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

object PagesDal extends BaseDal {

  def findAll(): Future[Seq[Page]] = pagesTable.result

  def findById(id: Long): Future[Page] = pagesTable.filter(_.id === id).result.head

  def findByName(name: String): Future[Page] = pagesTable.filter(_.name === name).result.head

  def findByLocation(latitude: Float, longitude: Float): Future[Seq[Page]] =
    pagesTable.filter(pg => pg.longitude === longitude && pg.latitude === latitude).result


  def getByCity(cityName: String): Future[Seq[Page]] = pagesTable.filter(_.city === cityName).result

  def create(page: Page): Future[Long] = pagesTable returning pagesTable.map(_.id) += page

  def insert(pages:Seq[Page]):Future[Option[Int]] = pagesTable.delete >> (pagesTable ++= pages)

  def update(id: Long, newPage: Page): Future[Int] = {
    pagesTable.filter(_.id === id).map(page =>
      (page.name, page.city, page.latitude, page.longitude))
      .update((newPage.name, newPage.city, newPage.latitude,newPage.longitude))
  }

  def delete(id: Long): Future[Int] = {
    pagesTable.filter(_.id === id).delete
  }
}
