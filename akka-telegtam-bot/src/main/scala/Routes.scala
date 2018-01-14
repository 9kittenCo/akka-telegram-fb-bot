import api.{ApiErrorHandler, PagesApi}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait Routes extends ApiErrorHandler with PagesApi {
  val routes: Route =
    pathPrefix("v1") {
      telegramRoute //coworkingsRoute
    }// ~ {
    //  telegramRoute
    //}
}
