/**
 * MIT License
 *
 * Copyright (c) 2019 Reijhanniel Jearl Campos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
    val buffer = ByteArray(128 * 1024)
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

          val code = connection.responseCode
          if (code == 200)
            bytes += size
        }
      } finally {
        connection.disconnect()
      }

    }
    return bytes
  }
}
