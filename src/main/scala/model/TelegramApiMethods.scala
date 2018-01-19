package model


case class SendMessage(
                        chatId                : String,
                        text                  : String,
                        parseMode             : Option[String] = None,
                        disableWebPagePreview : Option[Boolean] = None,
                        disableNotification   : Option[Boolean] = None,
                        replyToMessageId      : Option[Int] = None
                      )

case class SendLocation(
                         chatId: String,
                         latitude: Double,
                         longitude: Double,
                         disableNotification: Option[Boolean] = None,
                         replyToMessageId: Option[Long] = None
 //                        reply_markup: Option[ReplyMarkup] = None
                       )