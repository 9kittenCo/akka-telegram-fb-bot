package model

import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import model.dal.Location

import scala.concurrent.Future

trait FacebookGraphApi extends FailFastCirceSupport{

//  def newPhotos(accessToken: String, after: Option[String]): Future[Response[Photo]]

//  def getUser(accessToken: String): Future[User]

  def findPagesByCity(city: String): Future[Seq[SearchPagesInfo]]

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
