package model.db

import model.dal.Page
import slick.jdbc.PostgresProfile.api._

class PagesTable(tag: Tag) extends BaseTable[Page](tag, "page_info") {
  lazy implicit val locationsTableQ : TableQuery[LocationsTable] = TableQuery[LocationsTable]

  def name = column[String]("name")
  def locationId = column[Long]("location_id")
  def locationFbId = column[String]("location_fb_id")
  def phone = column[String]("phone")
  def price_range = column[String]("price_range")

  def * = (name, locationId.?, locationFbId.?, phone, price_range.?) <> ((Page.apply _).tupled, Page.unapply)

  def location = foreignKey("fk_location_id",
    locationId,
    locationsTableQ)(_.id)

}
