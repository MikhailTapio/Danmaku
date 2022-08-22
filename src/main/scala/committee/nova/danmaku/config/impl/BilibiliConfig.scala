package committee.nova.danmaku.config.impl

import com.google.gson.annotations.{Expose, SerializedName}
import committee.nova.danmaku.config.api.IConfig
import committee.nova.danmaku.config.impl.BilibiliConfig.{CONFIG_NAME, SITE_NAME}

import java.util.regex.Matcher

object BilibiliConfig {
  @Expose private val SITE_NAME = "哔哩哔哩"
  @Expose private val CONFIG_NAME = "bilibili"
}

class BilibiliConfig extends IConfig {
  @SerializedName("room") private var room = new Room
  @SerializedName("danmaku") private var danmaku = new Danmaku
  @SerializedName("gift") private var gift = new Gift
  @SerializedName("enter") private var enter = new Enter
  @SerializedName("guard") private var guard = new Guard
  @SerializedName("sc") private var sc = new SpecialChat

  def deco: BilibiliConfig = {
    this.danmaku = danmaku.deco
    this.gift = gift.deco
    this.enter = enter.deco
    this.guard = guard.deco
    this.sc = sc.deco
    this
  }

  override def getSiteName: String = SITE_NAME

  override def getConfigName: String = CONFIG_NAME

  def getRoom: Room = room

  def getDanmaku: Danmaku = danmaku

  def getGift: Gift = gift

  def getEnter: Enter = enter

  def getGuard: Guard = guard

  def getSc: SpecialChat = sc

  class Room {
    @SerializedName("id") private var id = -1
    @SerializedName("enable") private var enable = false

    def getId: Int = id

    def setId(id: Int): Unit = this.id = id

    def isEnable: Boolean = enable

    def setEnable(enable: Boolean): Unit = this.enable = enable
  }

  class Danmaku {
    @SerializedName("show") private var show = true
    @SerializedName("normal_style") private var normalStyle = "&7[普] &b<%{user}> &f%{danmaku}"
    private var normalStyleFormatted = ""
    @SerializedName("guard_style") private var guardStyle = "&6[舰] &2<%{user}> &f%{danmaku}"
    private var guardStyleFormatted = ""
    @SerializedName("admin_style") private var adminStyle = "&4[房] &d<%{user}> &f%{danmaku}"
    private var adminStyleFormatted = ""
    @SerializedName("block_word") private var blockWord = Array("小鬼", "biss", "嘴臭", "骂我", "傻逼", "弱智", "脑残", "cnm")

    def isShow: Boolean = show

    def setShow(show: Boolean): Unit = this.show = show

    def getNormalStyle: String = normalStyle

    def setNormalStyle(normalStyle: String): Unit = this.normalStyle = normalStyle

    def getGuardStyle: String = guardStyle

    def setGuardStyle(guardStyle: String): Unit = this.guardStyle = guardStyle

    def getAdminStyle: String = adminStyle

    def setAdminStyle(adminStyle: String): Unit = this.adminStyle = adminStyle

    def getBlockWord: Array[String] = blockWord

    def setBlockWord(blockWord: Array[String]): Unit = this.blockWord = blockWord

    def getNormalStyleFormatted: String = normalStyleFormatted

    def getGuardStyleFormatted: String = guardStyleFormatted

    def getAdminStyleFormatted: String = adminStyleFormatted

    def deco: Danmaku = {
      this.normalStyleFormatted = normalStyle.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s")).replaceAll("%\\{danmaku}", Matcher.quoteReplacement("%2$s"))
      this.guardStyleFormatted = guardStyle.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s")).replaceAll("%\\{danmaku}", Matcher.quoteReplacement("%2$s"))
      this.adminStyleFormatted = adminStyle.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s")).replaceAll("%\\{danmaku}", Matcher.quoteReplacement("%2$s"))
      this
    }
  }

  class Gift {
    @SerializedName("show") private var show = true
    @SerializedName("style") private var style = "&7%{user} %{action} [%{gift}] × %{num}"
    private var styleFormatted = ""
    @SerializedName("block_gift") private var blockGift = Array("辣条", "小心心")

    def isShow: Boolean = show

    def setShow(show: Boolean): Unit = this.show = show

    def getStyle: String = style

    def setStyle(style: String): Unit = this.style = style

    def getBlockGift: Array[String] = blockGift

    def setBlockGift(blockGift: Array[String]): Unit = this.blockGift = blockGift

    def getStyleFormatted: String = styleFormatted

