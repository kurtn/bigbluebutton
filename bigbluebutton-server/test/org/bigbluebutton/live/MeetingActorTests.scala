package org.bigbluebutton.live

import org.scalatest.{ Suite, BeforeAndAfterAll, WordSpec }
import org.scalatest.matchers.MustMatchers
import akka.testkit.TestKit
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.testkit.ImplicitSender
import akka.util.duration._
import org.bigbluebutton.test.util.StopSystemAfterAll

class MeetingActorTests extends TestKit(ActorSystem("testsystem"))
	with WordSpec
	with MustMatchers
	with ImplicitSender
	with StopSystemAfterAll {

	"A MeetingActor" should {
		"Respond with an auth token when a new user joins the meeting" in {
			within(1000 millis) {
				val meetingRef = system.actorOf(Props[MeetingActor])
				meetingRef ! new Meeting.Join(new Meeting.UserInfo("user1", "id1", "id2", "moderator"))
				expectMsg(Meeting.Joined("12345"))
			}
		}
	}

	"A MeetingActor" should {
		"Respond with the user info when a user enters the meeting" in {
			within(1000 millis) {
				val meetingRef = system.actorOf(Props[MeetingActor])
				meetingRef ! new Meeting.UserEnter("12345")
				expectMsg("OK")
			}
		}
	}
	
}