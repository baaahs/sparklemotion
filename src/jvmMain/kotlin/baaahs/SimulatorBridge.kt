package baaahs

import baaahs.proto.Ports
import io.ktor.application.install
import io.ktor.http.cio.websocket.*
import io.ktor.http.cio.websocket.CloseReason.Codes.NORMAL
import io.ktor.http.cio.websocket.Frame.Text
import io.ktor.request.host
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSocketServerSession
import io.ktor.websocket.webSocket
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import java.time.Duration

object SimulatorBridge {
    private val json = Json(JsonConfiguration.Stable)
    private val webSocketConnections = mutableListOf<WebSocketServerSession>()

    private val soundAnalyzer = JvmSoundAnalyzer()

    private val httpServer = embeddedServer(Netty, Ports.SIMULATOR_BRIDGE_TCP) {
        install(io.ktor.websocket.WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            webSocket("/bridge") {
                println("Connection from ${this.call.request.host()}…")
                webSocketConnections.add(this)

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
                    }
                }

                webSocketConnections.remove(this)
            }
        }
    }

    private fun sendFrequencies(connection: WebSocketServerSession) {
        connection.outgoing.offer(
            Text(toWsMessage(
                "soundFrequencies",
                json.toJson(Float.serializer().list, soundAnalyzer.frequencies.toList())
            ))
        )
    }

    fun run() {
        val beatLinkBeatSource = BeatLinkBeatSource(SystemClock())
        beatLinkBeatSource.listen { beatData ->
            sendToClients("beatData", json.toJson(BeatData.serializer(), beatData))
        }
        beatLinkBeatSource.start()

        soundAnalyzer.listen(object : SoundAnalyzer.AnalysisListener {
            override fun onSample(analysis: SoundAnalyzer.Analysis) {
                // todo: don't send more frequently than framerate
                sendToClients(
                    "soundMagnitudes",
                    json.toJson(Float.serializer().list, analysis.magnitudes.toList())
                )
            }
        })

        httpServer.start(true)
    }

    private fun sendToClients(command: String, json: JsonElement) {
        val frame = toWsMessage(command, json)
        webSocketConnections.forEach { it.outgoing.offer(Text(frame)) }
    }

    private fun toWsMessage(command: String, json: JsonElement): String {
        return SimulatorBridge.json.stringify(JsonElementSerializer, jsonArray {
            +command
            +json
        })
    }
}

fun main() {
    SimulatorBridge.run()
}