package model


case class Image(
                  width: Int,
                  height: Int,
                  source: String
                )

object Image {
  implicit val ordering: Ordering[Image] = new Ordering[Image] {
    def compare(x: Image, y: Image): Int = (y.width * y.height) compare (x.width * x.height)
  }
}

case class Photo(
                  id: String,
                  name: Option[String], //this is the photo caption
                  images: Seq[Image]
                )

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

case class UserProfilePic(url: String, is_silhouette: Boolean)

case class UserProfilePicContainer(data: UserProfilePic)

case class User(
                 id: String,
                 username: Option[String],
                 name: Option[String],
                 first_name: Option[String],
                 middle_name: Option[String],
                 last_name: Option[String],
                 email: Option[String],
                 link: Option[String],
                 gender: Option[String],
                 picture: Option[UserProfilePicContainer]
               ) {

  def profilePic: Option[String] = picture.flatMap(p => if (p.data.is_silhouette) None else Some(p.data.url))
}

case class FacebookFriends(data: Seq[User])

case class InsightValue(
                         action_type_id: Long,
                         action_type_name: String,
                         object_type_id: Long,
                         object_type_name: String,
                         value: Int
                       )

case class InsightDataPoint(
                             value: Seq[InsightValue],
                             end_time: String
                           )

case class Insight(
                    id: String,
                    name: String,
                    period: String,
                    values: Seq[InsightDataPoint],
                    title: String,
                    description: String
                  )

case class Page(id: String, name: String, link: String)

case class Tab(id: String, name: String, link: String)

case class PageInfo(id: String,
                    name: String,
                    location_id: Option[PageLocation],
                    phone: Option[String],
                    price_range: Option[String])

case class SearchPagesInfo(id: String,
                           name: String)

case class PageLocation(id: String,
                        //                    pageId:Long,
                        city: String,
                        country: String,
                        latitude: String,
                        longitude: String,
                        street: Option[String],
                        zip: Option[String])


case class Error(message: String, `type`: String, code: Int, error_subcode: Option[Int])

case class ErrorResponse(error: Error)
