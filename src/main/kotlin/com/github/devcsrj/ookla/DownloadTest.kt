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
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.Callable

/**
 * Initiates a download test to the provided [server]
 */
internal class DownloadTest(private val server: Server,
                            private val timeout: Duration) : Callable<Speed> {

  private val files = arrayOf(
    "random1000x1000.jpg",
    "random2000x2000.jpg",
    "random3000x3000.jpg",
    "random4000x4000.jpg"
  )

  init {
    require(!timeout.isNegative && !timeout.isZero) {
      "Timeout must be greater than 0, got: $timeout"
    }
  }

  override fun call(): Speed {
    val testStart = LocalTime.now()
    var totalDownloaded = 0L

    val maxTestEnd = testStart.plus(timeout)
    val it = files.iterator()
    while (LocalTime.now().isBefore(maxTestEnd) && it.hasNext()) {
      val url = URL("http://" + server.host + "/speedtest/${it.next()}")
      totalDownloaded += download(url, maxTestEnd)
    }

    val testEnd = LocalTime.now()
    val elapsed = Duration.between(testStart, testEnd)
    val size = DataSize.ofBytes(totalDownloaded)
    return Speed(size, elapsed)
  }

  private fun download(url: URL, endTime: LocalTime): Long {
    val connection = url.openConnection() as HttpURLConnection
    try {
      val code = connection.responseCode
      if (code != 200)
        return 0L
      return download(connection.inputStream, endTime)
    } finally {
      connection.disconnect()
    }
  }

  private fun download(src: InputStream, endTime: LocalTime): Long {
    var bytes = 0L
    val buffer = ByteArray(10240)
    src.use {
      while (true) {
        val len = it.read(buffer)
        if (len <= -1)
          break
        bytes += len
        if (LocalTime.now().isAfter(endTime))
          break
      }
    }
    return bytes
  }
}
