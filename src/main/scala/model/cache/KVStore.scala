package model.cache

import cats.effect.Effect

import scala.collection.mutable
import scala.language.higherKinds

trait KVStore[F[_], K, V] {

  def put(key: K, value: V): F[Unit]

  def get(key: K): F[Option[V]]

  def getAll(): F[List[V]]
}

object KVStore {
  def inMemoryCache[F[_], K, V](lifetime: Long = 5 * 60 * 1000)(
    implicit E: Effect[F]): KVStore[F, K, V] = new KVStore[F, K, V] {

    case class Value[KK](value: KK, createdAt: Long)

    private val cache = mutable.OpenHashMap[K, Value[V]]()

    override def put(key: K, value: V): F[Unit] =
      E.pure {
        cache.put(key, Value(value, System.currentTimeMillis()))
        ()
      }

    override def get(key: K): F[Option[V]] = E.pure {
      cache.get(key).flatMap {
        case Value(value, createdAt)
          if createdAt + lifetime >= System.currentTimeMillis() =>
          Some(value)
        case _ => None
      }

    }

    override def getAll(): F[List[V]] = E.pure {
      cache.values.map(_.value).toList
    }
  }
}
