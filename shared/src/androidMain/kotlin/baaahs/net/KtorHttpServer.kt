package baaahs.net

import baaahs.net.AndroidNetwork.Companion.logger
import baaahs.net.AndroidNetwork.Companion.networkScope
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.host
import io.ktor.server.response.respondBytes
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
import java.nio.ByteBuffer
import java.time.Duration

class KtorHttpServer(
    val port: Int,
    private val link: Network.Link,
) : Network.HttpServer {
    val httpServer = embeddedServer(CIO, port, configure = {
        // Let's give brains lots of time for OTA download:
//                responseWriteTimeoutSeconds = 3000
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
        httpServer.application.routing {
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
                        networkScope.launch {
                            this@webSocket.send(frame)
                            this@webSocket.flush()
                        }
                    }

                    override fun close() {
                        networkScope.launch {
                            this@webSocket.close()
                        }
                    }
                }

                logger.info { "Connection from ${this.call.request.host()}…" }
                val webSocketListener = onConnect(tcpConnection)
                webSocketListener.connected(tcpConnection)

                try {
                    while (true) {
                        val frame = incoming.receive()
                        if (frame is Frame.Binary) {
                            val bytes = frame.readBytes()
                            webSocketListener.receive(tcpConnection, bytes)
                        } else {
                            logger.warn { "wait huh? received weird data: $frame" }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    logger.info { "Websocket closed." }
                    close(CloseReason(
                        CloseReason.Codes.NORMAL, "Closed."))
                } catch (e: Exception) {
                    logger.error(e) { "Error reading websocket frame." }
                    close(CloseReason(
                        CloseReason.Codes.INTERNAL_ERROR, "Internal error: ${e.message}"))
                } finally {
                    webSocketListener.reset(tcpConnection)
                }
            }

            webSocket("/sm/udpProxy") {
                try {
                    AndroidUdpProxy().handle(this)
                } catch (e: Exception) {
                    logger.error(e) { "Error handling UDP proxy." }
                }
            }
        }
    }

    override fun routing(config: Network.HttpServer.HttpRouting.() -> Unit) {
        application.routing {
            val route = this
            val routing = object : Network.HttpServer.HttpRouting {
                override fun get(
                    path: String,
                    handler: (Network.HttpServer.HttpRequest) -> Network.HttpResponse
                ) {
                    route.get(path) {
                        val response = handler.invoke(object : Network.HttpServer.HttpRequest {
                            override fun param(name: String): String? = call.parameters[name]
                        })
                        call.respondBytes(
                            response.body,
                            ContentType.parse(response.contentType),
                            HttpStatusCode.fromValue(response.statusCode)
                        )
                    }
                }
            }
            config.invoke(routing)
        }
    }

    override fun start() {
        httpServer.start()
    }
}
