package org.bigbluebutton.live

import akka.actor.Actor
import akka.actor.ActorRef

class MeetingActor extends Actor {
  def receive = {
    case msg => {
      sender ! msg
    }
  }
}

object Meeting {
  trait MeetingMessage
  case object End extends MeetingMessage
  
}