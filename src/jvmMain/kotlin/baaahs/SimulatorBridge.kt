package baaahs

import baaahs.plugin.beatlink.BeatLinkBeatSource
import baaahs.plugin.sound_analysis.JvmSoundAnalysisPlatform
import baaahs.proto.Ports
import baaahs.sim.bridge.BridgeAudioInput
import baaahs.util.SystemClock
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import java.time.Duration

object SimulatorBridge {
    private val json = Json
    private val webSocketConnections = mutableListOf<WebSocketServerSession>()

    private val soundAnalysisPlatform = JvmSoundAnalysisPlatform()

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

                sendFrequencies(this)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val jsonCommand =
                                json.decodeFromString(JsonElement.serializer(), frame.readText()) as JsonArray
                            val opcode = jsonCommand[0]

                            when (opcode.jsonPrimitive.contentOrNull) {
                                "listAudioInputs" -> {
                                    val inputs = soundAnalysisPlatform.listAudioInputs().map { audioInput ->
                                        BridgeAudioInput(audioInput.id, audioInput.title)
                                    }
                                    val inputsJson = json.encodeToString(
                                        ListSerializer(BridgeAudioInput.serializer()),
                                        inputs
                                    )
                                    send(toWsMessage("onAudioInputs", buildJsonObject {  }))
                                }
                                "bye" -> {
                                    close(CloseReason(CloseReason.Codes.NORMAL, "Client said \"bye\""))
                                    webSocketConnections.remove(this)
                                }
                                else -> error("Unknown bridge command \"$opcode\"")
                            }
                        }
                        else -> error("huh?")
                    }
                }

                webSocketConnections.remove(this)
            }
        }
    }

    private fun sendFrequencies(connection: WebSocketServerSession) {
//        connection.outgoing.send(
//            Frame.Text(
//                toWsMessage(
//                    "soundFrequencies",
//                    json.encodeToJsonElement(ListSerializer(Float.serializer()), soundAnalyzer.frequencies.toList())
//                )
//            )
//        )
    }

    fun run() {
        val beatLinkBeatSource = BeatLinkBeatSource(SystemClock)
//        beatLinkBeatSource.addObserver {
//            val beatData = beatLinkBeatSource.getBeatData()
//            sendToClients("beatData", json.encodeToJsonElement(BeatData.serializer(), beatData))
//        }
        beatLinkBeatSource.start()

//        soundAnalyzer.listen(object : SoundAnalyzer.AnalysisListener {
//            override fun onSample(analysis: SoundAnalyzer.Analysis) {
//                // todo: don't send more frequently than framerate
//                sendToClients(
//                    "soundMagnitudes",
//                    json.encodeToJsonElement(ListSerializer(Float.serializer()), analysis.magnitudes.toList())
//                )
//            }
//        })

        httpServer.start(true)
    }

    private fun sendToClients(command: String, json: JsonElement) {
        val frame = toWsMessage(command, json)
        webSocketConnections.forEach { it.outgoing.offer(Frame.Text(frame)) }
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