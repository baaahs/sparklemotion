package baaahs

import baaahs.proto.Ports
import io.ktor.application.install
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.CloseReason.Codes.NORMAL
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.Frame.Text
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.request.host
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSocketServerSession
import io.ktor.websocket.webSocket
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.time.Duration

object SimulatorBridge {
    private val json = Json(JsonConfiguration.Stable)
    val webSocketConnections = mutableListOf<WebSocketServerSession>()

    val httpServer = embeddedServer(Netty, Ports.SIMULATOR_BRIDGE_TCP) {
        install(io.ktor.websocket.WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            webSocket("/bridge/beatSource") {
                println("Connection from ${this.call.request.host()}…")
                webSocketConnections.add(this)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            if (text.equals("bye", ignoreCase = true)) {
                                close(CloseReason(NORMAL, "Client said BYE"))
                                webSocketConnections.remove(this)
                            }
                        }
                    }
                }

                webSocketConnections.remove(this)
            }
        }
    }

    fun run() {
        val beatLinkBeatSource = BeatLinkBeatSource(SystemClock())
        beatLinkBeatSource.listen { beatData ->
            webSocketConnections.forEach {
                it.outgoing.offer(Text(json.stringify(BeatData.serializer(), beatData)))
            }
        }
        beatLinkBeatSource.start()

        httpServer.start(true)
    }
}

fun main() {
    SimulatorBridge.run()
}