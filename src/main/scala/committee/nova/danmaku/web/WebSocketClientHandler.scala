package committee.nova.danmaku.web

import committee.nova.danmaku.Danmaku
import committee.nova.danmaku.site.api.ISite
import io.netty.channel.{ChannelFuture, ChannelHandlerContext, ChannelPromise, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx.{WebSocketClientHandshaker, WebSocketFrame}
import io.netty.util.CharsetUtil

class WebSocketClientHandler(handshaker: WebSocketClientHandshaker, site: ISite) extends SimpleChannelInboundHandler[AnyRef] {
  private var handshakeFuture: ChannelPromise = _

  override def channelRead0(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
    val ch = ctx.channel()
    if (!handshaker.isHandshakeComplete) {
      handshaker.finishHandshake(ch, msg.asInstanceOf[FullHttpResponse])
      Danmaku.logger.info("Websocket client connected!")
      handshakeFuture.setSuccess()
      return
    }

    msg match {
      case response: FullHttpResponse => throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content="
        + response.content().toString(CharsetUtil.UTF_8) + ')');
      case frame: WebSocketFrame => site.handMsg(frame)
      case _ =>
    }
  }

  override def handlerAdded(ctx: ChannelHandlerContext): Unit = handshakeFuture = ctx.newPromise()

  override def channelActive(ctx: ChannelHandlerContext): Unit = handshaker.handshake(ctx.channel())

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    Danmaku.logger.info("WebSocket Client disconnected!")
    Danmaku.heartBeatTask.cancel(true)
  }

  def getHandshakeFuture: ChannelFuture = handshakeFuture

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    if (!handshakeFuture.isDone) handshakeFuture.setFailure(cause)
    ctx.close()
  }
}
