package com.github.devcsrj.ispmon

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Launcher
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Server : AbstractVerticle() {

  private lateinit var repo: ResultRepository
  private lateinit var mapper: ObjectMapper

  override fun init(vertx: Vertx,
                    context: Context) {
    super.init(vertx, context)

    val resultsDir = Paths.get("results")
    Files.createDirectories(resultsDir)
    repo = DiskResultRepository(resultsDir)

    mapper = ObjectMapper()
    mapper.registerModule(KotlinModule())
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
}

fun main() {
  Launcher.executeCommand("run", Server::class.java.name)
}
