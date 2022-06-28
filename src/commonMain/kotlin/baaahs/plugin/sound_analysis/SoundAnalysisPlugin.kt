package baaahs.plugin.sound_analysis

import baaahs.PubSub
import baaahs.ShowPlayer
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.dialog.DialogPanel
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.*
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.sim.BridgeClient
import baaahs.util.*
import com.danielgergely.kgl.*
import kotlinx.cli.ArgParser
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

class SoundAnalysisPlugin internal constructor(
    val soundAnalyzer: SoundAnalyzer,
    val historySize: Int = 100
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
        get() = dataSourceBuilders.map { it.contentType }

    override val controlSerializers: List<SerializerRegistrar<out Control>>
        get() = listOf(
            classSerializer(SoundAnalysisControl.serializer())
        )

    override val dataSourceBuilders: List<DataSourceBuilder<out DataSource>> =
        listOf(SoundAnalysisDataSourceBuilder())

    internal val dataSource = SoundAnalysisDataSource()

    override fun getSettingsPanel(): DialogPanel = SoundAnalysisSettingsPanel()

    @SerialName("baaahs.SoundAnalysis:SoundAnalysis")
    inner class SoundAnalysisDataSource internal constructor() : DataSource {
        override val pluginPackage: String get() = id
        override val title: String get() = "SoundAnalysis"
        override val contentType: ContentType get() = soundAnalysisContentType
        override fun getType(): GlslType = soundAnalysisStruct

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            SoundAnalysisFeed(getVarName(id), soundAnalyzer, historySize)
    }

    inner class SoundAnalysisDataSourceBuilder : DataSourceBuilder<SoundAnalysisDataSource> {
        override val title: String get() = "Sound Analysis"
        override val description: String get() = "Spectral analysis of sound input."
        override val resourceName: String get() = "SoundAnalysis"
        override val contentType: ContentType get() = soundAnalysisContentType
        override val serializerRegistrar get() = objectSerializer("$id:$resourceName", dataSource)

        override fun build(inputPort: InputPort): SoundAnalysisDataSource = dataSource
    }

    companion object : Plugin<Args>, SimulatorPlugin {
        override val id = "baaahs.SoundAnalysis"

        private val logger = Logger<SoundAnalysisPlugin>()

        val soundAnalysisStruct = GlslType.Struct(
            "SoundAnalysis",
            "bucketCount" to GlslType.Int,
            "sampleHistoryCount" to GlslType.Int,
            "buckets" to GlslType.Sampler2D,
            "maxMagnitude" to GlslType.Float
        )

        val soundAnalysisContentType = ContentType("sound-analysis", "Sound Analysis", soundAnalysisStruct)

        override fun getArgs(parser: ArgParser): Args = Args(parser)

        override fun openForServer(pluginContext: PluginContext, args: Args): OpenServerPlugin {
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
                    val soundAnalyzer = PubSubSubscriber(bridgeClient.pubSub)
                    PubSubPublisher(soundAnalyzer, pluginContext)
                    return SoundAnalysisPlugin(soundAnalyzer)
                }

                override fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin =
                    openForClient(pluginContext)
            }

        private val inputsTopic = PubSub.Topic("plugins/${id}/inputs", ListSerializer(AudioInput.serializer()))
        private val currentInputTopic = PubSub.Topic("plugins/${id}/currentInput", AudioInput.serializer().nullable)

        private val switchToCommand = PubSub.CommandPort(
            "plugins/${id}/switchTo",
            SwitchToCommand.serializer(),
            Unit.serializer()
        )

        @Serializable
        class SwitchToCommand(val audioInput: AudioInput?)

        private val updateCommand = PubSub.CommandPort(
            "plugins/${id}/update",
            UpdateCommand.serializer(),
            UpdateCommand.Response.serializer()
        )

        @Serializable
        class UpdateCommand(val frequenciesVersion: Int?, val magnitudesVersion: Int?) {
            @Serializable
            class Response(
                val frequenciesVersion: Int? = null,
                val frequencies: FloatArray? = null,
                val magnitudesVersion: Int,
                val magnitudes: FloatArray
            )
        }
    }

    @Serializable
    class AnalysisData(val magnitudes: FloatArray, val timestamp: Time)

    class Args(parser: ArgParser) {
//        val audioInput by parser.option(ArgType.String, description = "Audio input for spectral analysys")
    }

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

            pubSub.listenOnCommandChannel(switchToCommand) { command ->
                soundAnalyzer.switchTo(command.audioInput)
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

            pubSub.listenOnCommandChannel(updateCommand) { command ->
                while (command.frequenciesVersion == frequenciesVersion && command.magnitudesVersion == magnitudesVersion) {
                    val theUpdateChannel = updateChannel
                    theUpdateChannel.await()
                }

                if (command.frequenciesVersion == frequenciesVersion) {
                    UpdateCommand.Response(null, null, magnitudesVersion, magnitudes)
                } else {
                    UpdateCommand.Response(frequenciesVersion, frequencies, magnitudesVersion, magnitudes)
                }
            }
        }
    }

    class PubSubSubscriber(private val pubSub: PubSub.Endpoint) : SoundAnalyzer {
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
        private val switchTo = (pubSub as PubSub.Client).commandSender(switchToCommand)
        private val update = (pubSub as PubSub.Client).commandSender(updateCommand)

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
            while (!(pubSub as PubSub.Client).isConnected) delay(100)

            val response = update.invoke(UpdateCommand(frequenciesVersion, magnitudesVersion))
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
            switchTo.invoke(SwitchToCommand(audioInput))
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
    }
}

