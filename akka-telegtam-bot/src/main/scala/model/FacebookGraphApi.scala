package model

import scala.concurrent.Future
import FacebookGraphApiJsonProtocol._

trait FacebookGraphApi {

//  def newPhotos(accessToken: String, after: Option[String]): Future[Response[Photo]]

//  def getUser(accessToken: String): Future[User]

  def findPagesByCity(city: String): Future[Response[SearchPagesInfo]]

  def getPageInfo(pageId: String): Future[PageInfo]

  def getPagesByLocation(location: Location): Future[Seq[PageInfo]]

  //  def getTab(pageId: String, appId: String, token: String): Future[Response[Tab]]

//  def getFriends(accessToken: String): Future[Seq[User]]

//  def getLikes(objectId: String, accessToken: String): Future[Seq[User]]

//  def getApplicationOpenGraphActionCreate(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]]
//
//  def getApplicationOpenGraphActionClick(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]]
//
//  def getApplicationOpenGraphActionImpressions(appId: String, accessToken: String, since: Long, until: Long): Future[Seq[Insight]]
}
