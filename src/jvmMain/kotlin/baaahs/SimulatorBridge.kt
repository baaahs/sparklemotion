package baaahs

import baaahs.net.JvmNetwork
import baaahs.net.Network
import baaahs.plugin.OpenBridgePlugin
import baaahs.plugin.toWsMessage
import baaahs.proto.Ports
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.cio.websocket.CloseReason.Codes.*
import io.ktor.http.cio.websocket.Frame.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.nio.ByteBuffer
import java.time.Duration

class SimulatorBridge {
    private val webSocketConnections = mutableListOf<WebSocketServerSession>()
    private val plugins = Pluggables.plugins.mapNotNull {
        val simulatorPlugin = it.openForSimulator()
        simulatorPlugin.getBridgePlugin()
    }

    private val soundAnalyzer = JvmSoundAnalyzer()

    private val httpServer = embeddedServer(Netty, Ports.SIMULATOR_BRIDGE_TCP) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            webSocket("/bridge") {
                println("Connection from ${this.call.request.host()}…")
                webSocketConnections.add(this)

                val tcpConnection = object : Network.TcpConnection {
                    override val fromAddress: Network.Address get() = TODO("not implemented")
                    override val toAddress: Network.Address get() = TODO("not implemented")
                    override val port: Int get() = TODO("not implemented")

                    override fun send(bytes: ByteArray) {
                        val frame = Binary(true, ByteBuffer.wrap(bytes.clone()))
                        JvmNetwork.networkScope.launch {
                            this@webSocket.send(frame)
                            this@webSocket.flush()
                        }
                    }

                    override fun close() {
                        JvmNetwork.networkScope.launch {
                            this@webSocket.close()
                        }
                    }
                }

                plugins.forEach { bridgePlugin ->
                    bridgePlugin.onConnectionOpen(tcpConnection)
                }

                sendFrequencies(this)

                for (frame in incoming) {
                    when (frame) {
                        is Text -> {
                            val text = frame.readText()
                            if (text.equals("bye", ignoreCase = true)) {
                                close(CloseReason(NORMAL, "Client said BYE"))
                                webSocketConnections.remove(this)
                            }
                        }
                        else -> error("Unsupported frame type ${frame::class}")
                    }
                }

                plugins.forEach { bridgePlugin ->
                    bridgePlugin.onConnectionClose(tcpConnection)
                }

                webSocketConnections.remove(this)
            }
        }
    }

    private fun sendFrequencies(connection: WebSocketServerSession) {
        connection.outgoing.trySend(
            Text(
                toWsMessage(
                    "soundFrequencies",
                    OpenBridgePlugin.json.encodeToJsonElement(
                        ListSerializer(Float.serializer()),
                        soundAnalyzer.frequencies.toList()
                    )
                )
            )
        ).isSuccess
    }

    fun run() {
        soundAnalyzer.listen(object : SoundAnalyzer.AnalysisListener {
            override fun onSample(analysis: SoundAnalyzer.Analysis) {
                // todo: don't send more frequently than framerate
//                sendToClients(
//                    "soundMagnitudes",
//                    json.encodeToJsonElement(ListSerializer(Float.serializer()), analysis.magnitudes.toList())
//                )
            }
        })

        httpServer.start(true)
    }
}

fun main() {
    SimulatorBridge().run()
}