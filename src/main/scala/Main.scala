import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import api.PagesApi
import client.BaseClient
import service.MigrationConfig

import scala.concurrent.Future
import scala.io.StdIn

object Main extends App with BaseClient with MigrationConfig with PagesApi with Routes {

 // protected def routes: Route = Routes.routes
 protected val log: LoggingAdapter = Logging(system, getClass)

  migrate()

  val bindingFuture: Future[Http.ServerBinding] = Http()
    .bindAndHandle(handler = logRequestResult("log")(routes)
      , interface = httpInterface, port = httpPort)

  println(s"Server online at $httpInterface:$httpPort\nPress RETURN to stop...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}

