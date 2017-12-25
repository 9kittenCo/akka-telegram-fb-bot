package model

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

object FacebookGraphApiJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

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
    // Ignores Facebook default photo
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
                            phone: String,
                            price_range: String)

  case class SearchPagesInfo(id: String,
                             name: String)

  case class PageLocation(id: String,
                          //                    pageId:Long,
                          city: String,
                          country: String,
                          latitude: String,
                          longitude: String,
                          street: String,
                          zip: String)


  case class Error(message: String, `type`: String, code: Int, error_subcode: Option[Int])

  case class ErrorResponse(error: Error)

  implicit val imageFormat: RootJsonFormat[Image] = jsonFormat3(Image.apply)
  implicit val photoFormat: RootJsonFormat[Photo] = jsonFormat3(Photo)
  implicit val cursorsFormat: RootJsonFormat[Cursors] = jsonFormat2(Cursors)
  implicit val pagingFormat: RootJsonFormat[Paging] = jsonFormat3(Paging)

  implicit def responseFormat[T: JsonFormat]: RootJsonFormat[Response[T]] = jsonFormat2(Response.apply[T])

  implicit val userProfilePic: RootJsonFormat[UserProfilePic] = jsonFormat2(UserProfilePic)
  implicit val userProfilePicContainer: RootJsonFormat[UserProfilePicContainer] = jsonFormat1(UserProfilePicContainer)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat10(User)
  implicit val pageFormat: RootJsonFormat[Page] = jsonFormat3(Page)
  implicit val tabFormat: RootJsonFormat[Tab] = jsonFormat3(Tab)
  implicit val facebookFriends: RootJsonFormat[FacebookFriends] = jsonFormat1(FacebookFriends)

  implicit val insightValueFormat: RootJsonFormat[InsightValue] = jsonFormat5(InsightValue)
  implicit val insightDataPointFormat: RootJsonFormat[InsightDataPoint] = jsonFormat2(InsightDataPoint)
  implicit val insightFormat: RootJsonFormat[Insight] = jsonFormat6(Insight)

  implicit val searchPagesInfoFormat: RootJsonFormat[SearchPagesInfo] = jsonFormat2(SearchPagesInfo)
  implicit val pageInfoFormat: RootJsonFormat[PageInfo] = jsonFormat5(PageInfo)
  implicit val pageLocationFormat: RootJsonFormat[PageLocation] = jsonFormat7(PageLocation)

  implicit val errorFormat: RootJsonFormat[Error] = jsonFormat4(Error)
  implicit val errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)

//  implicit val locationFormat: RootJsonFormat[Location] = jsonFormat8(Location)

  implicit object TimestampFormat extends RootJsonFormat[Timestamp] {
    def write(ts: Timestamp) = JsNumber(ts.getTime)

    def read(value: JsValue): Timestamp = value match {
      case JsNumber(n) => new Timestamp(n.bigDecimal.longValue())
      case _ => deserializationError("long timestamp expected")
    }
  }

}