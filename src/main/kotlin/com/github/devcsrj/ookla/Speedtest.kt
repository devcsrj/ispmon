package com.github.devcsrj.ookla

import com.github.devcsrj.ispmon.Result
import io.vertx.core.logging.LoggerFactory
import org.joox.JOOX.`$`
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Callable


/**
 * A client for Ookla's Speedtest
 *
 * @see <a href="https://www.speedtest.net">speedtest.net</a>
 * @author devcsrj
 */
class Speedtest(private val timeout: Duration) : Callable<Result> {

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

  private fun loadServers(): Sequence<Server> {
    val url = URL("https://www.speedtest.net/speedtest-servers-static.php")
    val connection = url.openConnection() as HttpURLConnection
    val code = connection.responseCode
    try {
      require(code == 200) {
        "Could not fetch configuration, server responded with $code"
      }

      val doc = connection.inputStream.use { `$`(it) }
      val matches = doc.xpath("/settings/servers").children()
      return sequence {
        for (match in matches) {
          yield(Server(
            uploadUrl = URI.create(match.getAttribute("url")),
            location = Location(
              latitude = match.getAttribute("lat").toDouble(),
              longitude = match.getAttribute("lon").toDouble()
            ),
            countryName = match.getAttribute("name"),
            countryCode = match.getAttribute("cc"),
            sponsor = match.getAttribute("sponsor"),
            host = match.getAttribute("host")
          ))
        }
      }
    } finally {
      connection.disconnect()
    }
  }

  private fun loadSettings(): Settings {
    val url = URL("https://www.speedtest.net/speedtest-config.php")
    val connection = url.openConnection() as HttpURLConnection
    val code = connection.responseCode
    try {
      require(code == 200) {
        "Could not fetch configuration, server responded with $code"
      }

      val doc = connection.inputStream.use { `$`(it) }
      val client = doc.xpath("/settings/client")
      return Settings(
        address = InetAddress.getByName(client.attr("ip")),
        location = Location(
          latitude = client.attr("lat").toDouble(),
          longitude = client.attr("lon").toDouble()
        ),
        isp = client.attr("isp"),
        countryCode = client.attr("country")
      )
    } finally {
      connection.disconnect()
    }
  }
}
