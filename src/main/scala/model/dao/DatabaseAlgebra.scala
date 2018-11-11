package model.dao

import fs2.Stream
import helpers.{Id, Num}
import model.dal.City


trait DatabaseAlgebra[F[_], A] {

  def findAll(): F[List[A]]

  def findById(id: Id): Stream[F, A]

  def findByName(name: String): F[Option[A]] // Stream[F, A]

  def findByLocation(latitude: Float, longitude: Float): F[Option[A]] //Stream[F, A]

  def getByCity(name: String): Stream[F, A]

  def create(entity: A): F[Num]

  def insert(entities:List[A]):F[Num]

  def update(id: Id, newEntity: A): F[Num]

  def delete(id: Id): F[Num]


}
