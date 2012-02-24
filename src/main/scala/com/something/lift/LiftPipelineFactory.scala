package com.something.lift

import org.jboss.netty.handler.codec.http.{HttpResponseEncoder, HttpRequestDecoder}
import net.liftweb.common.Box
import net.liftweb.util.Helpers
import net.liftweb.http.{LiftRules, LiftServlet}
import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory}


/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/23/12
 * Time: 9:55 PM
 */

import MyTypes._
class LiftPipelineFactory extends ChannelPipelineFactory {

  // TODO make better, extensible, etc
  def getPipeline: ChannelPipeline = Channels.pipeline(
    new HttpRequestDecoder,
    new HttpResponseEncoder,
    handler
  )

  val transientVarProvider: VarProvider =
    findObject("net.liftweb.http.TransientRequestVarHandler").open_!.asInstanceOf[VarProvider]
  val reqVarProvider: VarProvider =
    findObject("net.liftweb.http.RequestVarHandler").open_!.asInstanceOf[VarProvider]

  val nettyContext = new HTTPNettyContext

  val liftLand = new LiftServlet(nettyContext)

  LiftRules.setContext(nettyContext)

  val handler = new LiftChannelHandler(nettyContext, transientVarProvider, reqVarProvider, liftLand)

  private def findObject(cls: String): Box[AnyRef] =
    Helpers.tryo[Class[_]](Nil)(Class.forName(cls + "$")).flatMap {
      c =>
        Helpers.tryo {
          val field = c.getField("MODULE$")
          field.get(null)
        }
    }

}