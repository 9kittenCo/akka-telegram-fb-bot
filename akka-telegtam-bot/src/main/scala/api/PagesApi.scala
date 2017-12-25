package api

import java.sql.Timestamp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import client.FacebookClient._
import model.FacebookGraphApiJsonProtocol._
import model.dao.PagesDao
import model.{Location, Page}
import spray.json._

trait JsonMapping extends DefaultJsonProtocol {

  implicit object TimestampFormat extends RootJsonFormat[Timestamp] {
    def write(ts: Timestamp) = JsNumber(ts.getTime)

    def read(value: JsValue): Timestamp = value match {
      case JsNumber(n) => new Timestamp(n.bigDecimal.longValue())
      case _ => deserializationError("long timestamp expected")
    }
  }

  implicit def responseFormat[T: JsonFormat]: RootJsonFormat[Response[T]] = jsonFormat2(Response.apply[T])

  implicit val pageFormat: RootJsonFormat[Page] = jsonFormat6(Page)
  implicit val locationFormat: RootJsonFormat[Location] = jsonFormat8(Location)
}

trait PagesApi extends JsonMapping with ApiErrorHandler {
  val coworkingsRoute: Route =
    (path("coworkings") & get) {
      complete(PagesDao.findAll().map(_.toJson))
    } ~
//      (path("coworkings" / "city" / Segment) & get) { name: String =>
//        complete {
//          findPagesByCity(name) flatMap { pages =>
//            Future.sequence {
//              pages map (page => getPageInfo(page.id))
//            }
//          }
//        }
//      } ~
      (path("coworkings" / IntNumber) & get) { id =>
        complete(PagesDao.findById(id).map(_.toJson))
      } ~
      (path("coworkings") & post) {
        entity(as[Page]) { coworking =>
          complete(PagesDao.create(coworking).map(_.toJson))
        }
      } ~
      (path("coworkings" / IntNumber) & put) { id =>
        entity(as[Page]) { coworking =>
          complete(PagesDao.update(id, coworking).map(_.toJson))
        }
      } ~
      (path("coworkings" / IntNumber) & delete) { id =>
        complete(PagesDao.delete(id).map(_.toJson))
      }
}
