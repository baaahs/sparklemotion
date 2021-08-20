package baaahs.sim.bridge

import baaahs.plugin.beatlink.BeatData
import baaahs.plugin.beatlink.BeatSource
import baaahs.plugin.sound_analysis.AudioInput
import baaahs.plugin.sound_analysis.SoundAnalysisPlatform
import baaahs.plugin.sound_analysis.SoundAnalyzer
import baaahs.ui.Observable
import baaahs.util.Logger
import baaahs.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import org.w3c.dom.WebSocket

class BridgeClient(private val url: String) {
    private val logger = Logger("BridgedBeatSource")
    private val json = Json
    private val defaultBpm = BeatData(0.0, 500, confidence = 1f)
    private val l = window.location
    private lateinit var webSocket: WebSocket
    private var everConnected = false
    val beatSource = BridgedBeatSource()

    private var beatData = BeatData(0.0, 0, confidence = 0f)
        set(value) {
            field = value
            beatSource.notifyChanged()
        }

    private val soundAnalysisPlatform = BridgedSoundAnalysisPlatform()

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
            val responseJson = json.parseToJsonElement(buf)
            val command = responseJson.jsonArray[0].jsonPrimitive.content
            val arg = responseJson.jsonArray[1]
            GlobalScope.launch {
                when (command) {
                    "audioInputs" -> soundAnalysisPlatform.onAudioInputs(responseJson.jsonArray)
                    "soundFrequencies" -> soundAnalysisPlatform.onSoundFrequencies(responseJson.jsonArray)
                    "soundMagnitudes" -> soundAnalysisPlatform.onSoundMagnitudes(responseJson.jsonArray)
                    "beatData" -> beatData = json.decodeFromJsonElement(BeatData.serializer(), arg)
                    else -> throw IllegalArgumentException("unknown command \"$command\"")
                }
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

    inner class BridgedBeatSource : Observable(), BeatSource {
        override fun getBeatData(): BeatData = beatData
    }

    inner class BridgedSoundAnalysisPlatform : SoundAnalysisPlatform {
        private var nextInputsRequestId: Int = 0
        private val inputsRequests = mutableMapOf<Int, Channel<List<BridgeAudioInput>>>()

        private var nextAnalyzerRequestId: Int = 0
        private val analyzers = mutableMapOf<Int, BridgedSoundAnalyzer>()

        private lateinit var soundAnalysisFrequences: FloatArray

        override suspend fun listAudioInputs(): List<AudioInput> {
            val requestId = nextInputsRequestId++
            webSocket.send(buildJsonArray { add("listAudioInputs"); add(requestId) }.toString())
            val channel = Channel<List<BridgeAudioInput>>()
            inputsRequests[requestId] = channel
            return channel.receive()
        }

        suspend fun onAudioInputs(responseJson: JsonArray) {
            val responseId = (responseJson[1] as JsonPrimitive).int
            val request = inputsRequests.remove(responseId)
            if (request == null) {
                logger.error { "Unknown request id $responseId." }
                return
            }

            val audioInputs = json.decodeFromJsonElement(
                ListSerializer(BridgeAudioInput.serializer()), responseJson[2]
            )

            GlobalScope.launch { request.send(audioInputs) }
        }

        override fun createConstantQAnalyzer(audioInput: AudioInput, sampleRate: Float): SoundAnalyzer {
            val requestId = nextAnalyzerRequestId++
            webSocket.send(buildJsonArray { add("createConstantQAnalyzer"); add(requestId) }.toString())
            return BridgedSoundAnalyzer()
        }

        fun onSoundFrequencies(frequencies: JsonArray) {
            soundAnalysisFrequences =
                frequencies.map { it.jsonPrimitive.float }.toFloatArray()
        }

        fun onSoundMagnitudes(magnitudesJson: JsonArray) {
            val magnitudes = magnitudesJson.map { it.jsonPrimitive.float }.toFloatArray()
            val analysis = SoundAnalyzer.Analysis(soundAnalysisFrequences, magnitudes)

            analyzers.forEach { (id, analyzer) ->
                analyzer.listeners.forEach {
                    it.onSample(analysis)
                }
            }
        }

    }

    inner class BridgedSoundAnalyzer : SoundAnalyzer {
        internal val listeners = mutableListOf<SoundAnalyzer.AnalysisListener>()

        override val numberOfBuckets: Int
            get() = TODO("not implemented")

        override fun listen(analysisListener: SoundAnalyzer.AnalysisListener) {
            listeners.add(analysisListener)
        }

        override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
            listeners.remove(analysisListener)
        }
    }


    companion object {
        private val logger = Logger<BridgeClient>()
    }
}
