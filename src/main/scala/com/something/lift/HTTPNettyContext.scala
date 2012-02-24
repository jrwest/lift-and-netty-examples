package com.something.lift

import net.liftweb.http.provider.HTTPContext
import java.net.URL
import java.io.InputStream
import net.liftweb.common.{Empty, Box}

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/23/12
 * Time: 8:07 PM
 */

class HTTPNettyContext extends HTTPContext {

  // netty has no context, so we assume root
  def path: String = ""

  // FIXME this should actually return the path to the resource @see{net.liftweb.http.provider.HTTPContext}
  def resource(path: String): URL = null

  // FIXME @see{net.liftweb.http.provider.HTTPContext}
  def resourceAsStream(path: String): InputStream = throw new Exception("Not Yet Implemented")

  // FIXME  @see{net.liftweb.http.provider.HTTPContext}
  def mimeType(path: String): Box[String] = Empty

  /* All Methods Below Should Not Be Needed for Netty */

  def initParam(name: String): Box[String] = Empty

  def initParams: List[(String, String)] = Nil

  def attribute(name: String): Box[Any] = Empty

  def attributes: List[(String, Any)] = Nil

  def setAttribute(name: String, value: Any) { }

  def removeAttribute(name: String) { }
}