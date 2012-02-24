package com.something.lift

import net.liftweb.http.provider._
import net.liftweb.common.{Empty, Box}
import java.io.InputStream
import net.liftweb.http.{ParamHolder, LiftResponse, Req}
import java.util.Locale
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpHeaders}
import java.net.{URI, InetSocketAddress}


/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/23/12
 * Time: 8:31 PM 
 */

/**
 * The representation of a HTTP request state
 */
class NettyHttpRequest(request: HttpRequest,
                       channelContext: ChannelHandlerContext,
                       nettyContext: HTTPNettyContext,
                       val provider: HTTPProvider) extends HTTPRequest {

  lazy val nettyLocalAddress = channelContext.getChannel.getLocalAddress.asInstanceOf[InetSocketAddress]
  lazy val nettyRemoteAddress = channelContext.getChannel.getRemoteAddress.asInstanceOf[InetSocketAddress]

  val contextPath = ""

  // FIXME actually return the list of cookies
  def cookies: List[HTTPCookie] = Nil

  // FIXME
  def authType: Box[String] = throw new Exception("Implement me")

  // FIXME
  def headers(name: String): List[String] = throw new Exception("Implement me")

  // FIXME actually return the list of headers
  def headers: List[HTTPParam] = Nil //TODO FIXME // throw new Exception("Implement me")

  def context: HTTPContext = nettyContext

  // FIXME actually return the content type
  def contentType: Box[String] = Empty //FIXME // throw new Exception("Implement me")

  def uri: String =  request.getUri

  // FIXME
  def url: String = throw new Exception("Implement me")

  def queryString: Box[String] =  Box !! uri.splitAt(uri.indexOf("?") + 1)._2

  // FIXME
  def param(name: String): List[String] = throw new Exception("Implement me")

  // FIXME
  def params: List[HTTPParam] = throw new Exception("Implement me")

  // FIXME
  def paramNames: List[String] = throw new Exception("Implement me")

  // FIXME the session is fake
  def session: HTTPSession = new NettyHttpSession

  // not needed for netter
  def destroyServletSession() {}

  // FIXME implement once sessions are supported
  def sessionId: Box[String] = Empty

  def remoteAddress: String = nettyRemoteAddress.toString

  def remotePort: Int = nettyRemoteAddress.getPort

  def remoteHost: String = nettyRemoteAddress.getHostName

  def serverName: String = nettyLocalAddress.getHostName

  def scheme: String = (new URI(request.getUri)).getScheme

  def serverPort: Int = nettyLocalAddress.getPort

  def method: String = request.getMethod.toString

  // FIXME not really implemented, @dpp made it false when we started, left comment, "this should be trivial"
  def suspendResumeSupport_? : Boolean = false

  // FIXME not really implemented, @dpp made it None when we started, left comment, "trivial support"
  def resumeInfo : Option[(Req, LiftResponse)] = None

  // FIXME trivial support
  def suspend(timeout: Long): RetryState.Value = throw new Exception("Implement me")

  // FIXME trivial support
  def resume(what: (Req, LiftResponse)): Boolean = throw new Exception("Implement me")

  // FIXME
  def inputStream: InputStream = throw new Exception("Implement me")

  // FIXME actually detect multipart content
  def multipartContent_? : Boolean = false

  // FIXME
  def extractFiles: List[ParamHolder] = throw new Exception("Implement me")

  // FIXME actually detect locale
  def locale: Box[Locale] = Empty

  // FIXME
  def setCharacterEncoding(encoding: String) = throw new Exception("Implement me")

  /**
   * Creates a new HTTPRequest instance as a copy of this one. It is used when
   * snapshots of the current request context is created in order for this request object
   * to be used on different threads (such as asynchronous template fragments processing).
   * The new instance must not keep any reference to the container' instances.
   */
  // FIXME actually copy instance
  def snapshot: HTTPRequest = this

  def userAgent: Box[String] = Box !! request.getHeader(HttpHeaders.Names.USER_AGENT)
}