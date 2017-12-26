package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import client.FacebookClient._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import model.dal._


trait PagesApi extends ApiErrorHandler with FailFastCirceSupport{
  val coworkingsRoute: Route =
    (path("coworkings") & get) {
      complete(PagesDal.findAll().map(_.asJson))
    } ~
      (path("coworkings" / "city" / Segment) & get) { name: String =>
        complete {getPagesInfoByCity(name) map(_.asJson)}
      } ~
      (path("coworkings" / IntNumber) & get) { id =>
        complete(PagesDal.findById(id).map(_.asJson))
      } ~
      (path("coworkings") & post) {
        entity(as[Page]) { coworking =>
          complete(PagesDal.create(coworking).map(_.asJson))
        }
      } ~
      (path("coworkings" / IntNumber) & put) { id =>
        entity(as[Page]) { coworking =>
          complete(PagesDal.update(id, coworking).map(_.asJson))
        }
      } ~
      (path("coworkings" / IntNumber) & delete) { id =>
        complete(PagesDal.delete(id).map(_.asJson))
      }
}