class SoundAnalysisFeed(
    private val varPrefix: String,
    private val soundAnalyzer: SoundAnalyzer,
    private val historySize: Int
) : Feed, RefCounted by RefCounter(), SoundAnalyzer.AnalysisListener {
    private var bucketCount = 0
    private var textureBuffer = FloatArray(0)
    private var maxMagnitude = 0f

    init { soundAnalyzer.listen(this) }

    override fun onSample(analysis: SoundAnalyzer.Analysis) {
        if (analysis.frequencies.size != bucketCount) {
            bucketCount = analysis.frequencies.size

            val bufferSize = bucketCount * historySize
            textureBuffer = FloatArray(bufferSize)
        }

        // Shift historical data down one row.
        textureBuffer.copyInto(textureBuffer, bucketCount, 0, bucketCount * historySize - bucketCount)

        // Copy this sample's data into the buffer.
        var max = 0f
        analysis.magnitudes.forEachIndexed { index, magnitude ->
            val normalizedMagnitude = magnitude * bucketCount
            textureBuffer[index] = normalizedMagnitude
            if (normalizedMagnitude > max) max = normalizedMagnitude
        }
        maxMagnitude = max
    }

    override fun bind(gl: GlContext): EngineFeed = SoundAnalysisEngineFeed(gl)

    inner class SoundAnalysisEngineFeed(private val gl: GlContext) : EngineFeed {
        private val textureUnit = gl.getTextureUnit(SoundAnalysisPlugin)
        private val texture = gl.check { createTexture() }

        init { gl.checkForLinearFilteringOfFloatTextures() }

        override fun bind(glslProgram: GlslProgram): ProgramFeed = object : ProgramFeed {
            val bucketCountUniform = glslProgram.getUniform("${varPrefix}.bucketCount")
            val sampleHistoryCountUniform = glslProgram.getUniform("${varPrefix}.sampleHistoryCount")
            val bucketsUniform = glslProgram.getUniform("${varPrefix}.buckets")
            val maxMagnitudeUniform = glslProgram.getUniform("${varPrefix}.maxMagnitude")

            override val isValid: Boolean
                get() = bucketCountUniform != null ||
                        sampleHistoryCountUniform != null ||
                        bucketsUniform != null ||
                        maxMagnitudeUniform != null

            override fun setOnProgram() {
                if (bucketCount == 0 || historySize == 0) return

                bucketCountUniform?.set(bucketCount)
                sampleHistoryCountUniform?.set(historySize)
                with(textureUnit) {
                    bindTexture(texture)
                    configure(GL_LINEAR, GL_LINEAR)
                    uploadTexture(
                        0, GL_R32F, bucketCount, historySize, 0,
                        GL_RED, GL_FLOAT, FloatBuffer(textureBuffer)
                    )
                }
                bucketsUniform?.set(textureUnit)
                maxMagnitudeUniform?.set(maxMagnitude)
            }
        }

        override fun release() {
            gl.check { deleteTexture(texture) }
            textureUnit.release()
        }
    }

    override fun onRelease() {
        soundAnalyzer.unlisten(this)
    }

    companion object {
        private val logger = Logger<SoundAnalysisFeed>()
    }
}

@Serializable
data class AudioInput(
    val id: String,
    val title: String
)

internal expect fun createServerSoundAnalyzer(pluginContext: PluginContext): SoundAnalyzer
