package com.github.devcsrj.ookla

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeUnit

object SpeedtestSpek : Spek({

  describe("Speedtest") {

    lateinit var server: MockWebServer

    beforeEachTest {
      server = MockWebServer()
    }

    it("can load settings") {
      val body = javaClass.getResourceAsStream("/ookla/speedtest-config.php").use {
        it.reader().readText()
      }
      server.enqueue(MockResponse()
        .setResponseCode(200)
        .setBody(body))

      val speedtest = Speedtest(server.url("/").toUri(), Duration.ofSeconds(2L))
      val settings = speedtest.loadSettings()

      val rr = server.takeRequest(1, TimeUnit.SECONDS)!!

      rr.path shouldEqual "/speedtest-config.php"
      rr.method shouldEqual "GET"
      settings shouldEqual Settings(
        address = "192.168.0.1",
        location = Location(
          latitude = 24.9999993,
          longitude = -71.0087548
        ),
        isp = "Levi",
        countryCode = "US"
      )
    }

    it("can load servers") {
      val body = javaClass.getResourceAsStream("/ookla/speedtest-servers-static.php").use {
        it.reader().readText()
      }
      server.enqueue(MockResponse()
        .setResponseCode(200)
        .setBody(body))

      val speedtest = Speedtest(server.url("/").toUri(), Duration.ofSeconds(2L))
      val servers = speedtest.loadServers().toList()

      val rr = server.takeRequest(1, TimeUnit.SECONDS)!!

      rr.path shouldEqual "/speedtest-servers-static.php"
      rr.method shouldEqual "GET"
      servers.size shouldEqual 4
      servers shouldContain Server(
        uploadUrl = URI.create("http://172.162.24.5:8080/speedtest/upload.php"),
        location = Location(
          latitude = 37.2513663,
          longitude = -115.8390265
        ),
        countryCode = "US",
        countryName = "United States",
        sponsor = "ET",
        host = "172.162.24.5:8080"
      )
    }

    afterEachTest {
      server.shutdown()
    }
  }
})
