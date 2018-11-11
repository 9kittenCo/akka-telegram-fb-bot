import api.TelegramApiService
import bot.{Command, CowobotService, EventSourceImpl, ProcessedMessage}
import cats.effect._
import client.{CityClient, FacebookClient}
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s.client.blaze.Http1Client
import service.DoobieDatabaseConfig
import cats.implicits._
import model.dao.DoobieCityAlgebra

object Main extends StreamApp[IO] {

  import service.MigrationConfig._
  migrate()

  override def stream(args: List[String], shutdown: IO[Unit]): Stream[IO, ExitCode] = createStream[IO](args, shutdown)

  def createStream[F[_]](args: List[String], shutdown: F[Unit])(
    implicit E: Effect[F]): Stream[F, ExitCode] =
    for {
      client    <- Http1Client.stream()
      dbConfig   = new DoobieDatabaseConfig[F]
      dbAlgebra  = new DoobieCityAlgebra[F](dbConfig.xa)
      citiesF      = dbAlgebra.findAll()
      cities    <- Stream.eval(citiesF)
      apiC       = new CityClient[F](dbAlgebra, cities)
      apiT       = new TelegramApiService[F](client)
      apiF       = new FacebookClient[F](apiC, dbAlgebra)
      cowobot    = new CowobotService[F](apiT, apiF)
      eventSource = EventSourceImpl(apiT)
      _ <- eventSource.events().map(Command.process).collect {
        case Some(command) => command match {
          case processedMessage@ProcessedMessage(_, _, _, _) =>
            for {
              pums <- cowobot.parseUserMessage(E.delay(processedMessage))
              pum  <- pums
              _     = apiT.sendMessage(pum.userId, s"${pum.name} - ${pum.distance} m. \n ${pum.url}")
              _     = apiT.sendLocation(pum.userId, pum.latitude, pum.longitude)
            } yield ()
        }
      }.evalMap(v => v).last
    } yield ExitCode.Success
}