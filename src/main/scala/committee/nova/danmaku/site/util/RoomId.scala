package committee.nova.danmaku.site.util

import org.apache.commons.io.IOUtils

import java.io.IOException
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

object RoomId {
  private val INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init"
  private val EXTRACT_ROOM_ID = Pattern.compile("\"room_id\":(\\d+),")

  def getRealRoomId(roomId: Int): Int = {
    var realRoomId: String = null
    try {
      val url = new URI(INIT_URL + "?id=" + roomId)
      val data = IOUtils.toString(url, StandardCharsets.UTF_8)
      val matcher = EXTRACT_ROOM_ID.matcher(data)
      if (matcher.find) realRoomId = matcher.group(1)
    } catch {
      case e: IOException => e.printStackTrace()
    }
    if (realRoomId == null) return -1
    realRoomId.toInt
  }
}
