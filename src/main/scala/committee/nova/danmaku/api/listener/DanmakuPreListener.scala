package committee.nova.danmaku.api.listener

import net.minecraft.util.text.ITextComponent

trait DanmakuPreListener {
  def maySendDanmaku(shouldSend: Boolean, danmaku: ITextComponent): Boolean
}
