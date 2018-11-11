package service

import doobie.util.transactor.Transactor
import cats.effect._

class DoobieDatabaseConfig[F[_] : Effect]()(implicit E:Effect[F]) extends Config {

  val xa: Transactor[F] = Transactor.fromDriverManager[F](
    driver = "org.postgresql.Driver",  url = databaseUrl, user = databaseUser, pass = databasePassword
  )
}
