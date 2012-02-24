package com.something.lift

import org.jboss.netty.buffer.ChannelBuffers
import net.liftweb.http.provider.{HTTPParam, HTTPCookie, HTTPResponse}
import java.io.OutputStream
import org.jboss.netty.channel.{ChannelHandlerContext, ChannelFutureListener}
import org.jboss.netty.handler.codec.http._
import net.liftweb.http.LiftRules

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/23/12
 * Time: 8:41 PM
 */

/**
 * Representation of the HTTPResponseStatus
 *
 * @param ctx - the netty channel handler context
 * @param keepAlive - if true the channel's connection will remain open after response is written, otherwise it will be closed.
 *
 */
class NettyHttpResponse(ctx: ChannelHandlerContext, keepAlive: Boolean) extends HTTPResponse {

  lazy val nettyResponse: HttpResponse = {
    val r = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    r.setContent(ChannelBuffers.dynamicBuffer(1024)) // TODO this is just some random choice, do something more intelligent?
    r
  }
  
  private var cookies = List[HTTPCookie]()

  def addCookies(cks: List[HTTPCookie]) {
    cookies ++= cks
  }

  // FIXME should add session id to url if/when sessions are supported but session id not set in cookies
  def encodeUrl(url: String): String = url

  def addHeaders(headers: List[HTTPParam]) {
    val appearOnce = Set(LiftRules.overwrittenReponseHeaders.vend.map(_.toLowerCase):_*)
    for (h <- headers;
         value <- h.values) {
      if (appearOnce.contains(h.name.toLowerCase)) nettyResponse.setHeader(h.name, value)
      else nettyResponse.addHeader(h.name, value)
    }
  }

  def setStatus(status: Int) {
    nettyResponse.setStatus(HttpResponseStatus.valueOf(status))
  }

  def getStatus: Int = nettyResponse.getStatus.getCode

  // TODO: it is possible to implement this method although netty has no equiv. but more intelligent content buffer management is needed
  def setStatusWithReason(status: Int, reason: String) = throw new Exception("not implemented, there is no equivalent in netty")

  // TODO better flush
  def outputStream: OutputStream = new OutputStream {

    def write(i: Int) {
      nettyResponse.getContent.writeByte(i)
    }

    override def write(bytes: Array[Byte]) {
      nettyResponse.getContent.writeBytes(bytes)
    }
    
    override def write(bytes: Array[Byte], offset: Int, len: Int) {
      nettyResponse.getContent.writeBytes(bytes, offset, len)
    }

    override def flush() {
      if (cookies.length > 0) writeCookiesToResponse()
      val future = ctx.getChannel.write(nettyResponse)
      if (!keepAlive) future.addListener(ChannelFutureListener.CLOSE)
    }
  }
  
  private def writeCookiesToResponse() {
    val encoder = new CookieEncoder(true)

    for (c <- cookies) {
      val cookie = new DefaultCookie(c.name, c.value openOr null)
      c.domain foreach (cookie.setDomain(_))
      c.path foreach (cookie.setPath(_))
      c.maxAge foreach (cookie.setMaxAge(_))
      c.version foreach (cookie.setVersion(_))
      c.secure_? foreach (cookie.setSecure(_))
      c.httpOnly foreach (cookie.setHttpOnly(_))
    }

    addHeaders(HTTPParam(HttpHeaders.Names.SET_COOKIE, encoder.encode) :: Nil)
  }
}