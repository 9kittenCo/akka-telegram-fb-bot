package model

import helpers.DistanceKm
import model.dal.Page

case class Cursors(
    after: String,
    before: String
)

case class Paging(
    cursors: Option[Cursors],
    next: Option[String],
    previous: Option[String]
)

case class Response[T](
    data: Seq[T],
    paging: Option[Paging]
)

case class Tab(id: String, name: String, link: String)

case class PageInfo(id: String, name: String, location: Option[PageLocation], phone: Option[String], price_range: Option[String])

case class SearchPagesInfo(id: String, name: String)

case class PageLocation(
    city: String,
    city_id: Option[String],
    country: Option[String],
    country_code: Option[String],
    latitude: Option[Float],
    located_in: Option[String],
    longitude: Option[Float],
    region: Option[String],
    region_id: Option[String],
    state: Option[String],
    street: Option[String],
    zip: Option[String])

case class Error(message: String, `type`: String, code: Int, error_subcode: Option[Int])

case class ErrorResponse(error: Error)

case class PageDistance(page: Page, distanceKm: DistanceKm)
