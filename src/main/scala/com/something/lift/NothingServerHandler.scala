package com.something.lift

import org.jboss.netty.handler.codec.http._
import org.jboss.netty.channel._
import net.liftweb.common.{Empty, Box}
import net.liftweb.http.provider.servlet.{HTTPResponseServlet, HTTPRequestServlet}
import java.io.{OutputStream, InputStream}
import net.liftweb.http.provider._
import net.liftweb.http._
import java.util.Locale
import net.liftweb.util.{Helpers, Schedule, LoanWrapper}
import java.net.{InetSocketAddress, URI, URL}
import org.jboss.netty.buffer.ChannelBuffers

/**
 * Created by IntelliJ IDEA.
 * User: jordanrw
 * Date: 2/22/12
 * Time: 10:04 PM
 */

class NettyContext extends HTTPContext {
  /**
   * @return - the context path. It always comes first in a request URI. It is
   *           the URI part that represent to context of the request.
   */
  def path: String = "/"

  /**
   * Returns the URL representation of a resource that is mapped by a fully qualified path.
   * The path is considered relative to the root path and it always starts with '/'.
   *
   * @param path - the resource path
   * @return - the URL object of the path
   */
  def resource(path: String): URL = null // FIX ME, this should return the path of the resource // throw new Exception("Implement me, please")

  /**
   * Same as <i>resource</i> but returns an InputStream to read the resource.
   * @param path - the resource path
   * @return InputStream
   */
  def resourceAsStream(path: String): InputStream = throw new Exception("Implement me, please")

  /**
   * @param path
   * @return - the mime type mapped to resource determined by this path.
   */
  def mimeType(path: String): Box[String] = Empty

  /**
   * @param name
   * @return - the value of the init parameter identified by then provided name. Note
   *           that this is not typesfe and you need to explicitely do the casting
   *           when reading this attribute. Returns Empty if this parameter does not exist.
   */
  def initParam(name: String): Box[String] = Empty

  /**
   * @return - a List of Tuple2 consisting of name and value pair of the init parameters
   */
  def initParams: List[(String, String)] = Nil

  /**
   * @param name
   * @return - the value of the context attribute identified by then provided name.
   *           Returns Empty if this parameter does not exist.
   */
  def attribute(name: String): Box[Any] = Empty

  /**
   * @return - a List of Tuple2 consisting of name and value pair of the attributes
   */
  def attributes: List[(String, Any)] = Nil

  /**
   * @param - name
   * @param - value. Any reference. Note that this is not typesfe and you need to explicitely do
   *          the casting when reading this attribute.
   */
  def setAttribute(name: String, value: Any): Unit = {}

  /**
   * @param - name. The name ofthe parameter that needs to be removed.
   */
  def removeAttribute(name: String): Unit = {}
}

object MyTypes {
  type VarProvider = { def apply[T](session: Box[LiftSession], f: => T): T}

}

import MyTypes._

