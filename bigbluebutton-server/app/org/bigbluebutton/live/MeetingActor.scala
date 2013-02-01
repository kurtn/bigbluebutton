package org.bigbluebutton.live

import akka.actor.Actor
import akka.actor.ActorRef

class MeetingActor extends Actor {
  import Meeting._
  
  def receive = {
    case msg: Join => {
      sender ! new Joined("12345") 
    }
    
    case msg: UserEnter => {
      sender ! "OK"
    }
  }
}

object Meeting {
  trait MeetingMessage
  case object End extends MeetingMessage
  case object ForceEnd extends MeetingMessage
  case class Join(val user: UserInfo) extends MeetingMessage
  case class Joined(val authToken: String) extends MeetingMessage
  case class UserEnter(val authToken: String) extends MeetingMessage
  
  case class UserInfo(val name: String, val internalUserID: String, val externalUserID: String, val role: String)
  
  case class MeetingDescriptor(val name: String, val externalID: String, val internalID: String, val duration: Long, val createdOn: Long,
		  						val startedOn: Long, val endedOn: Long, val telVoice: String, val webVoice: String, val moderatorPass: String,
		  						val viewerPass: String, val welcomeMsg: String, val logoutUrl: String, val maxUsers: Number, val record: Boolean,
		  						val dialNumber: String, val defaultAvatarURL: String)
		  						
  case class UserSession(val internalUserID: String, val meetingName: String, val meetingID: String, val externanMeetingID: String,
		  					val externalUserID: String, val username: String, val role: String, val voiceBridge: String, val webVoiceConf: String,
		  					val record: String, val welcomeMessage: String, val logoutURL: String, val defauleLayout: String, val avatarURL: String)

}