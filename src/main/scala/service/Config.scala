package service

import com.typesafe.config.ConfigFactory
import org.http4s.Uri

trait Config {
  private val config          = ConfigFactory.load()
  private val httpConfig      = config.getConfig("http")
  private val databaseConfig  = config.getConfig("database")
  val httpInterface: String   = httpConfig.getString("interface")
  val httpPort: Int           = httpConfig.getInt("port")

  private lazy val facebookConfig = config.getConfig("api.facebook")
  lazy val fbServiceUrl: String   = facebookConfig.getString("serviceUrl")

  private lazy val telegramConfig = config.getConfig("api.telegram")
  lazy val tgServiceUrl: Uri   = Uri.fromString(telegramConfig.getString("serviceUrl")).toOption.get


  val databaseUrl: String      = databaseConfig.getString("url")
  val databaseUser: String     = databaseConfig.getString("user")
  val databasePassword: String = databaseConfig.getString("password")

  lazy val fbAccessToken: String = getToken("facebook.token")
  lazy val tgAccessToken: String = getToken("bot.token")

  lazy val telegramUrl           = tgServiceUrl / s"bot$tgAccessToken"

  def getToken(tokenName: String): String = {
    getClass.getResourceAsStream(s"/$tokenName")
    val file_ = scala.io.Source.fromInputStream(getClass.getResourceAsStream(s"/$tokenName"))
    scala.util.Properties
      .envOrNone("TOKEN")
      .getOrElse(file_.getLines().mkString)
  }
}
