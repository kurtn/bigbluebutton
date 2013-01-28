import org.scalatest.{ Suite, BeforeAndAfterAll, WordSpec }
import org.scalatest.matchers.MustMatchers
import akka.testkit.TestKit
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import util.Random
import akka.util.duration._
import akka.testkit.TestProbe
import akka.testkit.ImplicitSender
import org.bigbluebutton.test.util.StopSystemAfterAll

/**
 * An Actor that echoes everything you send to it
 */
class EchoActor extends Actor {
  def receive = {
    case msg => {
      sender ! msg
    }
  }
}

/**
 * An Actor that forwards every message to a next Actor
 */
class ForwardingActor(next: ActorRef) extends Actor {
  def receive = {
    case msg => {
      next ! msg
    }
  }
}

/**
 * An Actor that only forwards certain messages to a next Actor
 */
class FilteringActor(next: ActorRef) extends Actor {
  def receive = {
    case msg: String => {
      next ! msg
    }
    case _ => None
  }
}

/**
 * An actor that sends a sequence of messages with a random head list, an interesting value and a random tail list
 * The idea is that you would like to test that the interesting value is received and that you cant be bothered with the rest
 */
class SequencingActor(next: ActorRef, head: List[String], tail: List[String]) extends Actor {
  def receive = {
    case msg => {
      System.out.println("Rx " + msg)
      head map (next ! _)
      next ! msg
      tail map (next ! _)
    }
  }
}

class TestKitUsageSpec extends TestKit(ActorSystem("testsystem"))
	with WordSpec
	with MustMatchers
	with ImplicitSender
	with StopSystemAfterAll {
  
  
  "An EchoActor" should {
    "Respond with the same message it receives" in {
      within(1000 millis) {
        val echoRef = system.actorOf(Props[EchoActor])
        echoRef ! "test"
        expectMsg("test")
      }
    }
  }
  "A ForwardingActor" should {
    "Forward a message it receives" in {
      within(1000 millis) {
        val probe = TestProbe()
        val forwardRef = system.actorOf(Props(new ForwardingActor(probe.ref)))
        forwardRef ! "test"
        probe.expectMsg("test")
      }
    }
  }
  "A FilteringActor" should {
    "Filter all messages, except expected messagetypes it receives" in {
      var messages = List[String]()
      within(1000 millis) {
        val probe = TestProbe()
        val filterRef = system.actorOf(Props(new FilteringActor(probe.ref)))
        filterRef ! "test"
        probe.expectMsg("test")
        filterRef ! 1
        probe.expectNoMsg
        filterRef ! "some"
        filterRef ! "more"
        filterRef ! 1
        filterRef ! "text"
        filterRef ! 1

        receiveWhile(500 millis) {
          case msg: String => messages = msg :: messages
        }
      }
//      messages.length should be(3)
//      messages.reverse should be(List("some", "more", "text"))
    }
  }
  "A SequencingActor" should {
    "receive an interesting message at some point " in {
      within(1000 millis) {
    	  val randomHead = Random.nextInt(6)
          val randomTail = Random.nextInt(10)
          val headList = List().padTo(randomHead, "0")
          val tailList = List().padTo(randomTail, "1")
          val probe = TestProbe()
          val seqRef = system.actorOf(Props(new SequencingActor(probe.ref, headList, tailList)))
  
        seqRef ! "something"
        probe.ignoreMsg {
          case msg: String => {
            println("ignoring [" + msg + "]")
            msg != "something"
          }
        }
    	probe.expectMsg("something")
    	probe.ignoreMsg {
    	  case msg: String =>  msg == "1"
        }
  //      probe.expectNoMsg
      }
    }
  }
}