package model.dao

import cats.effect.Async
import doobie.util.transactor.Transactor
import model.dal.City
import fs2.Stream
import doobie.implicits._
import doobie.util.update.Update
import cats.instances.list._
import helpers.Num

class DoobieCityAlgebra[F[_] : Async](xa: Transactor[F]) extends DatabaseAlgebra[F, City] {

  val y = xa.yolo
  import y._

  override def findAll(): F[List[City]] = sql"SELECT * FROM city_location_catalog"
    .query[City]
    .to[List]
    .transact(xa)

  override def findById(id: Long): Stream[F, City] = sql"SELECT * FROM city_location_catalog where id = $id"
    .query[City]
    .stream
    .transact(xa)

  override def findByName(name: String): F[Option[City]] = sql"SELECT * FROM city_location_catalog where name = $name"
    .query[City]
    .option
    .transact(xa)

  override def findByLocation(latitude: Float, longitude: Float): F[Option[City]] =
    sql"SELECT * FROM city_location_catalog where latitude = $latitude and longitude = $longitude"
    .query[City]
    .option
    .transact(xa)

  override def create(city: City): F[Num] = sql"insert into city_location_catalog (name, alternate_names, latitude, longitude) values (${city.name}, ${city.alternate_names}, ${city.latitude}, ${city.longitude})"
    .update
    .run
    .transact(xa)

  override def insert(cities: List[City]): F[Num] = {
    val sql = s"insert into city_location_catalog (`name`, alternate_names, latitude, longitude) values (?,?,?,?)"
    Update[City](sql)
      .updateMany(cities)
      .transact(xa)

  }

  override def update(id: Long, newCity: City): F[Num] = sql"update city_location_catalog set (name, alternate_names, latitude, longitude) = (${newCity.name}, ${newCity.alternate_names}, ${newCity.latitude}, ${newCity.longitude}) where id = $id"
    .update
    .run.transact(xa)

  override def delete(id: Long): F[Num] = sql"delete from city_location_catalog where id = $id"
    .update
    .run
    .transact(xa)

  override def getByCity(name: String): Stream[F, City] = findByName(name)
}
