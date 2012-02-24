package com.something.lift

import org.jboss.netty.handler.codec.http.{HttpResponseStatus, HttpVersion, DefaultHttpResponse, HttpResponse}
import org.jboss.netty.buffer.ChannelBuffers
import net.liftweb.http.provider.{HTTPParam, HTTPCookie, HTTPResponse}
import java.io.OutputStream
import org.jboss.netty.channel.{ChannelHandlerContext, ChannelFutureListener}

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/23/12
 * Time: 8:41 PM
 */

class NettyHttpResponse(ctx: ChannelHandlerContext) extends HTTPResponse {

  lazy val nettyResponse: HttpResponse = {
    val r = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    r.setContent(ChannelBuffers.dynamicBuffer(1024)) // TODO this is just some random choice, do something more intelligent?
    r
  }

  // FIXME actually add cookies to the response
  def addCookies(cookies: List[HTTPCookie]) {}

  // FIXME add session id if/when sessions are supported?
  def encodeUrl(url: String): String = url

  // FIXME actually set headers
  def addHeaders(headers: List[HTTPParam]) {}

  def setStatus(status: Int) = nettyResponse.setStatus(HttpResponseStatus.valueOf(status))

  def getStatus: Int = nettyResponse.getStatus.getCode

  // FIXME
  def setStatusWithReason(status: Int, reason: String) = throw new Exception("Implement me")

  // TODO make better: ovverride other write methods, better flush
  def outputStream: OutputStream = new OutputStream {
    // TODO: there is a probably a better impl, by override the other write methods.
    def write(i: Int) {
      nettyResponse.getContent.writeByte(i)
    }

    override def flush() {
      val future = ctx.getChannel.write(nettyResponse)
      future.addListener(ChannelFutureListener.CLOSE)
    }
  }
}