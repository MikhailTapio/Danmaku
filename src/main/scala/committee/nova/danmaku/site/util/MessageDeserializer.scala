package committee.nova.danmaku.site.util

import com.google.gson.{JsonDeserializationContext, JsonDeserializer, JsonElement, JsonObject}
import committee.nova.danmaku.config.impl.BilibiliConfig
import committee.nova.danmaku.site.util.MessageDeserializer.empty
import org.apache.commons.lang3.StringUtils

import java.lang.reflect.Type

object MessageDeserializer {
  val empty = ""
}

class MessageDeserializer(config: BilibiliConfig) extends JsonDeserializer[String] {
  override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String = {
    if (!json.isJsonObject) return empty
    val data = json.getAsJsonObject
    val t = data.get("cmd").getAsString
    t match {
      case "DANMU_MSG" => if (config.getDanmaku.isShow) handDanmaku(data) else empty
      case "SEND_GIFT" => if (config.getGift.isShow) handGift(data) else empty
      case "COMBO_SEND" => if (config.getGift.isShow) handComboGift(data) else empty
      case "INTERACT_WORD" => if (config.getEnter.isShowNormal) handNormalEnter(data) else empty
      case "WELCOME" => if (config.getEnter.isShowNormal) handWelcome(data) else empty
      case "WELCOME_GUARD" => if (config.getEnter.isShowGuard) handGuardWelcome(data) else empty
      case "GUARD_BUY" => if (config.getGuard.isShow) handBuyGuard(data) else empty
      case "SUPER_CHAT_MESSAGE" => if (config.getSc.isShow) handSuperChat(data) else empty
      case _ => empty
    }
  }

  private def handDanmaku(dataIn: JsonObject): String = {
    val info = dataIn.getAsJsonArray("info")
    val user = info.get(2).getAsJsonArray
    val userName = user.get(1).getAsString
    val danmaku = info.get(1).getAsString
    val isAdmin = user.get(2).getAsInt == 1
    val isGuard = StringUtils.isNotBlank(user.get(7).getAsString)
    for (block <- config.getDanmaku.getBlockWord) if (danmaku.contains(block)) return empty
    if (isAdmin) return String.format(config.getDanmaku.getAdminStyleFormatted, userName, danmaku)
    if (isGuard) return String.format(config.getDanmaku.getGuardStyleFormatted, userName, danmaku)
    String.format(config.getDanmaku.getNormalStyleFormatted, userName, danmaku)
  }

  private def handGift(dataIn: JsonObject): String = {
    val data = dataIn.getAsJsonObject("data")
    val userName = data.get("uname").getAsString
    val action = data.get("action").getAsString
    val giftName = data.get("giftName").getAsString
    val num = data.get("num").getAsInt
    for (block <- config.getGift.getBlockGift) if (giftName == block) return empty
    String.format(config.getGift.getStyleFormatted, userName, action, giftName, num)
  }

  private def handComboGift(dataIn: JsonObject): String = {
    val data = dataIn.getAsJsonObject("data")
    val userName = data.get("uname").getAsString
    val action = data.get("action").getAsString
    val giftName = data.get("gift_name").getAsString
    val num = data.get("total_num").getAsInt
    for (block <- config.getGift.getBlockGift) if (giftName == block) return empty
    String.format(config.getGift.getStyleFormatted, userName, action, giftName, num)
  }

  private def handNormalEnter(dataIn: JsonObject): String = {
    val data = dataIn.getAsJsonObject("data")
    val userName = data.get("uname").getAsString
    String.format(config.getEnter.getNormalStyleFormatted, userName)
  }

  private def handWelcome(dataIn: JsonObject): String = {
    val data = dataIn.getAsJsonObject("data")
    val userName = data.get("uname").getAsString
    String.format(config.getEnter.getNormalStyleFormatted, userName)
  }

  private def handGuardWelcome(dataIn: JsonObject): String = {
    val data = dataIn.getAsJsonObject("data")
    val userName = data.get("username").getAsString
    val level = data.get("guard_level").getAsInt
    level match {
      case 1 => String.format(config.getEnter.getGuardStyle1Formatted, userName)
      case 2 => String.format(config.getEnter.getGuardStyle2Formatted, userName)
      case 3 => String.format(config.getEnter.getGuardStyle3Formatted, userName)
      case _ => empty
    }
  }

  private def handBuyGuard(dataIn: JsonObject): String = {
    val data = dataIn.getAsJsonObject("data")
    val userName = data.get("username").getAsString
    val level = data.get("guard_level").getAsInt
    level match {
      case 1 => String.format(config.getGuard.getGuardStyle1Formatted, userName)
      case 2 => String.format(config.getGuard.getGuardStyle2Formatted, userName)
      case 3 => String.format(config.getGuard.getGuardStyle3Formatted, userName)
      case _ => empty
    }
  }

  private def handSuperChat(dataIn: JsonObject): String = {
    val data = dataIn.getAsJsonObject("data")
    val userName = data.getAsJsonObject("user_info").get("uname").getAsString
    val message = data.get("message").getAsString
    val price = data.get("price").getAsInt
    String.format(config.getSc.getStyleFormatted, userName, message, price)
  }
}
