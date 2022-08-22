package committee.nova.danmaku.site.api

import committee.nova.danmaku.config.api.IConfig
import committee.nova.danmaku.web.WebSocketClient
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.websocketx.WebSocketFrame

trait ISite {
  def getURI: String

  def initMsg(client: WebSocketClient): Unit

  def getHeartBeatInterval: Long

  def getHeartBeat: ByteBuf

  @throws[Exception] def handMsg(frame: WebSocketFrame): Unit

  def getConfig: IConfig
}
