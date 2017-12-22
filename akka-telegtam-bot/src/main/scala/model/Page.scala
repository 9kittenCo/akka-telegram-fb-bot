package model

import java.sql.Timestamp

case class Page(id: Long,
                name: String,
                location_id: Option[Long],
                phone: String,
                price_range: String,
                createdAt: Timestamp)
