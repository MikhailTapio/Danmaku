package committee.nova.danmaku.api.listener

import net.minecraft.util.text.ITextComponent

trait DanmakuPostListener {
  def afterDanmakuSent(danmaku: ITextComponent): Unit
}
