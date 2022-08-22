package committee.nova.danmaku.utils

import committee.nova.danmaku.Danmaku
import committee.nova.danmaku.api.listener.{DanmakuPostListener, DanmakuPreListener, UpdatePopularInfoListener}
import committee.nova.danmaku.config.util.ConfigManager
import committee.nova.danmaku.site.impl.BilibiliSite
import committee.nova.danmaku.web.WebSocketClient
import net.minecraft.client.Minecraft
import net.minecraft.util.text.{ChatType, ITextComponent, TextComponentString}
import org.dimdev.riftloader.RiftLoader

object DanmakuManager {
  def openDanmaku(): Unit = {
    val site = new BilibiliSite(ConfigManager.getBilibiliConfig)
    if (site.getConfig.getRoom.isEnable) {
      Danmaku.webSocketClient = new WebSocketClient(site)
      try Danmaku.webSocketClient.open()
      catch {
        case e: Exception =>
          Danmaku.webSocketClient = null
          e.printStackTrace()
      }
    }
  }

  def closeDanmaku(): Boolean = {
    if (Danmaku.webSocketClient == null) return false
    try Danmaku.webSocketClient.close()
    catch {
      case _: Exception =>
    } finally Danmaku.webSocketClient = null
    true
  }

  def sendDanmaku(msg: String): Unit = sendDanmaku(new TextComponentString(msg))

  def sendDanmaku(danmaku: ITextComponent): Unit = {
    var shouldSend = true
    val rift = RiftLoader.instance
    rift.getListeners(classOf[DanmakuPreListener]).forEach(l => {
      val s = l.maySendDanmaku(shouldSend, danmaku)
      if (!s) shouldSend = false
    })
    if (!shouldSend) return
    val gui = Minecraft.getInstance().ingameGUI
    if (gui != null) gui.addChatMessage(ChatType.CHAT, danmaku)
    rift.getListeners(classOf[DanmakuPostListener]).forEach(l => l.afterDanmakuSent(danmaku))
  }

  def updatePopularInfo(info: Int): Unit = RiftLoader.instance.getListeners(classOf[UpdatePopularInfoListener]).forEach(l => l.infoUpdated(info))
}
