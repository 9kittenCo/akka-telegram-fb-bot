package model.dal

import java.sql.Timestamp

case class Page(fb_id: String,
                name: String,
                phone: Option[String],
                city: String,
                country: Option[String],
                price_range: Option[String],
                latitude: Float,
                longitude: Float,
                street: Option[String],
                zip: Option[String],
                created_at: Timestamp)
