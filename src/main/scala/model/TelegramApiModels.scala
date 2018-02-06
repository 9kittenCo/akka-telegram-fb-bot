package model

sealed trait ChatType extends Product with Serializable {
  def name: String = this.productPrefix
}

object ChatType {

  case object Private extends ChatType { override val name: String = super.name }
  case object Group extends ChatType { override val name: String = super.name }
  case object Super_group extends ChatType { override val name: String = super.name }
  case object Channel extends ChatType { override val name: String = super.name }

  def unsafe(str: String): ChatType = str match {
    case Private.name     => Private
    case Group.name       => Group
    case Super_group.name => Super_group
    case Channel.name     => Channel
//    case _               => sys.error(s"Unexpected call: $str")
  }
}

//case class Audio(id: String,
//                 duration: Int,
//                 performer: Option[String],
//                 title: Option[String],
//                 mimeType: Option[String],
//                 fileSize: Option[Int])

case class Location(longitude: Float, latitude: Float)

//case class Venue(location: Location, title: String, address: String, foursquareId: String)

//case class ResponseParameters(
//                               migrateToChatId: Option[Long] = None,
//                               retryAfter: Option[Int] = None
//                             )

case class TelegramResponse[T](ok: Boolean, result: Option[T], description: Option[String] = None, error_code: Option[Int] = None)

//case class Document(id: String, thumb: PhotoSize, fileName: String, mimeType: String, fileSize: String)

//case class PhotoSize(fileId: String, width: Int, height: Int, fileSize: Option[Int])

//case class ChatPhoto(
//                      smallFileId: String,
//                      bigFileId: String
//                    )

//case class PhotoSize(id: String, width: Int, height: Int, fileSize: Option[Int])

//case class Sticker(id: String, width: Int, height: Int, thumb: Option[PhotoSize], fileSize: Option[Int])

//case class Video(id: String, width: Int, height: Int, duration: Int, thumb: Option[PhotoSize], mimeType: Option[String], fileSize: Option[Int])

//case class Voice(id: String, duration: Int, mimeType: Option[String], fileSize: Option[Int])

//case class Contact(phoneNumber: String, firstName: String, lastName: Option[String], userId: Option[Int])

//case class InlineQuery(
//                        id: String,
//                        from: User,
//                        location: Option[Location] = None,
//                        query: String,
//                        offset: String
//                      )
//
//case class ChosenInlineResult(
//                               resultId: String,
//                               from: User,
//                               location: Option[Location] = None,
//                               inlineMessageId: Option[String] = None,
//                               query: String
//                             )

//case class CallbackQuery(
//                          id: String,
//                          from: User,
//                          message: Option[Message] = None,
//                          inlineMessageId: Option[String] = None,
//                          chatInstance: String,
//                          data: Option[String] = None,
//                          gameShortName: Option[String] = None
//                        )

case class Update(
    update_id: Int,
    message: Option[Message] = None
//                   inlineQuery: Option[InlineQuery] = None,
//                   chosenInlineResult: Option[ChosenInlineResult] = None
    //callbackQuery: Option[CallbackQuery] = None
)

case class Chat(
    id: Int,
    //                `type`: ChatType,
    `type`: String,
    title: Option[String] = None,
    username: Option[String] = None,
    first_name: Option[String] = None,
    last_name: Option[String] = None,
//                 allMembersAreAdministrators: Option[Boolean] = None,
//                 photo: Option[ChatPhoto] = None,
    description: Option[String] = None
//                 inviteLink: Option[String] = None,
//                 pinnedMessage: Option[Message] = None,
//                 stickerSetName: Option[String] = None,
//                 canSetStickerSet: Option[Boolean] = None
)

//case class GroupChat(
//                      id: String,
//                      title: String)

case class User(id: Int, is_bot: Boolean, first_name: String, last_name: Option[String], username: Option[String], language_code: Option[String])

case class Message(
    message_id: Int,
    from: Option[User] = None,
    date: Int,
    chat: Chat,
    forward_from: Option[User] = None,
    reply_to: Option[Message] = None,
    text: Option[String] = None,
    //                    audio: Option[Audio] = None,
    //                    document: Option[Document] = None,
    //                    photo: Option[List[PhotoSize]] = None,
    //                    sticker: Option[Sticker] = None,
    //                   video: Option[Video] = None,
    //                   voice: Option[Voice] = None,
    caption: Option[String] = None,
    //                    contact: Option[Contact] = None,
    location: Option[Location] = None)
//                    newChatParticipant: Option[User] = None,
//                    leftChatParticipant: Option[User] = None,
//                    newChatTitle: Option[String] = None,
//                    newChatPhoto: Option[List[PhotoSize]] = None,
//                    deleteChatPhoto: Option[Boolean] = None)
//case class WebhookInfo(
//                        url: String,
//                        hasCustomCertificate: Boolean,
//                        pendingUpdateCount: Int,
//                        lastErrorDate: Option[Int] = None,
//                        lastErrorMessage: Option[String] = None
//                      )
