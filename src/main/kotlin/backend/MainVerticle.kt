package backend

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler


class MainVerticle : AbstractVerticle() {
  override fun start(startPromise: Promise<Void>) {
    val router = createRouter()
    vertx.createHttpServer().apply {
      requestHandler(router::accept)
    }.listen(8080) { res ->
      if (res.failed()) {
        res.cause().printStackTrace()
      } else {
        println("Started server at port 8080")
      }
    }

  }

  private fun createRouter() = Router.router(vertx).apply {
    // route().handler(CookieHandler.create())
    // route().handler(SessionHandler.create(sessionStore))
    val uploadDir = System.getenv("upload_dir") ?: "/tmp/vertx"
    route().handler(BodyHandler.create(uploadDir))
    route().handler(StaticHandler.create())
    route().handler(
      CorsHandler.create("*")
    )
    route("/form").handler { ctx ->
      ctx.response().end("ABC")
    }
  }
}

fun main() {
  Vertx.vertx().deployVerticle(MainVerticle())
}
