package org.bigbluebutton.live

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.event.Logging
//import akka.event.LogSource
import org.slf4j.LoggerFactory

object BigBlueButton {
//  val log = LoggerFactory.getLogger("bigBlueButton")
  
  println("************ Instantiating BigBlueButton System ***************")
  val config = ConfigFactory.load()
//  val system = ActorSystem("BigBlueButton")
  val system = ActorSystem("BigBlueButton", config.getConfig("myapp1").withFallback(config))
//  val log = Logging(system.eventStream)

  val log = Logging(system.eventStream, "bigbluebutton")
//  val log = Logging(system, classOf[BigBlueButton])
  
  log.debug("************ Initialized Logging ***************")
  
	def hello = {
	  println("************* Hello world! [{}] ****************", config.getNumber("myapp1.my.own.setting"))
	  log.debug("********* my.own.setting = {} ****************", config.getNumber("myapp1.my.own.setting"))
	}
}