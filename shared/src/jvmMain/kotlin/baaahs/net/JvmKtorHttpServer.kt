package baaahs.net

import baaahs.io.Fs
import baaahs.sm.server.ExceptionReporter
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import java.io.File

class JvmKtorHttpServer(
    link: Network.Link,
    port: Int,
    private val networkScope: CoroutineScope
) : AbstractKtorHttpServer(
    embeddedServer(CIO, port, configure = {
        // Let's give brains lots of time for OTA download:
//                responseWriteTimeoutSeconds = 3000
    }) {
        install(WebSockets.Plugin) {
            pingPeriod = java.time.Duration.ofSeconds(15)
            timeout = java.time.Duration.ofSeconds(15)
            maxFrameSize = Long.Companion.MAX_VALUE
            masking = false
        }
        install(CallLogging)
    }, link, port, networkScope
) {

    override fun routing(config: Network.HttpServer.HttpRouting.() -> Unit) {
        application.routing {
            val routing = KtorHttpRouting(this)
            config.invoke(routing)
        }
    }

    override fun Routing.configRouting(config: Network.HttpServer.HttpRouting.() -> Unit) {
        KtorHttpRouting(this).config()
    }

    override suspend fun DefaultWebSocketServerSession.handleUdpProxy() {
        JvmUdpProxy(networkScope).handle(this)
    }

    class KtorHttpRouting(
        val routing: Routing
    ) : Network.HttpServer.HttpRouting {

        override fun get(
            path: String,
            handler: suspend Network.HttpServer.HttpHandling.() -> Unit
        ) {
            routing.get(path) {
                handler.invoke(KtorHttpHandling(call))
            }
        }

        override fun staticResources(path: String, basePackage: String) {
            routing.staticResources(path, basePackage)
        }

        override fun staticFiles(path: String, dir: Fs.File) {
            routing.staticFiles(path, File(dir.fullPath))
        }

        class KtorHttpHandling(
            val call: ApplicationCall
        ) : Network.HttpServer.HttpHandling {
            override suspend fun redirect(path: String) {
                call.respondRedirect(path)
            }

            override suspend fun respondWithResource(path: String, resourcePackage: String) {
                val file = call.resolveResource(path, resourcePackage)
                if (file is OutgoingContent)
                    call.respond(HttpStatusCode.OK, file)
            }
        }
    }
}