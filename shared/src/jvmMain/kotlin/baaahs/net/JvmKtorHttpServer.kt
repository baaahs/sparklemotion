package baaahs.net

import baaahs.io.Fs
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.resolveResource
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
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