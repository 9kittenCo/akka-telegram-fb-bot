//package api
//
//import model.ResponseParameters
//
//case class TelegramApiException(message: String, errorCode: Int, cause: Option[Throwable] = None, parameters: Option[ResponseParameters] = None)
//  extends Exception(message, cause.orNull)