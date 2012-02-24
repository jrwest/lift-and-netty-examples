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

  /**
   * Add cookies to the response
   *
   * @param cookies - the list of response cookies
   */
  def addCookies(cookies: List[HTTPCookie]) {} // FIXME // throw new Exception("Implement me")

  /**
   * Encodes the URL such that it adds the session ID if it is necessary to the URL.
   * It is implementation specific detail how/if to add the session informations. This will be used
   * for URL rewriting purposes.
   *
   * @param url - the URL that needs to be analysed
   */
  def encodeUrl(url: String): String = url

  /**
   * Add a list of header parameters to the response object
   *
   * @param headers - the list of headers
   */
  def addHeaders(headers: List[HTTPParam]) {} // FIXME // throw new Exception("Implement me")

  /**
   * Sets the HTTP response status
   *
   * @param status - the HTTP status
   */
  def setStatus(status: Int) = nettyResponse.setStatus(HttpResponseStatus.valueOf(status)) //throw new Exception("Implement me")

  /**
   * Returns the HTTP response status that has been set with setStatus
   * */
  def getStatus: Int = nettyResponse.getStatus.getCode

  /**
   * Sets the HTTP response status
   *
   * @param status - the HTTP status
   * @param reason - the HTTP reason phrase
   */
  def setStatusWithReason(status: Int, reason: String) = throw new Exception("Implement me")

  /**
   * @return - the OutputStream that can be used to send down o the client the response body.
   */
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