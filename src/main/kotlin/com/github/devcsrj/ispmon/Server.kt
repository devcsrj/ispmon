package com.github.devcsrj.ispmon

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.devcsrj.ookla.Speedtest
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

class Server : AbstractVerticle() {

  private val logger = LoggerFactory.getLogger(Server::class.java)
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

    val monitorInterval = config().getLong("MONITOR_INTERVAL", 30L)
    scheduler = Executors.newSingleThreadScheduledExecutor()
    scheduler.scheduleWithFixedDelay(
      speedtest(), 0L, monitorInterval, TimeUnit.MINUTES
    )
  }

  override fun stop() {
    scheduler.shutdown()
  }

  override fun start(future: Future<Void>) {
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
      .listen(8080)
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
    val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
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
  Launcher.executeCommand("run", Server::class.java.name)
}
