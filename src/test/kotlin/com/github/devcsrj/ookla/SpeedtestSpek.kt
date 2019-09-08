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
