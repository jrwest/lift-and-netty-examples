package com.something.lift

import net.liftweb.http.provider.HTTPSession
import net.liftweb.http.LiftSession

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/23/12
 * Time: 9:26 PM
 */


class NettyHttpSession extends HTTPSession {

  // FIXME return a real session id when supported
  def sessionId: String = ""

  // FIXME implement me for realz
  def link(liftSession: LiftSession) {}

  // FIXME
  def unlink(liftSession: LiftSession) = throw new Exception("implement me")

  // FIXME this is just an arbitrary value to get things running
  def maxInactiveInterval: Long = 100

  // FIXME
  def setMaxInactiveInterval(interval: Long) = throw new Exception("implement me")

  // FIXME return real time when sessions are supported
  def lastAccessedTime: Long = System.currentTimeMillis

  // FIXME
  def setAttribute(name: String, value: Any) = throw new Exception("implement me")

  // FIXME
  def attribute(name: String): Any = throw new Exception("implement me")

  // FIXME
  def removeAttribute(name: String) { throw new Exception("implement me") }

  // FIXME
  def terminate { throw new Exception("implement me") }
}