import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import service.{Config, MigrationConfig}


object Main extends App with Config with MigrationConfig with Routes {
  protected val log: LoggingAdapter = Logging(system, getClass)

  migrate()
  //reloadSchema()

  val bindingFuture = Http()
    .bindAndHandle(handler = logRequestResult("log")(routes)
      , interface = httpInterface, port = httpPort)

  println(s"Server online at $httpInterface:$httpPort\nPress RETURN to stop...")
  scala.io.StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}


