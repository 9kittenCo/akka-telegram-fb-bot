package model.dal

case class Page(name: String,
                location_id: Option[Long],
                location_fb_id:Option[String],
                phone: String,
                price_range: Option[String])
