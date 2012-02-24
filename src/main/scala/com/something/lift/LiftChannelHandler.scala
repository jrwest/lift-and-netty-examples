package com.something.lift

import org.jboss.netty.handler.codec.http._
import org.jboss.netty.channel._
import net.liftweb.common.{Empty, Box}
import net.liftweb.http.provider._
import net.liftweb.http._
import net.liftweb.util.{Helpers, Schedule, LoanWrapper}

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/22/12
 * Time: 10:04 PM
 */

object MyTypes {
  type VarProvider = { def apply[T](session: Box[LiftSession], f: => T): T}

}

import MyTypes._

class LiftChannelHandler(val nettyContext: HTTPNettyContext,
                           val transientVarProvider: VarProvider,
                           val reqVarProvider: VarProvider,
                           val liftLand: LiftServlet) extends SimpleChannelUpstreamHandler with HTTPProvider {

  bootLift(Empty)

  def context = nettyContext

  /**
   * Wrap the loans around the incoming request
   */
  private def handleLoanWrappers[T](f: => T): T = {
    /*

    FIXME -- this is a 2.5-ism
    val wrappers = LiftRules.allAround.toList

    def handleLoan(lst: List[LoanWrapper]): T = lst match {
      case Nil => f
      case x :: xs => x(handleLoan(xs))
    }

    handleLoan(wrappers)
    */
    f
  }

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    val request = e.getMessage.asInstanceOf[HttpRequest]    

    if (HttpHeaders.is100ContinueExpected(request)) {
      send100Continue(e);
    }

    def doNotHandled() {}

    Schedule(() => {
      try {
        transientVarProvider(Empty,
                                   reqVarProvider(Empty,{


                val httpRequest: HTTPRequest = new NettyHttpRequest(request, ctx, nettyContext, this) // FIXME new HTTPRequestServlet(httpReq, this)
                val httpResponse = new NettyHttpResponse(ctx)

                handleLoanWrappers(service(httpRequest, httpResponse) {
                  doNotHandled()
                })}))
      } catch {
        case excp => {
          excp.printStackTrace
          // FIXME placeholder code, do something correct here
          val response: HttpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
          val future = e.getChannel.write(response)
          future.addListener(ChannelFutureListener.CLOSE)
        }
      }
    })
  

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

