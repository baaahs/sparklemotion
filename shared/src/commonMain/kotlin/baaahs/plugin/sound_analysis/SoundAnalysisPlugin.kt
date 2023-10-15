package baaahs.plugin.sound_analysis

import baaahs.PubSub
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.dialog.DialogPanel
import baaahs.clamp
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.internalTimerClock
import baaahs.plugin.*
import baaahs.rpc.Service
import baaahs.show.Control
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.sim.BridgeClient
import baaahs.util.*
import com.danielgergely.kgl.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlin.math.PI
import kotlin.math.tan

class SoundAnalysisPlugin internal constructor(
    val soundAnalyzer: SoundAnalyzer,
    val historySize: Int = 128
) : OpenServerPlugin, OpenClientPlugin {

    override val packageName: String = id
    override val title: String = "Sound Analysis"

    override val addControlMenuItems: List<AddControlMenuItem>
        get() = listOf(
            AddControlMenuItem("New Sound Analysis Control…", CommonIcons.SoundAnalysisControl) {
                MutableSoundAnalysisControl()
            }
        )

    override val contentTypes: List<ContentType>
        get() = feedBuilders.map { it.contentType }

    override val controlSerializers: List<SerializerRegistrar<out Control>>
        get() = listOf(
            classSerializer(SoundAnalysisControl.serializer())
        )

    override val feedBuilders: List<FeedBuilder<out Feed>> =
        listOf(SoundAnalysisFeedBuilder())

    internal val feed = SoundAnalysisFeed()

    override fun getSettingsPanel(): DialogPanel = SoundAnalysisSettingsPanel()

    @SerialName("baaahs.SoundAnalysis:SoundAnalysis")
    inner class SoundAnalysisFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "SoundAnalysis"
        override val contentType: ContentType get() = soundAnalysisContentType
        override fun getType(): GlslType = soundAnalysisStruct

        override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext =
            SoundAnalysisFeedContext(getVarName(id), soundAnalyzer, historySize)
    }

    inner class SoundAnalysisFeedBuilder : FeedBuilder<SoundAnalysisFeed> {
        override val title: String get() = "Sound Analysis"
        override val description: String get() = "Spectral analysis of sound input."
        override val resourceName: String get() = "SoundAnalysis"
        override val contentType: ContentType get() = soundAnalysisContentType
        override val serializerRegistrar get() = objectSerializer("$id:$resourceName", feed)

        override fun build(inputPort: InputPort): SoundAnalysisFeed = feed
    }

    companion object : Plugin, SimulatorPlugin {
        override val id = "baaahs.SoundAnalysis"

        private val commandsRpc = SoundAnalysisCommands.getImpl("plugins/$id")

        private val logger = Logger<SoundAnalysisPlugin>()

        val soundAnalysisStruct = GlslType.Struct(
            "SoundAnalysis",
            "bucketCount" to GlslType.Int,
            "sampleHistoryCount" to GlslType.Int,
            "buckets" to GlslType.Sampler2D,
            "maxMagnitude" to GlslType.Float
        )

        val soundAnalysisContentType = ContentType("sound-analysis", "Sound Analysis", soundAnalysisStruct)

        override fun openForServer(pluginContext: PluginContext): OpenServerPlugin {
            val soundAnalyzer = createServerSoundAnalyzer(pluginContext)
            PubSubPublisher(soundAnalyzer, pluginContext) // Yuck.
            return SoundAnalysisPlugin(soundAnalyzer)
        }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin {
            return SoundAnalysisPlugin(PubSubSubscriber(pluginContext.pubSub))
        }

        override fun openForSimulator(): OpenSimulatorPlugin =
            object : OpenSimulatorPlugin {
                override fun getBridgePlugin(pluginContext: PluginContext): OpenBridgePlugin =
                    PubSubPublisher(createServerSoundAnalyzer(pluginContext), pluginContext)

                override fun getServerPlugin(pluginContext: PluginContext, bridgeClient: BridgeClient): SoundAnalysisPlugin {
                    val soundAnalyzer = PubSubSubscriber(bridgeClient.pubSub, true)
                    PubSubPublisher(soundAnalyzer, pluginContext)
                    return SoundAnalysisPlugin(soundAnalyzer)
                }

                override fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin =
                    openForClient(pluginContext)
            }

        private val inputsTopic = PubSub.Topic("plugins/${id}/inputs", ListSerializer(AudioInput.serializer()))
        private val currentInputTopic = PubSub.Topic("plugins/${id}/currentInput", AudioInput.serializer().nullable)
    }

    @Serializable
    class AnalysisData(val magnitudes: FloatArray, val timestamp: Time)

    class PubSubPublisher(
        soundAnalyzer: SoundAnalyzer,
        pluginContext: PluginContext
    ) : OpenBridgePlugin {
        private var audioInputs: List<AudioInput> = soundAnalyzer.listAudioInputs()
        private var currentInput: AudioInput? = soundAnalyzer.currentAudioInput

        private val pubSub = pluginContext.pubSub

        private val inputsChannel = pubSub.openChannel(inputsTopic, audioInputs) {
            error("Huh? Don't update inputs!")
        }

        private val currentInputChannel = pubSub.openChannel(currentInputTopic, soundAnalyzer.currentAudioInput) {
            error("Not allowed!")
        }

        init {
            soundAnalyzer.listen { inputs: List<AudioInput>, currentInput: AudioInput? ->
                if (audioInputs != inputs) {
                    audioInputs = inputs
                    inputsChannel.onChange(inputs)
                }

                if (this.currentInput != currentInput) {
                    this.currentInput = currentInput
                    currentInputChannel.onChange(currentInput)
                }
            }
        }

        private var magnitudes = floatArrayOf()
        private var magnitudesVersion = 0
        private var frequencies = floatArrayOf()
        private var frequenciesVersion = 0
        private var updateChannel = CompletableDeferred<Unit>()

        init {
            soundAnalyzer.listen { analysis: SoundAnalyzer.Analysis ->
                if (!analysis.magnitudes.contentEquals(magnitudes)) {
                    magnitudes = analysis.magnitudes
                    magnitudesVersion++
                }

                if (!analysis.frequencies.contentEquals(frequencies)) {
                    frequencies = analysis.frequencies
                    frequenciesVersion++
                }

                globalLaunch {
                    val oldUpdateChannel = updateChannel
                    updateChannel = CompletableDeferred()
                    oldUpdateChannel.complete(Unit)
                }
            }
        }

        init {
            suspend fun doUpdate(newFrequenciesVersion: Int?, newMagnitudesVersion: Int?): UpdateResponse {
                while (newFrequenciesVersion == frequenciesVersion && newMagnitudesVersion == magnitudesVersion) {
                    val theUpdateChannel = updateChannel
                    theUpdateChannel.await()
                }

                return if (newFrequenciesVersion == frequenciesVersion) {
                    UpdateResponse(null, null, magnitudesVersion, magnitudes)
                } else {
                    UpdateResponse(frequenciesVersion, frequencies, magnitudesVersion, magnitudes)
                }
            }

            commandsRpc.createReceiver(pubSub, object : SoundAnalysisCommands {
                override suspend fun switchTo(audioInput: AudioInput?) =
                    soundAnalyzer.switchTo(audioInput)

                override suspend fun update(frequenciesVersion: Int?, magnitudesVersion: Int?) =
                    doUpdate(frequenciesVersion, magnitudesVersion)
            })
        }
    }

    class PubSubSubscriber(private val pubSub: PubSub.Endpoint, val generateData: Boolean? = null) : SoundAnalyzer {
        override var currentAudioInput: AudioInput? = null
            private set
        private var audioInputs: List<AudioInput> = emptyList()

        private var magnitudes: FloatArray = floatArrayOf()
        private var magnitudesVersion = -1
        private var frequencies: FloatArray = floatArrayOf()
        private var frequenciesVersion = -1
        private var sampleTimestamp: Time = 0.0

        private val listeners = mutableSetOf<SoundAnalyzer.AnalysisListener>()
        private val inputsListeners = mutableSetOf<SoundAnalyzer.InputsListener>()

        private val currentInputChannel: PubSub.Channel<AudioInput?>
        private val rpcClient = commandsRpc.createSender(pubSub)

        init {
            pubSub.openChannel(inputsTopic, audioInputs) { inputs ->
                audioInputs = inputs
                notifyChanged()
            }

            currentInputChannel = pubSub.openChannel(currentInputTopic, null) { input ->
                currentAudioInput = input
                notifyChanged()
            }

            globalLaunch { requestUpdate() }
        }

        private suspend fun requestUpdate() {
            while (!(pubSub as PubSub.Client).isConnected) {
                if (generateData == true) {
                    generateRandomData()
                    continue
                }
                delay(100)
            }

            val response = rpcClient.update(frequenciesVersion, magnitudesVersion)
            if (response.frequenciesVersion != null &&
                response.frequencies != null &&
                response.frequenciesVersion != frequenciesVersion
            ) {
                frequencies = response.frequencies
                frequenciesVersion = response.frequenciesVersion
            }

            if (response.magnitudesVersion != magnitudesVersion) {
                magnitudes = response.magnitudes
                magnitudesVersion = response.magnitudesVersion
            }

            sendSample()
            requestUpdate()
        }

        private fun notifyChanged() {
            inputsListeners.forEach { it.onChange(audioInputs, currentAudioInput) }
        }

        private fun sendSample() {
            if (frequencies.isNotEmpty() && magnitudes.isNotEmpty()) {
                val analysis = SoundAnalyzer.Analysis(frequencies, magnitudes, sampleTimestamp)
                listeners.forEach { it.onSample(analysis) }
            }
        }

        override fun listAudioInputs(): List<AudioInput> = audioInputs

        override suspend fun switchTo(audioInput: AudioInput?) {
            rpcClient.switchTo(audioInput)
        }

        override fun listen(analysisListener: SoundAnalyzer.AnalysisListener): SoundAnalyzer.AnalysisListener {
            listeners.add(analysisListener)
            return analysisListener
        }

        override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
            listeners.remove(analysisListener)
        }

        override fun listen(inputsListener: SoundAnalyzer.InputsListener): SoundAnalyzer.InputsListener {
            inputsListeners.add(inputsListener)
            return inputsListener
        }

        override fun unlisten(inputsListener: SoundAnalyzer.InputsListener) {
            inputsListeners.remove(inputsListener)
        }

        private suspend fun generateRandomData() {
            val buckets = 4
            frequencies = (0 until buckets).map {
                it.toFloat() * 20f
            }.toFloatArray()
            val t = internalTimerClock.now().asDoubleSeconds.toFloat()
            magnitudes = arrayOf(t * 2, t * 3, t * 4, t * 8).map {
                tan(it * PI).clamp(0.0, 1.0).toFloat()
            }.map { it / buckets } // pre-compensate for normalization
                .toFloatArray()
            sendSample()
            delay(20)
        }
    }
}

