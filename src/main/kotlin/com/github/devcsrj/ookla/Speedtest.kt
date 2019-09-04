package com.github.devcsrj.ookla

import com.github.devcsrj.ispmon.Result
import io.vertx.core.logging.LoggerFactory
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

  private val logger = LoggerFactory.getLogger(Speedtest::class.java)

  override fun call(): Result {
    logger.debug("Loading speedtest settings...")
    val settings = loadSettings()

    logger.debug("Finding the best server...")
    val closest = loadServers()
      .filter { it.countryCode == settings.countryCode }
      .groupBy { it.location.distanceFrom(settings.location) }
      .minBy { it.key }
    require(closest != null) {
      "Could not find any server for ${settings.isp}"
    }

    val server = closest.value.iterator().next()
    logger.info("Server: ${server.sponsor} - ${server.host}")

    logger.debug("Starting download...")
    val downloadSpeed = DownloadTest(server, timeout).call()
    logger.info("⬇️ ${downloadSpeed.value()} Mbps")

    logger.debug("Starting upload...")
    val uploadSpeed = UploadTest(server, timeout).call()
    logger.info("⬆️ ${uploadSpeed.value()} Mbps")

    return Result(
      timestamp = LocalDateTime.now(),
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
