package com.something.lift

import org.jboss.netty.channel.group.DefaultChannelGroup
import org.jboss.netty.handler.codec.http.{HttpResponseEncoder, HttpRequestDecoder}
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.util.concurrent.Executors
import org.jboss.netty.bootstrap.ServerBootstrap
import java.net.InetSocketAddress
import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory, ChannelFactory}
import net.liftweb.util.Helpers
import net.liftweb.http.{LiftRules, LiftServlet}
import net.liftweb.common.{Empty, Box}

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/22/12
 * Time: 10:04 PM
 */

import MyTypes._

object LiftNettyExampleServer extends App {

  val allChannels = new DefaultChannelGroup("lift-netty-example-server")

  private[this] val factory: ChannelFactory = new NioServerSocketChannelFactory(
    Executors.newCachedThreadPool(),
    Executors.newCachedThreadPool()
  )

  private def findObject(cls: String): Box[AnyRef] =
    Helpers.tryo[Class[_]](Nil)(Class.forName(cls + "$")).flatMap {
      c =>
        Helpers.tryo {
          val field = c.getField("MODULE$")
          field.get(null)
        }
    }

  val transientVarProvider: VarProvider =
    findObject("net.liftweb.http.TransientRequestVarHandler").open_!.asInstanceOf[VarProvider]
  val reqVarProvider: VarProvider =
    findObject("net.liftweb.http.RequestVarHandler").open_!.asInstanceOf[VarProvider]

  val nettyContext = new HTTPNettyContext

  val liftLand = new LiftServlet(nettyContext)

  LiftRules.setContext(nettyContext)

  private[this] val bootstrap = new ServerBootstrap(factory)

  val handler = new LiftChannelHandler(nettyContext, transientVarProvider, reqVarProvider, liftLand)

  bootstrap.setPipelineFactory(new ChannelPipelineFactory {
    def getPipeline: ChannelPipeline = Channels.pipeline(
      new HttpRequestDecoder,
      new HttpResponseEncoder,
      handler
    )
  })

  bootstrap.setOption("child.tcpNoDelay", true)
  bootstrap.setOption("child.keepAlive", true)

  val boundChannel = bootstrap.bind(new InetSocketAddress(8080))
  println("Lift-Netty Server Started")

  allChannels.add(boundChannel)

}