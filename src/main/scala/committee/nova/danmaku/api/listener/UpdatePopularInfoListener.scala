package committee.nova.danmaku.api.listener

trait UpdatePopularInfoListener {
  def infoUpdated(info: Int): Unit
}
