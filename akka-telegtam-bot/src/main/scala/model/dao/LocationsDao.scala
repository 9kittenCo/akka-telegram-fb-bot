package model.dao

import model.Location
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

object LocationsDao extends BaseDao {
  def findAll(): Future[Seq[Location]] = locationsTable.result

  def findById(id: Long): Future[Location] = locationsTable.filter(_.id === id).result.head

//  def findByPageId(pageId: Long): Future[Location] = locationsTable.filter(_.pageId === pageId).result.head

  def findByLocation(latitude: String, longitude:String)(implicit ec:ExecutionContext): Future[Location] = locationsTable.filter(loc => loc.longitude === longitude && loc.latitude === latitude).result.head

  def findByCity(cityName:String): Future[Seq[Location]] = locationsTable.filter(_.city === cityName).result

  def create(location: Location): Future[Long] = locationsTable returning locationsTable.map(_.id) += location

  def update(id: Long, newLocation: Location): Future[Int] = {
    locationsTable.filter(_.id === id).map(location =>
      (location.city, location.country, location.latitude, location.longitude, location.street, location.zip))
      .update((newLocation.city, newLocation.country, newLocation.latitude, newLocation.longitude, newLocation.street, newLocation.zip))
  }

  def delete(id: Long): Future[Int] = {
    locationsTable.filter(_.id === id).delete
  }

}
