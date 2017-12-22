package model.db

import model._
import slick.jdbc.H2Profile.api._

class LocationsTable(tag: Tag) extends BaseTable[Location](tag, "location_info") {
//  def id= column[Long]("id", O.PrimaryKey, O.AutoInc)
  lazy implicit val pagesTableQ : TableQuery[PagesTable] = TableQuery[PagesTable]

//  def pageId = column[Long]("pageId")
  def city = column[String]("city")
  def country = column[String]("country")
  def latitude = column[String]("latitude")
  def longitude = column[String]("longitude")
  def street = column[String]("street")
  def zip = column[String]("zip")

  def * = (id, city, country, latitude, longitude, street, zip, createdAt) <> ((Location.apply _).tupled, Location.unapply)
}

//city, country, latitude, longitude, street, zip
