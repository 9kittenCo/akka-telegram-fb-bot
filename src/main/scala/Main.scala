import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import service.{Config, MigrationConfig}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object Main extends HttpApp with Config with MigrationConfig {
  private implicit val system: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override protected def routes: Route = Routes.routes

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

