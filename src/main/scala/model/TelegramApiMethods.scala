package model


case class SendMessage(
                        chatId                : String,
                        text                  : String,
                        parseMode             : Option[String] = None,
                        disableWebPagePreview : Option[Boolean] = None,
                        disableNotification   : Option[Boolean] = None,
                        replyToMessageId      : Option[Long] = None
                      )

case class SendLocation(
                         chatId: String,
                         latitude: Double,
                         longitude: Double,
                         disableNotification: Option[Boolean] = None,
                         replyToMessageId: Option[Long] = None
 //                        reply_markup: Option[ReplyMarkup] = None
                       )

case class GetUpdates(
                       offset: Option[Long] = None,
                       limit: Option[Int] = Some(100),
                       timeout: Option[Int] = Some(0),
                       allowed_updates: Option[List[String]] = None
                     )