package client

import java.sql.Timestamp
import java.util.Date

import helpers.{CityName, Distance}
import model.dal.{CitiesDal, City}
import utils.DistanceCalculation.getDistance

import scala.concurrent.Future

object CityClient extends BaseClient {
  private lazy val resourceStream = getClass.getResourceAsStream("/cities15000.txt")
  private lazy val file_cities = scala.io.Source.fromInputStream(resourceStream)
  private lazy val ds_cities: Iterator[Array[String]] = file_cities.getLines map (line => line.split("\t").map(_.trim))
  lazy val cities: Seq[City] = ds_cities map { l =>
    City(l(1),
      l(3),
      l(4).toFloat,
      l(5).toFloat,
      new Timestamp(new Date().getTime))
  } toSeq

  //@todo implement full text search
  def getNearestCities(lat: Float, lon: Float): Future[List[(CityName, Distance)]] = {
    CitiesDal.findAll() map { cities =>
      cities.map(city =>
        (city.name, getDistance(lat, lon, city.latitude, city.longitude))).toList.sortBy(_._2)
    } map (data =>
      data.take(3))
  }
}
