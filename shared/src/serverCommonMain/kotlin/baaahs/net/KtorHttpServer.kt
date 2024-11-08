package baaahs.net

import baaahs.util.Logger
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.request.host
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch

abstract class AbstractKtorHttpServer(
    val applicationEngine: ApplicationEngine,
    private val link: Network.Link,
    private val port: Int,
    private val networkScope: CoroutineScope
) : Network.HttpServer {

    val application: Application get() = applicationEngine.application

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
                        get() = this@AbstractKtorHttpServer.port

                    override fun send(bytes: ByteArray) {
                        val frame = Frame.Binary(true, bytes.copyOf())
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
                    close(CloseReason(CloseReason.Codes.NORMAL, "Closed."))
                } catch (e: Exception) {
                    logger.error(e) { "Error reading websocket frame." }
                    close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error: ${e.message}"))
                } finally {
                    webSocketListener.reset(tcpConnection)
                }
            }

            webSocket("/sm/udpProxy") {
                try {
                    handleUdpProxy()
                } catch (e: Exception) {
                    logger.error(e) { "Error handling UDP proxy." }
                }
            }
        }
    }

    abstract suspend fun DefaultWebSocketServerSession.handleUdpProxy()

    override fun routing(config: Network.HttpServer.HttpRouting.() -> Unit) {
        application.routing {
            configRouting(config)
        }
    }

    abstract fun Routing.configRouting(config: Network.HttpServer.HttpRouting.() -> Unit)

    override fun start() {
        applicationEngine.start()
    }

    companion object {
        val logger = Logger<AbstractKtorHttpServer>()
    }
}