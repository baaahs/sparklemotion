package baaahs

import baaahs.plugin.beatlink.BeatData
import baaahs.plugin.beatlink.BeatLinkBeatSource
import baaahs.proto.Ports
import baaahs.ui.addObserver
import baaahs.util.SystemClock
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.cio.websocket.CloseReason.Codes.*
import io.ktor.http.cio.websocket.Frame.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import java.time.Duration

object SimulatorBridge {
    private val json = Json
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
                json.encodeToJsonElement(ListSerializer(Float.serializer()), soundAnalyzer.frequencies.toList())
            ))
        )
    }

    fun run() {
        val beatLinkBeatSource = BeatLinkBeatSource(SystemClock)
        beatLinkBeatSource.addObserver {
            val beatData = beatLinkBeatSource.getBeatData()
            sendToClients("beatData", json.encodeToJsonElement(BeatData.serializer(), beatData))
        }
        beatLinkBeatSource.start()

        soundAnalyzer.listen(object : SoundAnalyzer.AnalysisListener {
            override fun onSample(analysis: SoundAnalyzer.Analysis) {
                // todo: don't send more frequently than framerate
                sendToClients(
                    "soundMagnitudes",
                    json.encodeToJsonElement(ListSerializer(Float.serializer()), analysis.magnitudes.toList())
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
        return SimulatorBridge.json.encodeToString(JsonElement.serializer(), buildJsonArray {
            add(command)
            add(json)
        })
    }
}

fun main() {
    SimulatorBridge.run()
}