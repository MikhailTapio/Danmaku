package committee.nova.danmaku.config.api

trait IConfig {
  def getSiteName: String

  def getConfigName: String
}
