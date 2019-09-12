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
package com.github.devcsrj.ispmon

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.http.ContentType
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.routing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun Application.main() {

  val logger = Module.logger()
  val interval = Module.monitorInterval()
  logger.info("Speedtest will be done every ${interval.toMinutes()} minute(s)")

  val repo = Module.resultsRepository()
  val speedtest = Module.speedtestTask(repo)
  val scheduler = Executors.newSingleThreadScheduledExecutor()
  scheduler.scheduleWithFixedDelay(
    speedtest, 0L, interval.toMinutes(), TimeUnit.MINUTES
  )


  install(AutoHeadResponse)

  routing {
    resource("/", "static/index.html")
    static("static") {
      resources("static")
    }
    get("results") {
      val since = call.request.queryParameters["since"].let {
        if (it == null)
          LocalDate.now()
        else {
          val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
          LocalDate.parse(it, pattern)
        }
      }
      val pattern = DateTimeFormatter.ofPattern("hh:mm a")
      val results = repo.findSince(since)
      val size = results.size

      call.respondTextWriter(ContentType.parse("application/json")) {
        write("[")
        results.forEachIndexed { i, result ->
          write("""
            {
              "timestamp" : "${result.timestamp.format(pattern)}",
              "upload" : ${result.upload.value()},
              "download" : ${result.download.value()}
            }
          """.trimIndent())
          if (i < size - 1)
            write(",\n")
        }
        write("]")
      }
    }
  }
}
