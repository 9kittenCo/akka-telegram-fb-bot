package utils

import helpers.Distance

object DistanceCalculation {
  //** The Earth's radius, in meters. *///** The Earth's radius, in meters. */
  private final val EARTH_RADIUS_KM = 6372795

  /**
    * Gets the great circle distance in meters between two geographical points, using
    * the <a href="http://en.wikipedia.org/wiki/Haversine_formula">haversine formula</a>.
    *
    * @param latitude1  the latitude of the first point
    * @param longitude1 the longitude of the first point
    * @param latitude2  the latitude of the second point
    * @param longitude2 the longitude of the second point
    * @return the distance, in kilometers, between the two points
    */
  def getDistance(latitude1: Float, longitude1: Float, latitude2: Float, longitude2: Float): Distance = {
    val dLat = Math.toRadians(latitude2 - latitude1)
    val dLon = Math.toRadians(longitude2 - longitude1)
    val lat1 = Math.toRadians(latitude1)
    val lat2 = Math.toRadians(latitude2)
    val sqrtHaversineLat = Math.sin(dLat / 2)
    val sqrtHaversineLon = Math.sin(dLon / 2)
    val a = sqrtHaversineLat * sqrtHaversineLat + sqrtHaversineLon * sqrtHaversineLon * Math.cos(lat1) * Math.cos(lat2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    (c * EARTH_RADIUS_KM).toInt
  }

}
