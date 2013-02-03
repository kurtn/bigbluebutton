package org.bigbluebutton.live

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.event.Logging
import org.slf4j.LoggerFactory
import akka.actor.Actor
import akka.actor.Props

object BigBlueButton {
//  val log = LoggerFactory.getLogger("BigBlueButton")
  
  println("************ Instantiating BigBlueButton System ***************")
  val config = ConfigFactory.load()
//  val system = ActorSystem("BigBlueButton")
  val system = ActorSystem("BigBlueButton", config.getConfig("myapp1").withFallback(config))
//  val log = Logging(system, this)

  val log = Logging(system.eventStream, "bigbluebutton")
//  val log = Logging(system, classOf[BigBlueButton])
  
  log.debug("************ Initialized Logging ***************")
  
	def hello = {
	  println("************* Hello world! [{}] ****************", config.getNumber("myapp1.my.own.setting"))
	  log.debug("********* my.own.setting = {} ****************", config.getNumber("myapp1.my.own.setting"))
	  
	  val ma = system.actorOf(Props[MyActor], "myActor")
	  ma ! "test"
	  ma ! "unknown message"
	}
}

class MyActor extends Actor {
  val log = Logging(context.system, this)
  override def preStart() = {
    log.debug("Starting")
  }
  
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }
  
  def receive = {
    case "test" => log.info("Received test")
    case x      => log.warning("Received unknown message: {}", x)
  }
}