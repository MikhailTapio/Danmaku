package committee.nova.danmaku.utils

import java.util.regex.Pattern

object BilibiliMsgSplit {
  private val SPLIT = Pattern.compile("}.{16}\\{\"cmd\"")

  def split(msg: String): Array[String] = {
    val split = SPLIT.split(msg.replaceAll("\n", "\u0020").replaceAll("\r", "\u0020"))
    if (split.length > 1) for (i <- 0 until split.length) {
      if (i == 0) split(0) = split(0) + "}"
      else if (i == split.length - 1) split(i) = "{\"cmd\"" + split(i)
      else split(i) = "{\"cmd\"" + split(i) + "}"
    }
    split
  }
}
