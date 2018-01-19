package model.db

import java.sql.Timestamp

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def createdAt = column[Timestamp]("created_at")
}

