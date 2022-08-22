package committee.nova.danmaku.config.util

import com.google.gson.GsonBuilder
import committee.nova.danmaku.Danmaku
import committee.nova.danmaku.config.impl.BilibiliConfig
import net.minecraft.client.Minecraft
import org.apache.commons.io.FileUtils

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files

object ConfigManager {
  private val CONFIG_FOLDER = Minecraft.getInstance.gameDir.toPath.resolve("config").resolve(Danmaku.MODID)
  private val GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping.create

  def getBilibiliConfig: BilibiliConfig = {
    var config = new BilibiliConfig
    if (!CONFIG_FOLDER.toFile.isDirectory) {
      try Files.createDirectories(CONFIG_FOLDER)
      catch {
        case e: IOException => e.printStackTrace()
      }
    }
    val configPath = CONFIG_FOLDER.resolve(config.getConfigName + ".json")
    if (configPath.toFile.isFile) try config = GSON.fromJson(FileUtils.readFileToString(configPath.toFile, StandardCharsets.UTF_8), classOf[BilibiliConfig])
    catch {
      case e: IOException => e.printStackTrace()
    }
    else try FileUtils.write(configPath.toFile, GSON.toJson(config), StandardCharsets.UTF_8)
    catch {
      case e: IOException => e.printStackTrace()
    }
    config.deco
  }

  def saveBilibiliConfig(config: BilibiliConfig): Unit = {
    if (!CONFIG_FOLDER.toFile.isDirectory) {
      try Files.createDirectories(CONFIG_FOLDER)
      catch {
        case e: IOException => e.printStackTrace()
      }
    }
    val configPath = CONFIG_FOLDER.resolve(config.getConfigName + ".json")
    try FileUtils.write(configPath.toFile, GSON.toJson(config), StandardCharsets.UTF_8)
    catch {
      case e: IOException => e.printStackTrace()
    }
  }
}
