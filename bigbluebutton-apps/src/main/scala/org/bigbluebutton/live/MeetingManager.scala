package org.bigbluebutton.live

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.Actor



class MeetingManager extends Actor {
	def receive = {
	    case msg => {
	      sender ! msg
	    }	  
	}
}

object MeetingManager {
  case class CreateMeeting(val meetingName: String, val externalMeetingID: String, val internalMeetingID: String)
}