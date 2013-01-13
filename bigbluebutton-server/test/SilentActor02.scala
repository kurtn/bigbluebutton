import org.scalatest.{ Suite, BeforeAndAfterAll, WordSpec }
import org.scalatest.matchers.MustMatchers
import akka.testkit.TestKit
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

object SilentActor02Protocol {                                         
  case class SilentMessage(data:String)                                
}

class SilentActor02 extends Actor {
  import SilentActor02Protocol._
  var internalState = Vector[String]()

  def receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data                            
  }

  def state = internalState
}


object SilentActor03Protocol {
  case class SilentMessage(data:String)
  case class GetState(receiver:ActorRef)   
}

class SilentActor03 extends Actor {
  import SilentActor03Protocol._
  var internalState = Vector[String]()

  def receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data
    case GetState(receiver) => receiver ! internalState   
 }
}

class SilentActor01Test extends TestKit(ActorSystem("testsystem"))
	with WordSpec
	with MustMatchers
	with StopSystemAfterAll {

  "A Silent Actor" must {
    "change state when it receives a message, single threaded" in {
	  import SilentActor02Protocol._ 
	  val silentActor = TestActorRef[SilentActor02] 
	  silentActor ! SilentMessage("whisper")
	  silentActor.underlyingActor.state must (contain("whisper")) 
    }
    "change state when it receives a message, multi-threaded" in {
	  import SilentActor03Protocol._ 
	  val silentActor = system.actorOf(Props[SilentActor03], "s3") 
	  silentActor ! SilentMessage("whisper1")
	  silentActor ! SilentMessage("whisper2")
	  silentActor ! GetState(testActor)
	  expectMsg(Vector("whisper1", "whisper2"))
    }
  }
}