package model

object ChatType extends Enumeration {
  type ChatType = Value
  val Private: model.ChatType.Value = Value("private")
  val Group: model.ChatType.Value = Value("group")
  val Supergroup: model.ChatType.Value = Value("supergroup")
  val Channel: model.ChatType.Value = Value("channel")
}

//case class Audio(id: String,
//                 duration: Int,
//                 performer: Option[String],
//                 title: Option[String],
//                 mimeType: Option[String],
//                 fileSize: Option[Int])

case class Location(longitude: Float, latitude: Float)

//case class FailResult(status: Boolean, code: Int, description: String)

//case class Result[T](status: Boolean, result: T)

//case class Venue(location: Location, title: String, address: String, foursquareId: String)

case class ResponseParameters(
                               migrateToChatId: Option[Long] = None,
                               retryAfter: Option[Int] = None
                             )

case class TelegramResponse[T](ok: Boolean,
                               result: Option[T] = None,
                               description: Option[String] = None,
                               errorCode: Option[Int] = None,
                               parameters: Option[ResponseParameters] = None)

//case class Document(id: String, thumb: PhotoSize, fileName: String, mimeType: String, fileSize: String)

case class ChatPhoto(
                      smallFileId: String,
                      bigFileId: String
                    )

//case class PhotoSize(id: String, width: Int, height: Int, fileSize: Option[Int])

//case class Sticker(id: String, width: Int, height: Int, thumb: Option[PhotoSize], fileSize: Option[Int])

//case class Video(id: String, width: Int, height: Int, duration: Int, thumb: Option[PhotoSize], mimeType: Option[String], fileSize: Option[Int])

//case class Voice(id: String, duration: Int, mimeType: Option[String], fileSize: Option[Int])

//case class Contact(phoneNumber: String, firstName: String, lastName: Option[String], userId: Option[Int])

case class InlineQuery(
                        id: String,
                        from: User,
                        location: Option[Location] = None,
                        query: String,
                        offset: String
                      )

case class ChosenInlineResult(
                               resultId: String,
                               from: User,
                               location: Option[Location] = None,
                               inlineMessageId: Option[String] = None,
                               query: String
                             )

case class CallbackQuery(
                          id: String,
                          from: User,
                          message: Option[Message] = None,
                          inlineMessageId: Option[String] = None,
                          chatInstance: String,
                          data: Option[String] = None,
                          gameShortName: Option[String] = None
                        )

case class Update(
                   updateId: Int,
                   message: Option[Message] = None,
                   inlineQuery: Option[InlineQuery] = None,
                   chosenInlineResult: Option[ChosenInlineResult] = None,
                   callbackQuery: Option[CallbackQuery] = None
                 ) {

  require(
    Seq[Option[_]](
      message,
      inlineQuery,
      chosenInlineResult,
      callbackQuery,
    ).count(_.isDefined) == 1
  )
}

case class Chat(
                 id: Long,
                 `type`: ChatType.ChatType,
                 title: Option[String] = None,
                 username: Option[String] = None,
                 firstName: Option[String] = None,
                 lastName: Option[String] = None,
                 allMembersAreAdministrators: Option[Boolean] = None,
                 photo: Option[ChatPhoto] = None,
                 description: Option[String] = None,
                 inviteLink: Option[String] = None,
                 pinnedMessage: Option[Message] = None,
                 stickerSetName: Option[String] = None,
                 canSetStickerSet: Option[Boolean] = None
               )

//case class GroupChat(
//                      id: String,
//                      title: String)

case class User(
                 id: String,
                 isBot: Option[Boolean],
                 firstName: String,
                 lastName: Option[String],
                 username: Option[String],
                 language_code: Option[String])

case class Message(
                    messageId: Int,
                    from: Option[User] = None,
                    date: Int,
                    chat: Chat,
                    forwardFrom: Option[User] = None,
                    replyTo: Option[Message] = None,
                    text: Option[String] = None,
                    //                    audio: Option[Audio] = None,
                    //                    document: Option[Document] = None,
                    //                    photo: Option[List[PhotoSize]] = None,
                    //                    sticker: Option[Sticker] = None,
                    //                   video: Option[Video] = None,
                    //                   voice: Option[Voice] = None,
                    caption: Option[String] = None,
                    //                    contact: Option[Contact] = None,
                    location: Option[Location] = None,
                    newChatParticipant: Option[User] = None,
                    leftChatParticipant: Option[User] = None,
                    newChatTitle: Option[String] = None,
                    //                    newChatPhoto: Option[List[PhotoSize]] = None,
                    deleteChatPhoto: Option[Boolean] = None,
                    groupChatCreated: Option[Boolean] = None)



//case class WebhookInfo(
//                        url: String,
//                        hasCustomCertificate: Boolean,
//                        pendingUpdateCount: Int,
//                        lastErrorDate: Option[Int] = None,
//                        lastErrorMessage: Option[String] = None
//                      )