package com.github.devcsrj.ispmon

import io.vertx.core.AbstractVerticle
import io.vertx.core.Launcher

class Server : AbstractVerticle() {

  override fun start() {
    vertx.createHttpServer()
      .requestHandler { req -> req.response().end("Hello Vert.x!") }
      .listen(8080)
  }

}

fun main() {
  Launcher.executeCommand("run", Server::class.java.name)
}
