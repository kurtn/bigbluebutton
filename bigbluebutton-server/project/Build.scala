import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "bigbluebutton-server"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "org.scalatest" %% "scalatest" % "1.8" % "test",
      "com.typesafe.akka" % "akka-testkit" % "2.0.2",
      "com.typesafe.akka" % "akka-actor" % "2.0.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
      retrieveManaged := true,
      testOptions in Test := Nil
    )

}
