package model.dao

import java.sql.Timestamp
import java.util.Date

import cats.effect.Async
import doobie.util.transactor.Transactor
import fs2.Stream
import helpers.{Id, Num}
import model.dal.{City, Page}
import doobie.implicits._
import cats.instances.list._
import doobie.util.update.Update

class DoobiePagesAlgebra[F[_]: Async](xa: Transactor[F]) extends DatabaseAlgebra[F, Page] {

  override def findAll(): F[List[Page]] = sql"SELECT * FROM page_info"
    .query[Page]
    .to[List]
    .transact(xa)

  override def findById(id: Long): Stream[F, Page] = sql"SELECT * FROM page_info where id = $id"
    .query[Page]
    .stream
    .transact(xa)

  override def findByName(name: String): Stream[F, Page] = sql"SELECT * FROM page_info where name = $name"
    .query[Page]
    .stream
    .transact(xa)

  override def findByLocation(latitude: Float, longitude: Float): Stream[F, Page] =
    sql"SELECT * FROM page_info where latitude = $latitude and longitude = $longitude"
      .query[Page]
      .stream
      .transact(xa)

  override def getByCity(name: String): Stream[F, Page] = sql"SELECT * FROM page_info WHERE city = $name"
    .query[Page]
    .stream
    .transact(xa)

  override def create(entity: Page): F[Num] = sql"insert into page_info (fb_id, name, phone, city, country, price_range, latitude, longitude, street, zip, created_at) values (${entity.fb_id}, ${entity.name}, ${entity.phone.getOrElse("")}, ${entity.city}, ${entity.country.getOrElse("")}, ${entity.price_range}, ${entity.latitude}, ${entity.longitude}, ${entity.street}, ${entity.zip}, ${new Timestamp(new Date().getTime)})"
    .update
    .run
    .transact(xa)

  override def insert(entities: List[Page]): F[Num] = {
    val sql = s"insert into page_info (fb_id, name, phone, city, country, price_range, latitude, longitude, street, zip, created_at) values (?,?,?,?,?,?,?,?,?,?,?)"
    Update[Page](sql)
      .updateMany(entities)
      .transact(xa)

  }

  override def update(id: Long, newEntity: Page): F[Num] = sql"update page_info set (fb_id, name, phone, city, country, price_range, latitude, longitude, street, zip, created_at) = (${newEntity.fb_id}, ${newEntity.name}, ${newEntity.phone.getOrElse("")}, ${newEntity.city}, ${newEntity.country.getOrElse("")}, ${newEntity.price_range}, ${newEntity.latitude}, ${newEntity.longitude}, ${newEntity.street}, ${newEntity.zip}, ${new Timestamp(new Date().getTime)}) where id = $id"
    .update
    .run.transact(xa)

  override def delete(id: Long): F[Num] = sql"delete from city_location_catalog where id = $id"
    .update
    .run
    .transact(xa)
}
