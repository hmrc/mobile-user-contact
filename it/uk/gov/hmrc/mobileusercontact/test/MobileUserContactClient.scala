package uk.gov.hmrc.mobileusercontact.test

import org.scalatestplus.play.WsScalaTestClient
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

trait MobileUserContactClient extends WsScalaTestClient  {

  client:OneServerPerSuiteWsClient =>

  private val supportResource  = "/support-requests"
  private val feedbackResource = "/feedback-submissions"
  private val applicationJson: (String, String) = "Content-Type" -> "application/json"

  def postToFeedbackSandboxResource(json:String, userIdentifier: String): Future[WSResponse] = {
    doPost(feedbackResource, json, applicationJson, "X-MOBILE-USER-ID" -> userIdentifier)
  }

  def postToFeedbackResource(json:String): Future[WSResponse] = {
    doPost(feedbackResource, json, applicationJson)
  }

  def postToSupportResource(json:String): Future[WSResponse] = {
    doPost(supportResource, json, applicationJson)
  }

  def postToSupportSandboxResource(json:String, userIdentifier: String): Future[WSResponse] = {
    doPost(supportResource, json, applicationJson, "X-MOBILE-USER-ID" -> userIdentifier)
  }

  private def doPost(path:String, json: String, o: (String, String)*): Future[WSResponse] = {
    wsUrl(path).withHeaders(o:_*).post(json)
  }
}
