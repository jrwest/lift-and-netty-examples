import sbt._
import Keys._
import com.github.siasia._
import PluginKeys._
import WebPlugin._
import WebappPlugin._

object LiftProjectBuild extends Build {
  override lazy val settings = super.settings ++ buildSettings
  
  lazy val buildSettings = Seq(
    organization := "com.something",
    version      := "0.1-SNAPSHOT",
    scalaVersion := "2.9.1")
  
  
  lazy val liftQuickstart = Project(
    id = "lift-netty",
    base = file("."),
    settings = defaultSettings ++ webSettings)
    
  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    name := "lift-netty",
    resolvers ++= Seq(
      "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases", 
      "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"),
    
    libraryDependencies ++= {
        val liftVersion = "2.4"
        Seq(
	      "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
	      "org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container",
	      "ch.qos.logback" % "logback-classic" % "1.0.0" % "compile",
        "org.jboss.netty" % "netty" % "3.2.7.Final")
    }
    //,

    // compile options
//    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
//    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

    // show full stack traces
//    testOptions in Test += Tests.Argument("-oF")
  )
}
