package model.dal

import model.db.{CityTable, PagesTable}
import service.SlickDatabaseConfig
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.lifted.TableQuery
import slick.sql.{FixedSqlStreamingAction, SqlAction}

import scala.concurrent.Future

trait BaseDal extends SlickDatabaseConfig {
  lazy val pagesTable: TableQuery[PagesTable] = TableQuery[PagesTable]
  lazy val citiesTable: TableQuery[CityTable] = TableQuery[CityTable]

  protected implicit def executeFormDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }

  protected implicit def executeSeqFormDb[A](action: DBIOAction[Option[A], NoStream, Effect.Write with Effect.Write]): Future[Option[A]] = {
    db.run(action)
  }

  protected implicit def executeReadStreamFormDb[A](action: FixedSqlStreamingAction[Seq[A], A, _ <: slick.dbio.Effect]): Future[Seq[A]] = {
    db.run(action)
  }
}
