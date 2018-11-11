package client

import io.circe.generic.extras.Configuration

trait BaseClient {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames.withSnakeCaseConstructorNames

}
