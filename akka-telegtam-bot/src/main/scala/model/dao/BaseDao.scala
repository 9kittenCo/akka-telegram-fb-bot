package model.dao

import model.db.{PagesTable, LocationsTable}
import service.DatabaseConfig
import slick.dbio.NoStream
import slick.lifted.TableQuery
import slick.sql.{FixedSqlStreamingAction, SqlAction}

import scala.concurrent.Future

trait BaseDao extends DatabaseConfig {
  lazy val pagesTable: TableQuery[PagesTable] = TableQuery[PagesTable]
  lazy val locationsTable: TableQuery[LocationsTable] = TableQuery[LocationsTable]

  protected implicit def executeFormDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }

  protected implicit def executeReadStreamFormDb[A](action: FixedSqlStreamingAction[Seq[A], A, _ <: slick.dbio.Effect]): Future[Seq[A]] = {
    db.run(action)
  }
}
