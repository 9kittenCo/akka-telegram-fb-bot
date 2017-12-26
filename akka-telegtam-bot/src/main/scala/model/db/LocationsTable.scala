package model.db

import model.dal.Location
import slick.jdbc.H2Profile.api._

class LocationsTable(tag: Tag) extends BaseTable[Location](tag, "location_info") {
  def fbId = column[String]("fb_id")
  def city = column[String]("city")
  def country = column[String]("country")
  def latitude = column[String]("latitude")
  def longitude = column[String]("longitude")
  def street = column[String]("street")
  def zip = column[String]("zip")

  def * = (id, fbId, city, country, latitude, longitude, street, zip) <> ((Location.apply _).tupled, Location.unapply)
}

//city, country, latitude, longitude, street, zip
