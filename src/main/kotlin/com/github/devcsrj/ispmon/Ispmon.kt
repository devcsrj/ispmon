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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.devcsrj.ookla.Speedtest
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Launcher
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class Ispmon : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(Ispmon::class.java)
  private val timeout = Duration.ofSeconds(15)

  private lateinit var repo: ResultRepository
  private lateinit var mapper: ObjectMapper
  private lateinit var scheduler: ScheduledExecutorService

  override fun init(vertx: Vertx,
                    context: Context) {
    super.init(vertx, context)

    val resultsDir = Paths.get("results")
    Files.createDirectories(resultsDir)
    repo = DiskResultRepository(resultsDir)

    mapper = ObjectMapper()
    mapper.registerModule(KotlinModule())
  }

  override fun stop() {
    scheduler.shutdown()
  }

  override fun start(future: Future<Void>) {
    val retriever = ConfigRetriever.create(vertx)
    retriever.getConfig { config ->
      val result = config.result()
      config.map {
        val port = result.getInteger("ISPMON_PORT", 5000)
        startServer(port)

        val monitorInterval = result.getLong("ISPMON_INTERVAL", 15L)
        startScheduler(monitorInterval)
      }
    }
  }

  private fun startScheduler(monitorInterval: Long) {
    logger.info("Speedtest will be done every $monitorInterval minute(s)")
    scheduler = Executors.newSingleThreadScheduledExecutor()
    scheduler.scheduleWithFixedDelay(
      speedtest(), 0L, monitorInterval, TimeUnit.MINUTES
    )
  }

  private fun startServer(port: Int) {
    logger.info("Server running at $port")
    val router = Router.router(vertx).apply {
      route(HttpMethod.GET, "/static/*")
        .handler(StaticHandler.create("static"))
      route(HttpMethod.GET, "/results")
        .produces("application/json")
        .handler { getResults(it) }
      route(HttpMethod.GET, "/")
        .produces("text/html")
        .handler { it.response().sendFile("static/index.html") }
    }

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port)
  }

  private fun getResults(context: RoutingContext) {
    val since = context.queryParam("since").let {
      if (it.isEmpty())
        LocalDate.now()
      else {
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(it.iterator().next(), pattern)
      }
    }
    val pattern = DateTimeFormatter.ofPattern("hh:mm a")
    val results = repo.findSince(since)
      .map {
        mapOf(
          "timestamp" to it.timestamp.format(pattern),
          "upload" to it.upload.value(),
          "download" to it.download.value()
        )
      }
    context
      .response()
      .end(mapper.writeValueAsString(results))
  }

  private fun speedtest(): Runnable {
    return Runnable {
      val test = Speedtest(timeout)
      try {
        val result = test.call()
        repo.save(result)
      } catch (e: Throwable) {
        logger.error("Could not finish speedtest due to exception", e)
      }
    }
  }
}

fun main() {
  Launcher.executeCommand("run", Ispmon::class.java.name)
}
