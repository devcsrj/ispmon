package com.github.devcsrj.ookla

import com.github.devcsrj.ispmon.DataSize
import com.github.devcsrj.ispmon.Speed
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.Callable


/**
 * Initiates a download test to the provided [server]
 */
internal class UploadTest(private val server: Server,
                          private val timeout: Duration) : Callable<Speed> {

  override fun call(): Speed {
    val testStart = LocalTime.now()
    val totalUploaded = upload(server.uploadUrl.toURL(), testStart.plus(timeout))
    val testEnd = LocalTime.now()
    val elapsed = Duration.between(testStart, testEnd)
    val size = DataSize.ofBytes(totalUploaded)
    return Speed(size, elapsed)
  }

  private fun upload(url: URL, endTime: LocalTime): Long {
    var bytes = 0L
    val buffer = ByteArray(32 * 1024)
    val size = buffer.size
    while (true) {
      if (LocalTime.now().isAfter(endTime))
        break

      val connection = url.openConnection() as HttpURLConnection
      try {
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setRequestProperty("Connection", "Keep-Alive")

        DataOutputStream(connection.outputStream).use {
          it.write(buffer, 0, size)
          it.flush()
          bytes += size
        }

      } finally {
        connection.disconnect()
      }

    }
    return bytes
  }
}
