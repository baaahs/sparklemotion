package baaahs.sim

import baaahs.BeatData
import baaahs.BeatSource
import baaahs.Logger
import baaahs.SoundAnalyzer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.w3c.dom.WebSocket
import kotlin.browser.window

class BridgeClient(private val url: String) {
    private val logger = Logger("BridgedBeatSource")
    private val json = Json
    private val defaultBpm = BeatData(0.0, 500, confidence = 1f)
    private val l = window.location
    private lateinit var webSocket: WebSocket
    private var everConnected = false
    private var beatData = BeatData(0.0, 0, confidence = 0f)

    private var soundAnalysisFrequences: FloatArray = floatArrayOf()

    val beatSource = BridgedBeatSource()
    val soundAnalyzer = BridgedSoundAnalyzer()

    inner class BridgedBeatSource : BeatSource {
        override fun getBeatData(): BeatData = beatData
    }

    inner class BridgedSoundAnalyzer : SoundAnalyzer {
        val listeners = mutableListOf<SoundAnalyzer.AnalysisListener>()

        override val frequencies: FloatArray
            get() = soundAnalysisFrequences

        override fun listen(analysisListener: SoundAnalyzer.AnalysisListener) {
            listeners.add(analysisListener)
        }

        override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
            listeners.remove(analysisListener)
        }
    }

    init {
        connect()
    }

    private fun connect() {
        webSocket = WebSocket("${if (l.protocol == "https:") "wss:" else "ws:"}//$url/bridge")

        webSocket.onopen = {
            everConnected = true
            logger.info { "Connected to simulator bridge." }
        }

        webSocket.onmessage = {
            val buf = it.data as String
//            logger.debug { "Received $buf" }
            val jsonCmd = json.parseToJsonElement(buf)
            val command = jsonCmd.jsonArray[0].jsonPrimitive.content
            val arg = jsonCmd.jsonArray[1]
            when (command) {
                "soundFrequencies" -> soundAnalysisFrequences = arg.jsonArray.map { it.jsonPrimitive.float }.toFloatArray()
                "soundMagnitudes" -> {
                    val magnitudes = arg.jsonArray.map { it.jsonPrimitive.float }.toFloatArray()
                    val analysis = SoundAnalyzer.Analysis(soundAnalysisFrequences, magnitudes)
                    soundAnalyzer.listeners.forEach {
                        it.onSample(analysis)
                    }
                }
                "beatData" -> beatData = json.decodeFromJsonElement(BeatData.serializer(), arg)
                else -> throw IllegalArgumentException("unknown command \"$command\"")
            }

            null
        }

        webSocket.onerror = {
            if (!everConnected) {
                logger.error { "Couldn't connect to simulator bridge; falling back to 120bpm: $it" }
                beatData = defaultBpm
            } else {
                logger.error { "WebSocket error: $it" }
            }
        }

        webSocket.onclose = {
            if (everConnected) {
                logger.error { "Lost connection to simulator bridge; falling back to 120bpm: $it" }
                beatData = defaultBpm

                GlobalScope.launch {
                    delay(1000)
                    logger.info { "Attempting to reconnect to simulator bridge..." }
                    connect()
                }
            }
        }
    }
}