    def deco: Gift = {
      this.styleFormatted = style.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s")).replaceAll("%\\{action}", Matcher.quoteReplacement("%2$s")).replaceAll("%\\{gift}", Matcher.quoteReplacement("%3$s")).replaceAll("%\\{num}", Matcher.quoteReplacement("%4$s"))
      this
    }
  }

  class Enter {
    @SerializedName("show_normal") private var showNormal = false
    @SerializedName("show_guard") private var showGuard = true
    @SerializedName("normal_style") private var normalStyle = "&7欢迎 %{user} 进入直播间"
    private var normalStyleFormatted = ""
    @SerializedName("guard_style_1") private var guardStyle1 = "&4欢迎总督 %{user} 进入直播间"
    private var guardStyle1Formatted = ""
    @SerializedName("guard_style_2") private var guardStyle2 = "&6欢迎提督 %{user} 进入直播间"
    private var guardStyle2Formatted = ""
    @SerializedName("guard_style_3") private var guardStyle3 = "&3欢迎舰长 %{user} 进入直播间"
    private var guardStyle3Formatted = ""

    def isShowNormal: Boolean = showNormal

    def setShowNormal(showNormal: Boolean): Unit = this.showNormal = showNormal

    def isShowGuard: Boolean = showGuard

    def setShowGuard(showGuard: Boolean): Unit = this.showGuard = showGuard

    def getNormalStyle: String = normalStyle

    def setNormalStyle(normalStyle: String): Unit = this.normalStyle = normalStyle

    def getGuardStyle1: String = guardStyle1

    def setGuardStyle1(guardStyle1: String): Unit = this.guardStyle1 = guardStyle1

    def getGuardStyle2: String = guardStyle2

    def setGuardStyle2(guardStyle2: String): Unit = this.guardStyle2 = guardStyle2

    def getGuardStyle3: String = guardStyle3

    def setGuardStyle3(guardStyle3: String): Unit = this.guardStyle3 = guardStyle3

    def getNormalStyleFormatted: String = normalStyleFormatted

    def getGuardStyle1Formatted: String = guardStyle1Formatted

    def getGuardStyle2Formatted: String = guardStyle2Formatted

    def getGuardStyle3Formatted: String = guardStyle3Formatted

    def deco: Enter = {
      this.normalStyleFormatted = normalStyle.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
      this.guardStyle1Formatted = guardStyle1.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
      this.guardStyle2Formatted = guardStyle2.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
      this.guardStyle3Formatted = guardStyle3.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
      this
    }
  }

  class Guard {
    @SerializedName("show") private var show = true
    @SerializedName("guard_style_1") private var guardStyle1 = "&4%{user} 开通了主播的总督"
    private var guardStyle1Formatted = ""
    @SerializedName("guard_style_2") private var guardStyle2 = "&6%{user} 开通了主播的提督"
    private var guardStyle2Formatted = ""
    @SerializedName("guard_style_3") private var guardStyle3 = "&3%{user} 开通了主播的舰长"
    private var guardStyle3Formatted = ""

    def isShow: Boolean = show

    def setShow(show: Boolean): Unit = this.show = show

    def getGuardStyle1: String = guardStyle1

    def setGuardStyle1(guardStyle1: String): Unit = this.guardStyle1 = guardStyle1

    def getGuardStyle2: String = guardStyle2

    def setGuardStyle2(guardStyle2: String): Unit = this.guardStyle2 = guardStyle2

    def getGuardStyle3: String = guardStyle3

    def setGuardStyle3(guardStyle3: String): Unit = this.guardStyle3 = guardStyle3

    def getGuardStyle1Formatted: String = guardStyle1Formatted

    def getGuardStyle2Formatted: String = guardStyle2Formatted

    def getGuardStyle3Formatted: String = guardStyle3Formatted

    def deco: Guard = {
      this.guardStyle1Formatted = guardStyle1.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
      this.guardStyle2Formatted = guardStyle2.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
      this.guardStyle3Formatted = guardStyle3.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
      this
    }
  }

  class SpecialChat {
    @SerializedName("show") private var show = true
    @SerializedName("style") private var style = "&4%{user} > %{msg} [¥%{price}]"
    private var styleFormatted = ""

    def isShow: Boolean = show

    def setShow(show: Boolean): Unit = this.show = show

    def getStyle: String = style

    def setStyle(style: String): Unit = this.style = style

    def getStyleFormatted: String = styleFormatted

    def deco: SpecialChat = {
      this.styleFormatted = style.replaceAll("&([0-9a-fk-or])", "§$1").replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s")).replaceAll("%\\{msg}", Matcher.quoteReplacement("%2$s")).replaceAll("%\\{price}", Matcher.quoteReplacement("%3$s"))
      this
    }
  }
}