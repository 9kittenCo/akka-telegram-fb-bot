import api.{ApiErrorHandler, PagesApi}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import service.Config

object Routes extends ApiErrorHandler with Config {

  lazy val pagesApi = new PagesApi

  val routes: Route =
    pathPrefix("v1") {
      pagesApi.coworkingsRoute ~
      pagesApi.telegramRoute
    }
}