class NothingServerHandler(val nettyContext: NettyContext,
                           val transientVarProvider: VarProvider,
                            val reqVarProvider: VarProvider,
                            val liftLand: LiftServlet) extends SimpleChannelUpstreamHandler with HTTPProvider{
  def context = nettyContext

    bootLift(Empty)

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
    println("MESSAGE RECEIVED")
    val request = e.getMessage.asInstanceOf[HttpRequest]    

    if (HttpHeaders.is100ContinueExpected(request)) {
      send100Continue(e);
    }

    def doNotHandled() {

    }


/**
 * The representation of a HTTP request state
 */
class NettyHttpRequest extends HTTPRequest {

  lazy val nettyLocalAddress = ctx.getChannel.getLocalAddress.asInstanceOf[InetSocketAddress]
  lazy val nettyRemoteAddress = ctx.getChannel.getRemoteAddress.asInstanceOf[InetSocketAddress]

  /**
   * @return - cookies from this request. Nil if there are no cookies.
   */
  def cookies: List[HTTPCookie] = Nil // TODO FIX ME // throw new Exception("Implement me")

  /**
   * The provider associated with this request
   */
  def provider: HTTPProvider = NothingServerHandler.this

  /**
   * @return - HTTP authentication scheme: BASIC, DIGEST etc.
   *           Empty if there is auth header.
   */
  def authType: Box[String] = throw new Exception("Implement me")

  /**
   * Return the header values by the given name.
   *
   * @param name - the header name
   * @return - List[String] or Nil if there is no such header
   */
  def headers(name: String): List[String] = throw new Exception("Implement me")

  /**
   * @return - all header parameters present in this request. Nil if no
   *           headers are used.
   */
  def headers: List[HTTPParam] = Nil //TODO FIXME // throw new Exception("Implement me")

  /**
   * @return - the context path. Similar with HttpServletRequest.getContextPath.
   *           Return "" empty string if your implementation does not support the contept of
   *           context path
   */
  def contextPath: String = ""

  /**
   * @return - the HTTPContext of this service. It does not guarantee that it returns the same
   *           HTTPContext reference upon each invocation but it must guarantee that HTTPContext
   *           reference contains the same information.
   */
  def context: HTTPContext = nettyContext

  /**
   * @return - the MIME type of the body of the request. Empty if this is unknonwn.
   */
  def contentType: Box[String] = Empty //FIXME // throw new Exception("Implement me")

  /**
   * @return - the request URI
   */
  def uri: String =  request.getUri // throw new Exception("Implement me")

  /**
   * @return - the request URL
   */
  def url: String = throw new Exception("Implement me")

  /**
   * @return - the entire query string. Empty if the requst contains no query string
   */
  def queryString: Box[String] =  Box !! uri.splitAt(uri.indexOf("?") + 1)._2 //FIXME throw new Exception("Implement me")

  /**
   * @param name - the parameter name
   * @return - the list of values associated with this name
   */
  def param(name: String): List[String] = throw new Exception("Implement me")

  /**
   * @return - all request parameters
   */
  def params: List[HTTPParam] = throw new Exception("Implement me")

  /**
   * @return - request parameter names
   */
  def paramNames: List[String] = throw new Exception("Implement me")

  /**
   * @return - the HTTP session associated with this request
   */
  def session: HTTPSession = new NettyHttpSession // FIX ME //throw new Exception("Implement me")

  /**
   * Destroy the underlying servlet session
   */
  def destroyServletSession(): Unit = {}

  /**
   * @return the sessionID (if there is one) for this request.  This will *NOT* create
   * a new session if one does not already exist
   */
  def sessionId: Box[String] = Empty

  /**
   * @return - the remote address of the client or the last seen proxy.
   */
  def remoteAddress: String = nettyRemoteAddress.toString

  /**
   * @return - the source port of the client or last seen proxy.
   */
  def remotePort: Int = throw new Exception("Implement me")

  /**
   * @return - the fully qualified name of the client host or last seen proxy
   */
  def remoteHost: String = throw new Exception("Implement me")

  /**
   * @return - the host name of the server
   */
  def serverName: String = nettyLocalAddress.getHostName

  /**
   * @return - the name of the scheme of this request: http, https etc.
   */
  def scheme: String = (new URI(request.getUri)).getScheme

  /**
   * @return - the server port
   */
  def serverPort: Int = nettyLocalAddress.getPort

  /**
   * @return - the HTTP method: GET, POST etc.
   */
  def method: String = request.getMethod.toString

  /**
   * @return true if the underlying container supports suspend/resume idiom.
   */
  def suspendResumeSupport_? : Boolean = false // FIXME this should be trivial

  /**
   * @return - Some[Any] if this is a resumed request, return the state
   *           associated with it.
   */
  def resumeInfo : Option[(Req, LiftResponse)] = None // FIXME trivial support

  /**
   * Suspend the curent request and resume it after a given timeout
   */
  def suspend(timeout: Long): RetryState.Value = throw new Exception("Implement me") // FIXME trivial support

  /**
   * Resume this request
   * @return false if this continuation cannot be resumed
   *         as it is not in pending state.
   */
  def resume(what: (Req, LiftResponse)): Boolean = throw new Exception("Implement me") // FIXME trivial support

  /**
   * @return - the input stream for the request body
   */
  def inputStream: InputStream = throw new Exception("Implement me")

  /**
   * @return true - if the request content is multipart
   */
  def multipartContent_? : Boolean = false // FIXME //throw new Exception("Implement me")

  /**
   * @return - the files uploaded
   */
  def extractFiles: List[ParamHolder] = throw new Exception("Implement me")

  /**
   * @return - the locale forthis request. Empty if there is not language information.
   */
  def locale: Box[Locale] = Empty // FIXME throw new Exception("Implement me")

  /**
   * Sets the character encoding that will be used for request body read
   *
   * @param encoding - the encoding that will be used (e.g. UTF-8)
   */
  def setCharacterEncoding(encoding: String) = throw new Exception("Implement me")

  /**
   * Creates a new HTTPRequest instance as a copy of this one. It is used when
   * snapshots of the current request context is created in order for this request object
   * to be used on different threads (such as asynchronous template fragments processing).
   * The new instance must not keep any reference to the container' instances.
   */
  def snapshot: HTTPRequest = this

  /**
  * The User-Agent of the request
  */
  def userAgent: Box[String] = Box !! request.getHeader(HttpHeaders.Names.USER_AGENT)
}


    class MyResponse extends HTTPResponse {

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

    Schedule(() => {
      try {
        transientVarProvider(Empty,
                                   reqVarProvider(Empty,{


                val httpRequest: HTTPRequest = new NettyHttpRequest // FIXME new HTTPRequestServlet(httpReq, this)
                val httpResponse = new MyResponse

                handleLoanWrappers(service(httpRequest, httpResponse) {
                  doNotHandled()
                })}))
      } catch {
        case excp => excp.printStackTrace
        val response: HttpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    val future = e.getChannel.write(response)
    future.addListener(ChannelFutureListener.CLOSE)
      }
    })
  

    /*
    val response: HttpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    val future = e.getChannel.write(response)
    future.addListener(ChannelFutureListener.CLOSE)
    */
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

class NettyHttpSession extends HTTPSession {

  /**
   * @return - the HTTP session ID
   */
  def sessionId: String = "" // FIXME //throw new Exception("implement me")

  /**
   * Links a LiftSession with this HTTP session. Hence when the HTTP session
   * terminates or times out LiftSession will be destroyed as well.
   *
   * @param liftSession - the LiftSession
   */
  def link(liftSession: LiftSession) {} // TODO: FIXME // throw new Exception("implement me")

  /**
   * The opposite of the <i>link</i>. Hence the LiftSession and the HTTP session no
   * longer needs to be related. It is called when LiftSession is explicitelly terminated.
   *
   * @param liftSession - the LiftSession
   */
  def unlink(liftSession: LiftSession) = throw new Exception("implement me")

  /**
   * @returns - the maximim interval in seconds between client request and the time when
   *            the session will be terminated
   *
   */
  def maxInactiveInterval: Long = 100 // FIX ME //throw new Exception("implement me")

  /**
   * Sets the maximim interval in seconds between client request and the time when
   * the session will be terminated
   *
   * @param interval - the value in seconds
   *
   */
  def setMaxInactiveInterval(interval: Long) = throw new Exception("implement me")

  /**
   * @return - the last time server receivsd a client request for this session
   */
  def lastAccessedTime: Long = System.currentTimeMillis // TODO: FIXME //throw new Exception("implement me")

  /**
   * Sets a value associated with a name for this session
   *
   * @param name - the attribute name
   * @param value - any value
   */
  def setAttribute(name: String, value: Any) = throw new Exception("implement me")

  /**
   * @param name - the attribute name
   * @return - the attribute value associated with this name
   */
  def attribute(name: String): Any = throw new Exception("implement me")

  /**
   * Removes the session attribute having this name
   *
   * @param name - the attribute name
   */
  def removeAttribute(name: String) { throw new Exception("implement me") }

  /**
   * Terminates this session
   */
  def terminate { throw new Exception("implement me") }
}
