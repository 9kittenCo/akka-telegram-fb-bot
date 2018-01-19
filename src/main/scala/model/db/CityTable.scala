package model.db

import model.dal.City
import slick.jdbc.PostgresProfile.api._

class CityTable(tag: Tag) extends BaseTable[City](tag, "city_location_catalog") {
  val name = column[String]("name")
  val alternate_names = column[String]("alternate_names")
  val latitude = column[Float]("latitude")
  val longitude = column[Float]("longitude")

  val * = (name, alternate_names, latitude, longitude, createdAt) <> (City.tupled, City.unapply)

}
