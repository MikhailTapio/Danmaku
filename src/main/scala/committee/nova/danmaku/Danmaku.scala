package committee.nova.danmaku

import committee.nova.danmaku.utils.DanmakuManager
import committee.nova.danmaku.web.WebSocketClient
import net.minecraft.client.util.InputMappings
import net.minecraft.util.text.TextComponentTranslation
import org.apache.logging.log4j.{LogManager, Logger}
import org.dimdev.rift.listener.client.KeybindHandler
import org.dimdev.riftloader.listener.InitializationListener
import org.lwjgl.glfw.GLFW
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.Mixins

import java.util.concurrent.ScheduledFuture

object Danmaku {
  val MODID = "danmaku"
  val logger: Logger = LogManager.getLogger
  var heartBeatTask: ScheduledFuture[_] = _
  var webSocketClient: WebSocketClient = _
}

class Danmaku extends InitializationListener with KeybindHandler {
  override def onInitialization(): Unit = {
    MixinBootstrap.init()
    Mixins.addConfiguration("mixins.danmaku.json")
  }

  override def processKeybinds(): Unit = {
    if (!InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) && !InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT)) return
    if (!InputMappings.isKeyDown(GLFW.GLFW_KEY_B)) return
    if (!DanmakuManager.closeDanmaku()) return
    DanmakuManager.sendDanmaku(new TextComponentTranslation("msg.danmaku.cfg.reload"))
    DanmakuManager.openDanmaku()
  }
}
