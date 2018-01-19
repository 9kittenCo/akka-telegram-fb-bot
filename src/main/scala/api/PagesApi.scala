package api

import java.sql.Timestamp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import client.CityClient._
import client.FacebookClient._
import client._
import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.extras.auto._
import io.circe.syntax._
import model.dal._


trait PagesApi extends ApiErrorHandler with BaseClient {

  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }

  val coworkingsRoute: Route =
    (path("coworkings") & get) {
      complete(PagesDal.findAll().map(_.asJson))
    } ~
      (path("coworkings" / "city" / Segment) & get) { name: String =>
        complete {
          PagesDal.getByCity(name.toLowerCase).map(_.asJson)
        }
      } ~
      (path("coworkings" / "search") & parameters(('lat.as[Float], 'lon.as[Float]))) { (lat: Float, lon: Float) =>
        complete {
          getPagesByLocation(lat, lon) map (_.asJson)
        }
      } ~
      (path("coworkings" / "cities") & parameters(('lat.as[Float], 'lon.as[Float]))) { (lat: Float, lon: Float) =>
        complete {
          getNearestCities(lat, lon) map (_.asJson)
        }
      } ~
      (path("coworkings" / "update" / "city" / Segment) & get) { name: String =>
        complete(updatePagesByCity(name))
      } ~
      (path("coworkings" / "update" / "location_catalog") & get) {
        complete(CitiesDal.insert(CityClient.cities).map(_.asJson))
      } ~
      (path("coworkings" / "id" / Segment) & get) { pageId: String =>
        complete {
          getPageInfo(pageId).map(_.asJson)
        }
      } ~
      (path("coworkings" / IntNumber) & get) { id =>
        complete(PagesDal.findById(id.toLong).map(_.asJson))
      } ~
      (path("coworkings") & post) {
        entity(as[Page]) { coworking =>
          complete(PagesDal.create(coworking).map(_.asJson))
        }
      } ~
      (path("coworkings" / IntNumber) & put) { id =>
        entity(as[Page]) { coworking =>
          complete(PagesDal.update(id.toLong, coworking).map(_.asJson))
        }
      } ~
      (path("coworkings" / IntNumber) & delete) { id =>
        complete(PagesDal.delete(id.toLong).map(_.asJson))
      }

  val telegramRoute: Route = (path("cowobot") & get) {
    complete(TelegramClient.msgsF.map(_.asJson))
  }
}
