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

import com.github.devcsrj.ispmon.Module
import com.github.devcsrj.ispmon.Result
import java.net.HttpURLConnection
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Callable
import kotlin.streams.toList


/**
 * A client for Ookla's Speedtest
 *
 * @see <a href="https://www.speedtest.net">speedtest.net</a>
 * @author devcsrj
 */
class Speedtest(
  private val baseUrl: URI,
  private val timeout: Duration) : Callable<Result> {

  constructor(timeout: Duration) : this(URI.create("https://www.speedtest.net"), timeout)

  private val logger = Module.logger()

  override fun call(): Result {
    logger.debug("Loading speedtest settings...")
    val settings = loadSettings()
    logger.info("ISP: ${settings.isp} - ${settings.address}")

    logger.debug("Finding the best server...")
    val closest = loadServers()
      .filter { it.countryCode == settings.countryCode }
      .groupBy { it.location.distanceFrom(settings.location) }
      .minBy { it.key }
    require(closest != null) {
      "Could not find any server for ${settings.isp}"
    }

    val server = closest.value.iterator().next()
    logger.info("Speedtest server: ${server.sponsor} - ${server.host}")

    logger.debug("Starting download...")
    val downloadSpeed = DownloadTest(server, timeout).call()
    logger.info("⬇️ ${downloadSpeed.value()} Mbps")

    logger.debug("Starting upload...")
    val uploadSpeed = UploadTest(server, timeout).call()
    logger.info("⬆️ ${uploadSpeed.value()} Mbps")

    return Result(
      timestamp = LocalDateTime.now(),
      ip = settings.address,
      isp = settings.isp,
      download = downloadSpeed,
      upload = uploadSpeed
    )
  }

  internal fun loadServers(): Sequence<Server> {
    val url = baseUrl.resolve("/speedtest-servers-static.php").toURL()
    val connection = url.openConnection() as HttpURLConnection
    val code = connection.responseCode
    try {
      require(code == 200) {
        "Could not fetch configuration, server responded with $code"
      }

      val results = connection.inputStream.use { src ->
        src.bufferedReader().lines()
          .filter { it.contains("<server ") }
          .map { Util.mapFromAttributeLine(it) }
          .map {
            Server(
              uploadUrl = URI.create(it["url"]!!),
              location = Location(
                latitude = it["lat"]!!.toDouble(),
                longitude = it["lon"]!!.toDouble()
              ),
              countryName = it["country"]!!,
              countryCode = it["cc"]!!,
              sponsor = it["sponsor"]!!,
              host = it["host"]!!
            )
          }.toList()
      }
      return results.asSequence()
    } finally {
      connection.disconnect()
    }
  }

  internal fun loadSettings(): Settings {
    val url = baseUrl.resolve("/speedtest-config.php").toURL()
    val connection = url.openConnection() as HttpURLConnection
    val code = connection.responseCode
    try {
      require(code == 200) {
        "Could not fetch configuration, server responded with $code"
      }

      return connection.inputStream.use { src ->
        src.bufferedReader().lines()
          .filter { it.contains("<client ") }
          .map { Util.mapFromAttributeLine(it) }
          .findFirst()
          .orElseThrow {
            throw IllegalArgumentException("Could not fetch xml node " +
              "'/settings/client' from $url")
          }
      }.let {
        Settings(
          address = it["ip"]!!,
          location = Location(
            latitude = it["lat"]!!.toDouble(),
            longitude = it["lon"]!!.toDouble()
          ),
          isp = it["isp"]!!,
          countryCode = it["country"]!!
        )
      }
    } finally {
      connection.disconnect()
    }
  }
}