class SoundAnalysisFeedContext(
    private val varPrefix: String,
    private val soundAnalyzer: SoundAnalyzer,
    private val historySize: Int
) : FeedContext, RefCounted by RefCounter(), SoundAnalyzer.AnalysisListener {
    private var bucketCount = 0
    private var sampleBuffer = FloatArray(0)
    private var textureBuffer = FloatBuffer(sampleBuffer)
    private var maxMagnitude = 0f

    init { soundAnalyzer.listen(this) }

    override fun onSample(analysis: SoundAnalyzer.Analysis) {
        if (analysis.frequencies.size != bucketCount) {
            bucketCount = analysis.frequencies.size
            val bufferSize = bucketCount * historySize
            sampleBuffer = FloatArray(bufferSize)
        }
        if (bucketCount == 0) {
            return
        }

        // Shift historical data down one row.
        sampleBuffer.copyInto(sampleBuffer, bucketCount, 0, bucketCount * historySize - bucketCount)

        // Copy this sample's data into the buffer.
        var max = 0f
        analysis.magnitudes.forEachIndexed { index, magnitude ->
            val normalizedMagnitude = magnitude * bucketCount
            sampleBuffer[index] = normalizedMagnitude
            if (normalizedMagnitude > max) max = normalizedMagnitude
        }
        try {
            textureBuffer.position = 0
            textureBuffer = FloatBuffer(sampleBuffer)
        } catch (e: Exception) {
//            println("texture buffer size == ${textureBuffer.size}")
            println("sample buffer size == ${sampleBuffer.size}")
            e.printStackTrace()
        }
        maxMagnitude = max
    }

    override fun bind(gl: GlContext): EngineFeedContext = SoundAnalysisEngineFeedContext(gl)

    inner class SoundAnalysisEngineFeedContext(private val gl: GlContext) : EngineFeedContext {
        private val texture = gl.check { createTexture() }

        init { gl.checkForLinearFilteringOfFloatTextures() }

        override fun bind(glslProgram: GlslProgram): ProgramFeedContext = object : ProgramFeedContext {
            val bucketCountUniform = glslProgram.getIntUniform("${varPrefix}.bucketCount")
            val sampleHistoryCountUniform = glslProgram.getIntUniform("${varPrefix}.sampleHistoryCount")
            val bucketsUniform = glslProgram.getTextureUniform("${varPrefix}.buckets")
            val maxMagnitudeUniform = glslProgram.getFloatUniform("${varPrefix}.maxMagnitude")

            override val isValid: Boolean
                get() = bucketCountUniform != null ||
                        sampleHistoryCountUniform != null ||
                        bucketsUniform != null ||
                        maxMagnitudeUniform != null

            override fun setOnProgram() {
                if (bucketCount == 0 || historySize == 0) return

                bucketCountUniform?.set(bucketCount)
                sampleHistoryCountUniform?.set(historySize)
                with(gl) {
                    texture.configure(GL_NEAREST, GL_NEAREST)
                    texture.upload(0, GL_R32F, bucketCount, historySize, 0, GL_RED, GL_FLOAT, textureBuffer)
                }
                bucketsUniform?.set(texture)
                maxMagnitudeUniform?.set(maxMagnitude)
            }
        }

        override fun release() {
            gl.check { deleteTexture(texture) }
        }
    }

    override fun onRelease() {
        soundAnalyzer.unlisten(this)
    }

    companion object {
        private val logger = Logger<SoundAnalysisFeedContext>()
    }
}

@Service
interface SoundAnalysisCommands {
    suspend fun switchTo(audioInput: AudioInput?)
    suspend fun update(frequenciesVersion: Int?, magnitudesVersion: Int?): UpdateResponse

    companion object
}

@Serializable
class UpdateResponse(
    val frequenciesVersion: Int? = null,
    val frequencies: FloatArray? = null,
    val magnitudesVersion: Int,
    val magnitudes: FloatArray
)

@Serializable
data class AudioInput(
    val id: String,
    val title: String
)

internal expect fun createServerSoundAnalyzer(pluginContext: PluginContext): SoundAnalyzer
