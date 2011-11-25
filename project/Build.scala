import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "nowtify"
    val appVersion      = "0.1"

    val appDependencies = Seq(
		"nu.validator.htmlparser" % "htmlparser" % "1.2.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies).settings(defaultScalaSettings:_*).settings(
      // Add your own project settings here      
    )

}
