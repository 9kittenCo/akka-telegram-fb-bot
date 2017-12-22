package client

import java.sql.Timestamp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import model.FacebookGraphApiJsonProtocol.Response
import model.{Location, Page}
import model.dao.PagesDao
import service.Config
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, JsonFormat, RootJsonFormat, deserializationError}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
trait JsonMapping extends DefaultJsonProtocol {
  implicit object TimestampFormat extends RootJsonFormat[Timestamp] {
    def write(ts: Timestamp) = JsNumber(ts.getTime)

    def read(value: JsValue): Timestamp = value match {
      case JsNumber(n) => new Timestamp(n.bigDecimal.longValue())
      case _ => deserializationError("long timestamp expected")
    }
  }

  implicit def responseFormat[T : JsonFormat]: RootJsonFormat[Response[T]] = jsonFormat2(Response.apply[T])
  implicit val pageFormat: RootJsonFormat[Page] = jsonFormat6(Page)
  implicit val locationFormat: RootJsonFormat[Location] = jsonFormat8(Location)
}

object FacebookClient extends App with Config with JsonMapping {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //TODO add city choose funtionality
  val city = "kyiv"

  private lazy val uri = s"https://graph.facebook.com/search?q=coworking+$city&type=page&access_token=$fbAccessToken"

  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = uri))

  val httpResponse = Await.result(responseFuture, Duration.Inf)
  if (httpResponse.status.isSuccess()) {
    Some(Await.result(Unmarshal(httpResponse.entity).to[Page], Duration.Inf))
  } else {
    None
  }
  def findPagesByCity(nameCity: String): Future[Seq[Page]] = {
    val maybePage: Future[Seq[Page]] = PagesDao.findByCity(nameCity)
    maybePage
//    val pages = maybePage. {
//        case Success(res) => res
//        case Failure(e) => sys.error(e.getLocalizedMessage)
//      }
  }
}
