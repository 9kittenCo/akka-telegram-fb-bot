package model.dal

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

object PagesDal extends BaseDal {

  def findAll(): Future[Seq[Page]] = pagesTable.result

  def findById(id: Long): Future[Page] = pagesTable.filter(_.id === id).result.head

  def findByName(name: String): Future[Page] = pagesTable.filter(_.name === name).result.head

  def findByLocation(latitude: String, longitude: String)(implicit ec: ExecutionContext): Future[Seq[Page]] =
    LocationsDal.findByLocation(latitude, longitude) flatMap (loc => pagesTable.filter(_.locationId === loc.id).result)

  def findByCity(cityName: String)(implicit ec: ExecutionContext): Future[Seq[Page]] = {

    val findPagesQuery = locationsTable.filter(_.city === cityName)
      .join(pagesTable).on(_.id === _.locationId)
      .groupBy(_._2)
      .result

    val action = for {
      pagesResult <- findPagesQuery
    } yield {
      pagesResult map { row => row._1
      }
    }

    db.run(action)
  }

  def create(coworking: Page): Future[Long] = pagesTable returning pagesTable.map(_.id) += coworking

  def update(id: Long, newPage: Page): Future[Int] = {
    pagesTable.filter(_.id === id).map(page => (page.name, page.phone))
      .update((newPage.name, newPage.phone))
  }

  def delete(id: Long): Future[Int] = {
    pagesTable.filter(_.id === id).delete
  }
}
