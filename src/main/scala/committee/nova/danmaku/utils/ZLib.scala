package committee.nova.danmaku.utils

import org.apache.commons.io.output.ByteArrayOutputStream

import java.util.zip.Inflater

object ZLib {
  def decompress(data: Array[Byte]): Array[Byte] = {
    var output: Array[Byte] = null
    val inflater = new Inflater
    inflater.reset()
    inflater.setInput(data)
    val o = new ByteArrayOutputStream(data.length)
    try {
      val buf = new Array[Byte](1024)
      while (!inflater.finished) {
        val i = inflater.inflate(buf)
        o.write(buf, 0, i)
      }
      output = o.toByteArray
    } catch {
      case _: Exception => output = data
    } finally if (o != null) o.close()
    inflater.end()
    output
  }
}
