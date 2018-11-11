package client

import java.sql.Timestamp
import java.util.Date

import cats.effect._
import cats.implicits._
import helpers.{CityName, DistanceKm}
import model.dal.City
import model.dao.DoobieCityAlgebra
import utils.DistanceCalculation.getDistance

import scala.language.higherKinds


case class CityWithDistance(cityName: String, distanceKm: DistanceKm)

class CityClient[F[_] : Effect](dbClient: DoobieCityAlgebra[F], citiesDb: List[City])(implicit E: Effect[F]) extends BaseClient {
  private lazy val resourceStream = getClass.getResourceAsStream("/cities15000.txt")
  private lazy val file_cities = scala.io.Source.fromInputStream(resourceStream)
  private lazy val ds_cities: Iterator[Array[String]] = file_cities.getLines map (line => line.split("\t").map(_.trim))
  lazy val cities: Seq[City] = ds_cities map { l =>
    City(l(1), l(3), l(4).toFloat, l(5).toFloat, new Timestamp(new Date().getTime))
  } toSeq

  //@todo implement full text search
  def getNearestCities(lat: Float, lon: Float): F[List[(CityName, DistanceKm)]] = {
    E.pure(citiesDb.map {city =>
      (city.name, getDistance(lat, lon, city.latitude, city.longitude))
    }.sortBy(_._2).take(3))
  }
}
