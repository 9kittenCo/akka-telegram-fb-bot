package model.db

import model.dal.Page
import slick.jdbc.PostgresProfile.api._

class PagesTable(tag: Tag) extends BaseTable[Page](tag, "page_info") {
  val fb_id = column[String]("fb_id")
  val name = column[String]("name")
  val phone = column[String]("phone")
  val price_range = column[String]("price_range")
  val city = column[String]("city")
  val country = column[String]("country")
  val latitude = column[Float]("latitude")
  val longitude = column[Float]("longitude")
  val street = column[String]("street")
  val zip = column[String]("zip")

  val * = (fb_id, name, phone.?, city, country.?, price_range.?, latitude, longitude, street.?, zip.?, createdAt) <> ((Page.apply _).tupled, Page.unapply)

}
