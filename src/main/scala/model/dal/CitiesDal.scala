package model.dal

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object CitiesDal extends BaseDal {

  def findAll(): Future[Seq[City]] = citiesTable.result

  def findById(id: Long): Future[City] = citiesTable.filter(_.id === id).result.head

  def findByName(name: String): Future[City] = citiesTable.filter(_.name === name).result.head

  def findByLocation(latitude: Float, longitude: Float): Future[City] =
    citiesTable.filter(pg => pg.longitude === longitude && pg.latitude === latitude).result.head

  def create(city: City): Future[Long] = citiesTable returning citiesTable.map(_.id) += city

  def insert(cities: Seq[City]):Future[Option[Int]] = {
    citiesTable.delete >> (citiesTable ++= cities)
  }

  def update(id: Long, newCity: City): Future[Int] = {
    citiesTable.filter(_.id === id).map(city =>
      (city.name, city.alternate_names, city.latitude, city.longitude))
      .update((newCity.name, newCity.alternate_names, newCity.latitude, newCity.longitude))
  }

  def delete(id: Long): Future[Int] = {
    citiesTable.filter(_.id === id).delete
  }
}
