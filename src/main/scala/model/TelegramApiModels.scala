package model

import helpers.Id
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec, JsonKey}

object TelegramApiModels {

  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  //  @ConfiguredJsonCodec
  case class TelegramResponse[T](ok: Boolean, result: T)


  @ConfiguredJsonCodec
  case class Update(updateId: Id,
                    message: Option[TgMessage] = None,
                    editedMessage: Option[TgMessage] = None,
                    channelPost: Option[TgMessage] = None,
                    editedChannelPost: Option[TgMessage] = None,
                    inlineQuery: Option[InlineQuery] = None)

  @ConfiguredJsonCodec
  case class InlineQuery(id: String,
                         from: User,
                         location: Option[Location],
                         query: String,
                         offset: String)

  @ConfiguredJsonCodec
  case class Location(longitude: Float, latitude: Float)

  @ConfiguredJsonCodec
  case class User(id: Id,
                  isBot: Boolean,
                  firstName: String,
                  lastName: Option[String],
                  username: Option[String],
                  languageCode: Option[String])


  @ConfiguredJsonCodec
  case class ChatPhoto(smallFileId: String, bigFileId: String)

  @ConfiguredJsonCodec
  case class Chat(id: Id,
                  @JsonKey("type")
                  `type`: String,
                  title: Option[String] = None,
                  username: Option[String] = None,
                  firstName: Option[String] = None,
                  lastName: Option[String] = None,
                  allMembersAreAdministrators: Option[String] = None,
                  photo: Option[ChatPhoto] = None,
                  description: Option[String] = None,
                  inviteLink: Option[String] = None,
                  pinnedMessage: Option[TgMessage] = None,
                  stickerSetName: Option[String] = None,
                  canSetStickerSet: Option[String] = None)

  @ConfiguredJsonCodec
  case class MessageEntity(`type`: String,
                           offset: Int,
                           length: Int,
                           url: Option[String],
                           user: Option[User])

  @ConfiguredJsonCodec
  case class Audio(fileId: String,
                   duration: Int,
                   performer: Option[String],
                   title: Option[String],
                   mimeType: Option[String],
                   fileSize: Option[Int])

  @ConfiguredJsonCodec
  case class TgMessage(
                        messageId: Id,
                        date: Long,
                        chat: Chat,
                        from: Option[User] = None,
                        forwardFrom: Option[User] = None,
                        forwardFromChat: Option[Chat] = None,
                        forwardFromMessageId: Option[Id] = None,
                        forwardSignature: Option[String] = None,
                        forwardDate: Option[Int] = None,
                        replyToMessage: Option[TgMessage] = None,
                        editDate: Option[Int] = None,
                        mediaGroupId: Option[String] = None,
                        authorSignature: Option[String] = None,
                        text: Option[String] = None,
                        entities: Option[Array[MessageEntity]] = None,
                        captionEntities: Option[Array[MessageEntity]] = None,
                        location: Option[Location] = None
                      )

  @ConfiguredJsonCodec
  case class Webhook(url: String,
                     maxConnections: Option[Int] = None,
                     allowedUpdates: Option[Array[String]] = None)

  @ConfiguredJsonCodec
  case class SendLocation(
                           chatId: Id,
                           latitude: Double,
                           longitude: Double,
                           disableNotification: Option[Boolean] = None,
                           replyToMessageId: Option[Long] = None
                         )

  @ConfiguredJsonCodec
  case class SendMessage(
                          chatId: Id,
                          text: String,
                          parseMode: Option[String] = None,
                          disableWebPagePreview: Option[Boolean] = None,
                          disableNotification: Option[Boolean] = None,
                          replyToMessageId: Option[Long] = None
                        )

  @ConfiguredJsonCodec
  case class GetUpdates(
                         offset: Option[Long] = None,
                         limit: Option[Int] = Some(100),
                         timeout: Option[Int] = Some(0),
                         allowed_updates: Option[List[String]] = None
                       )

}
