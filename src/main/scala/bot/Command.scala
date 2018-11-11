package bot

import helpers._
import model.TelegramApiModels._

sealed trait Command {
  def chatId: Id
}

case class ProcessedMessage(chatId: Id, user: User, message: TgMessage, location: Location) extends Command
case class UnknownMessage(chatId: Id) extends Command
case class UnknownLocationMessage(chatId: Id) extends Command

object Command {
  def process(update: Update): Option[Command] = for {
    message   <- update.message
    location  <- message.location
    user      <- message.from
  } yield (user, message, location) match {
    case (u: User, m: TgMessage, l: Location) => ProcessedMessage(m.chat.id, u, m, l)
    case (_, m: TgMessage, _)                 => UnknownLocationMessage(m.chat.id)
    case _                                    => UnknownMessage(message.chat.id)
  }
}

