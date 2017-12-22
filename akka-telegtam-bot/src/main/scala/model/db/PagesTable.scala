package model.db

import model.Page
import slick.jdbc.PostgresProfile.api._

class PagesTable(tag: Tag) extends BaseTable[Page](tag, "page_info") {
  lazy implicit val locationsTableQ : TableQuery[LocationsTable] = TableQuery[LocationsTable]

  def name = column[String]("name")
  def locationId = column[Long]("location_id")
  def phone = column[String]("phone")
  def price_range = column[String]("price_range")

  def * = (id, name, locationId.?, phone, price_range, createdAt) <> ((Page.apply _).tupled, Page.unapply)

  def location = foreignKey("fk_location_id",
    locationId,
    locationsTableQ)(_.id)

}
