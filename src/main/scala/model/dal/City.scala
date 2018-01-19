package model.dal

import java.sql.Timestamp

case class City(name: String, alternate_names: String, latitude: Float, longitude: Float, created_at: Timestamp)
