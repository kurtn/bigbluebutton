package org.bigbluebutton.live

import org.red5.server.adapter.MultiThreadedApplicationAdapter
import org.red5.server.api.scope.IScope
import org.slf4j.Logger
import org.red5.logging.Red5LoggerFactory

class BigBlueButtonApp extends MultiThreadedApplicationAdapter {
//  override val log: Logger = Red5LoggerFactory.getLogger(classOf[BigBlueButtonApp], "bigbluebutton");

  override def appStart(app: IScope): Boolean = {
    println("***************************************** BigBlueButton Starting ************************** ")
    val bbb = BigBlueButton
    bbb.hello
    super.appStart(app)
  }
}