package com.something.lift

import org.jboss.netty.handler.codec.http._
import org.jboss.netty.channel._


/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/22/12
 * Time: 10:04 PM
 */

class NothingServerHandler extends SimpleChannelUpstreamHandler {

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    println("MESSAGE RECEIVED")
    val request = e.getMessage.asInstanceOf[HttpRequest]
    println(request)

    if (HttpHeaders.is100ContinueExpected(request)) {
      send100Continue(e);
    }
    
    val response: HttpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    val future = e.getChannel.write(response)
    future.addListener(ChannelFutureListener.CLOSE)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
    println(e.getCause)
    e.getChannel.close()
  }

  private def send100Continue(e: MessageEvent) {
    val response: HttpResponse  = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE)
    e.getChannel.write(response)
  }


}