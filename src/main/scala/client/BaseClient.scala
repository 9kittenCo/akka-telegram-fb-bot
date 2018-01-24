package client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.circe.generic.extras.Configuration

import scala.concurrent.ExecutionContextExecutor

trait BaseClient {
//  implicit val system: ActorSystem = ActorSystem()
//  implicit val mat: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
//  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames.withSnakeCaseConstructorNames
}
