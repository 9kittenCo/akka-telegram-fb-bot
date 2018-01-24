import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.{ApiErrorHandler, PagesApi}

trait Routes extends ApiErrorHandler with PagesApi {
  val routes: Route =
    pathPrefix("v1") {
      coworkingsRoute ~
        telegramRoute
    }
}
