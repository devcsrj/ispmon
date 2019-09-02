package com.github.devcsrj.ispmon

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class IspmonTest {

  @BeforeEach
  fun prepare(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(Ispmon::class.java.name,
      testContext.succeeding<String> { id -> testContext.completeNow() })
  }

  @Test
  @DisplayName("Check that the server has started")
  fun checkServerHasStarted(vertx: Vertx, testContext: VertxTestContext) {
    val webClient = WebClient.create(vertx)
    webClient.get(8080, "localhost", "/")
      .`as`(BodyCodec.string())
      .send(testContext.succeeding { response ->
        testContext.verify {
          assertEquals(200, response.statusCode())
          assertTrue(response.body().length > 0)
          assertTrue(response.body().contains("Hello Vert.x!"))
          testContext.completeNow()
        }
      })
  }
}
