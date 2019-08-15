package backend

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import java.util.*
import java.lang.ProcessBuilder.Redirect




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
    val uploadDir = System.getenv("upload_dir") ?: "/home/quangio/dev/hackathon/nn/images"
    route().handler(BodyHandler.create(uploadDir))
    route().handler(StaticHandler.create())
    route().handler(
      CorsHandler.create("*")
    )
    route("/").handler{
      it.response().end("abc")
    }
    route("/form").handler { ctx ->
      println(ctx.fileUploads().first().uploadedFileName())
      Runtime.getRuntime().exec("mv ${ctx.fileUploads().first().uploadedFileName()} /home/quangio/dev/hackathon/nn/images/phuc.jpg")
      // println("OK")

      val cmd = arrayOf("/bin/bash", "-c", "cd /home/quangio/dev/hackathon/nn/images; bash ./main.sh")
      /*
      try {
        val p = Runtime.getRuntime().exec(cmd)
      } catch (e: Exception) {
        e.printStackTrace()
      }

       */
      val pb = ProcessBuilder(cmd.toList())
      pb.redirectOutput(Redirect.INHERIT)
      pb.redirectError(Redirect.INHERIT)
      pb.start()
      ctx.response().end("ABC")
    }
  }
}

fun main() {
  Vertx.vertx().deployVerticle(MainVerticle())
}
