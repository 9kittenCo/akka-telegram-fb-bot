package bot

import helpers.{DistanceKm, MessageId}

case class ParsedUserMessage(userId: Long, messageId: MessageId, name: String, url: String, longitude: Float, latitude: Float, distance: DistanceKm)

trait CowobotServiceAlgebra[F[_]] {
  def parseUserMessages(messages: F[List[ProcessedMessage]]): F[List[ParsedUserMessage]]
  def parseUserMessage(messages: F[ProcessedMessage]): F[List[ParsedUserMessage]]
}
