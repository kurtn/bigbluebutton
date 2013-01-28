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

	"An MeetingActor" should {
		"Respond with the same message it receives" in {
			within(1000 millis) {
				val meetingRef = system.actorOf(Props[MeetingActor])
				meetingRef ! "test"
				expectMsg("test")
			}
		}
	}
	
	
}