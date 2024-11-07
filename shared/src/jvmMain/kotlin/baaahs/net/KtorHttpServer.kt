package baaahs.net

import baaahs.io.Fs
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.resolveResource
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.request.host
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.time.Duration

class KtorHttpServer(
    private val link: Network.Link,
    private val port: Int
) : Network.HttpServer {
    val httpServer = embeddedServer(Netty, port, configure = {
        // Let's give brains lots of time for OTA download:
        responseWriteTimeoutSeconds = 3000
    }) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
    }

    val application: Application get() = httpServer.application

    override fun listenWebSocket(
        path: String,
        onConnect: (incomingConnection: Network.TcpConnection) -> Network.WebSocketListener
    ) {
        application.routing {
            webSocket(path) {
                val tcpConnection = object : Network.TcpConnection {
                    override val fromAddress: Network.Address
                        get() = link.myAddress // TODO Fix
                    override val toAddress: Network.Address
                        get() = link.myAddress // TODO fix
                    override val port: Int
                        get() = this@KtorHttpServer.port

                    override fun send(bytes: ByteArray) {
                        val frame = Frame.Binary(true, ByteBuffer.wrap(bytes.clone()))
                        JvmNetwork.Companion.networkScope.launch {
                            this@webSocket.send(frame)
                            this@webSocket.flush()
                        }
                    }

                    override fun close() {
                        JvmNetwork.Companion.networkScope.launch {
                            this@webSocket.close()
                        }
                    }
                }

                JvmNetwork.Companion.logger.info { "Connection from ${this.call.request.host()}…" }
                val webSocketListener = onConnect(tcpConnection)
                webSocketListener.connected(tcpConnection)

                try {
                    while (true) {
                        val frame = incoming.receive()
                        if (frame is Frame.Binary) {
                            val bytes = frame.readBytes()
                            webSocketListener.receive(tcpConnection, bytes)
                        } else {
                            JvmNetwork.Companion.logger.warn { "wait huh? received weird data: $frame" }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    JvmNetwork.Companion.logger.info { "Websocket closed." }
                    close(CloseReason(CloseReason.Codes.NORMAL, "Closed."))
                } catch (e: Exception) {
                    JvmNetwork.Companion.logger.error(e) { "Error reading websocket frame." }
                    close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error: ${e.message}"))
                } finally {
                    webSocketListener.reset(tcpConnection)
                }
            }

            webSocket("/sm/udpProxy") {
                try {
                    JvmUdpProxy().handle(this)
                } catch (e: Exception) {
                    JvmNetwork.Companion.logger.error(e) { "Error handling UDP proxy." }
                }
            }
        }
    }

    override fun routing(config: Network.HttpServer.HttpRouting.() -> Unit) {
        application.routing {
            val routing = KtorHttpRouting(this)
            config.invoke(routing)
        }
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

    override fun start() {
        httpServer.start()
    }
}