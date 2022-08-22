package committee.nova.danmaku.web

import committee.nova.danmaku.Danmaku
import committee.nova.danmaku.site.api.ISite
import committee.nova.danmaku.web.WebSocketClient.service
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{Channel, ChannelInitializer}
import io.netty.handler.codec.http.websocketx.{BinaryWebSocketFrame, CloseWebSocketFrame, WebSocketClientHandshakerFactory, WebSocketVersion}
import io.netty.handler.codec.http.{EmptyHttpHeaders, HttpClientCodec, HttpObjectAggregator}
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory

import java.net.URI
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

object WebSocketClient {
  val service: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
}

class WebSocketClient(site: ISite, uri: URI) {
  private var channel: Channel = _

  def this(site: ISite) = {
    this(site, URI.create(site.getURI))
  }

  @throws[Exception]
  def open(): Unit = {
    val bootstrap = new Bootstrap()
    val sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
    val group = new NioEventLoopGroup()
    val handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE)
    val handler = new WebSocketClientHandler(handshaker, site)
    bootstrap.group(group).channel(classOf[NioSocketChannel])
      .handler(new ChannelInitializer[SocketChannel] {
        override def initChannel(ch: SocketChannel): Unit = {
          val pipe = ch.pipeline()
          pipe.addLast(
            sslCtx.newHandler(ch.alloc(), uri.getHost, uri.getPort),
            new HttpClientCodec(),
            new HttpObjectAggregator(8192),
            handler
          )
        }
      })
    channel = bootstrap.connect(uri.getHost, uri.getPort).sync().channel()
    handler.getHandshakeFuture.sync()

    site.initMsg(this)
    Danmaku.heartBeatTask = service.scheduleAtFixedRate(() => sendMessage(site.getHeartBeat), site.getHeartBeatInterval, site.getHeartBeatInterval, TimeUnit.MILLISECONDS)
  }

  @throws[InterruptedException]
  def close(): Unit = {
    channel.writeAndFlush(new CloseWebSocketFrame)
    channel.closeFuture.sync
    Danmaku.heartBeatTask.cancel(true)
  }

  def sendMessage(binaryData: ByteBuf): Unit = channel.writeAndFlush(new BinaryWebSocketFrame(binaryData))
}
