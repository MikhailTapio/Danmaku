package committee.nova.danmaku.site.impl

import com.google.gson.{Gson, GsonBuilder}
import committee.nova.danmaku.Danmaku
import committee.nova.danmaku.api.listener.UpdatePopularInfoListener
import committee.nova.danmaku.config.impl.BilibiliConfig
import committee.nova.danmaku.site.api.ISite
import committee.nova.danmaku.site.impl.BilibiliSite._
import committee.nova.danmaku.site.util.{MessageDeserializer, RoomId}
import committee.nova.danmaku.utils.{DanmakuManager, ZLib}
import committee.nova.danmaku.web.WebSocketClient
import io.netty.buffer.{ByteBuf, Unpooled}
import net.minecraft.util.text.TextComponentTranslation
import org.apache.commons.io.IOUtils
import org.dimdev.riftloader.RiftLoader

import java.nio.charset.StandardCharsets

object BilibiliSite {
  private val URI = "wss://broadcastlv.chat.bilibili.com:443/sub"
  private val HEART_BEAT_INTERVAL = 30000L

  private val HEADER_LENGTH = 16
  private val SEQUENCE_ID = 1

  private val PACKET_LENGTH_OFFSET = 0
  private val PROTOCOL_VERSION_OFFSET = 6
  private val OPERATION_OFFSET = 8
  private val BODY_OFFSET = 16

  private val JSON_PROTOCOL_VERSION = 0
  private val POPULAR_PROTOCOL_VERSION = 1
  private val BUFFER_PROTOCOL_VERSION = 2

  private val HEART_BEAT_OPERATION = 2
  private val POPULAR_OPERATION = 3
  private val MESSAGE_OPERATION = 5
  private val ENTER_ROOM_OPERATION = 7
}

class BilibiliSite(config: BilibiliConfig, gson: Gson) extends ISite {
  def this(config: BilibiliConfig) = this(config, new GsonBuilder().registerTypeAdapter(classOf[String], new MessageDeserializer(config)).create())

  override def getURI: String = URI

  override def getHeartBeatInterval: Long = HEART_BEAT_INTERVAL

  override def initMsg(client: WebSocketClient): Unit = {
    val id: Int = RoomId.getRealRoomId(config.getRoom.getId)
    DanmakuManager.sendDanmaku(new TextComponentTranslation("msg.danmaku.access." + (if (id == -1) "failure" else "success")))
    val message: Array[Byte] = String.format("{\"roomid\": %d}", id).getBytes(StandardCharsets.UTF_8)
    val buf: ByteBuf = Unpooled.buffer
    buf.writeInt(HEADER_LENGTH + message.length)
    buf.writeShort(HEADER_LENGTH)
    buf.writeShort(BUFFER_PROTOCOL_VERSION)
    buf.writeInt(ENTER_ROOM_OPERATION)
    buf.writeInt(SEQUENCE_ID)
    buf.writeBytes(message)
    client.sendMessage(buf)
  }


  def getHeartBeat: ByteBuf = {
    val buf = Unpooled.buffer
    buf.writeInt(HEADER_LENGTH)
    buf.writeShort(HEADER_LENGTH)
    buf.writeShort(BUFFER_PROTOCOL_VERSION)
    buf.writeInt(HEART_BEAT_OPERATION)
    buf.writeInt(SEQUENCE_ID)
    buf
  }

  import io.netty.buffer.ByteBuf
  import io.netty.handler.codec.http.websocketx.{BinaryWebSocketFrame, WebSocketFrame}

  @throws[Exception]
  override def handMsg(webSocketFrame: WebSocketFrame): Unit = {
    if (webSocketFrame.isInstanceOf[BinaryWebSocketFrame]) {
      val data = webSocketFrame.content
      val protocol = data.getShort(PROTOCOL_VERSION_OFFSET)
      protocol match {
        case JSON_PROTOCOL_VERSION =>
          return
        case POPULAR_PROTOCOL_VERSION =>
          handPopularMessage(data)
          return
        case BUFFER_PROTOCOL_VERSION =>
          handBufferMessage(data)
          return
        case _ =>
      }
    }
  }

  import com.google.gson.JsonSyntaxException
  import committee.nova.danmaku.utils.BilibiliMsgSplit

  import java.nio.charset.StandardCharsets
  import java.util

  private def handJsonMessage(data: ByteBuf): Unit = {
    val packetLength = data.getInt(PACKET_LENGTH_OFFSET)
    val message = data.getCharSequence(BODY_OFFSET, packetLength - BODY_OFFSET, StandardCharsets.UTF_8)
    Danmaku.logger.info(message)
  }

  private def handPopularMessage(data: ByteBuf): Unit = {
  }

  @throws[Exception]
  private def handBufferMessage(data: ByteBuf): Unit = {
    val packetLength = data.getInt(PACKET_LENGTH_OFFSET)
    val operation = data.getInt(OPERATION_OFFSET)
    if (operation == POPULAR_OPERATION) {
      RiftLoader.instance.getListeners(classOf[UpdatePopularInfoListener]).forEach(l => l.infoUpdated(data.getInt(BODY_OFFSET)))
      return
    }
    if (operation == MESSAGE_OPERATION) {
      val uncompressedData = new Array[Byte](packetLength - BODY_OFFSET)
      data.getBytes(BODY_OFFSET, uncompressedData)
      val decompressData = ZLib.decompress(uncompressedData)
      val msgBytes = util.Arrays.copyOfRange(decompressData, BODY_OFFSET, decompressData.length)
      val message = BilibiliMsgSplit.split(IOUtils.toString(msgBytes, StandardCharsets.UTF_8.toString))
      for (msg <- message) {
        handStringMessage(msg)
      }
    }
  }

  private def handStringMessage(message: String): Unit = {
    try {
      val str = gson.fromJson(message, classOf[String])
      if (str != null) DanmakuManager.sendDanmaku(str)
    } catch {
      case _: JsonSyntaxException =>
    }
  }

  override def getConfig: BilibiliConfig = config
}
