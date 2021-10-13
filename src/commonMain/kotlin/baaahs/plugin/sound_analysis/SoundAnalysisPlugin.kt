package baaahs.plugin.sound_analysis

import baaahs.PubSub
import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.*
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.util.Logger
import baaahs.util.Time
import com.danielgergely.kgl.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable

class SoundAnalysisPlugin internal constructor(
    val soundAnalyzer: SoundAnalyzer,
    val historySize: Int = 300
) : OpenServerPlugin, OpenClientPlugin {

    override val packageName: String = id
    override val title: String = "Sound Analysis"

    override val contentTypes: List<ContentType>
        get() = dataSourceBuilders.map { it.contentType }

    override val dataSourceBuilders: List<DataSourceBuilder<out DataSource>>
        get() = listOf(
            object : DataSourceBuilder<SoundAnalysisDataSource> {
                override val title: String get() = "Sound Analysis"
                override val description: String get() = "Spectral analysis of sound input."
                override val resourceName: String get() = "SoundAnalysis"
                override val contentType: ContentType get() = soundAnalysisContentType
                override val serializerRegistrar get() = objectSerializer("$id:$resourceName", dataSource)

                override fun build(inputPort: InputPort): SoundAnalysisDataSource = dataSource
            }
        )

    internal val dataSource = SoundAnalysisDataSource()

    @SerialName("baaahs.SoundAnalysis:SoundAnalysis")
    inner class SoundAnalysisDataSource internal constructor() : DataSource {
        override val pluginPackage: String get() = id
        override val title: String get() = "SoundAnalysis"
        override val contentType: ContentType get() = soundAnalysisContentType
        override fun getType(): GlslType = soundAnalysisStruct

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            SoundAnalysisFeed(getVarName(id), soundAnalyzer, historySize)
    }

    companion object : Plugin<Args> /*, SimulatorPlugin*/ {
        private val logger = Logger<SoundAnalysisPlugin>()

        override val id = "baaahs.SoundAnalysis"

        val soundAnalysisStruct = GlslType.Struct(
            "SoundAnalysis",
            "bucketCount" to GlslType.Int,
            "sampleHistoryCount" to GlslType.Int,
            "buckets" to GlslType.Sampler2D
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

//        override fun openForSimulator(): OpenSimulatorPlugin =
//            object : OpenSimulatorPlugin {
//                override fun getBridgePlugin(pluginContext: PluginContext): OpenBridgePlugin? =
//                    SoundAnalysisBridgePlugin(createServerSoundAnalyzer(pluginContext))
//
//                override fun getServerPlugin(pluginContext: PluginContext, bridgeClient: BridgeClient) =
//                    SoundAnalysisPlugin(
//                        PubSubSoundAnalysisData(
//                            PubSubPublisherz<SoundAnalysis>(
//                                PubSubSoundAnalyzer
//                            )
//                        )
//                    )
//
//                override fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin {
//                    TODO("not implemented")
//                }
//            }

        private val inputsTopic = PubSub.Topic("plugins/${id}/inputs", ListSerializer(AudioInput.serializer()))
        private val currentInputTopic = PubSub.Topic("plugins/${id}/currentInput", AudioInput.serializer().nullable)
        private val magnitudesTopic = PubSub.Topic("plugins/${id}/magnitudes", FloatArraySerializer())
        private val frequenciesTopic = PubSub.Topic("plugins/${id}/frequencies", AnalysisData.serializer())
    }

    @Serializable
    class AnalysisData(val frequencies: FloatArray, val timestamp: Time)

    class Args(parser: ArgParser) {
        val audioInput by parser.option(ArgType.String, description = "Audio input for spectral analysys")
    }

    class PubSubPublisher(
        soundAnalyzer: SoundAnalyzer,
        pluginContext: PluginContext
    ) {
        private var audioInputs: List<AudioInput> = soundAnalyzer.listAudioInputs()

        private val pubSub = pluginContext.pubSub

        val inputsChannel = pubSub.openChannel(inputsTopic, audioInputs) { audioInputs = it }
        val magnitudesChannel = pubSub.openChannel(magnitudesTopic, floatArrayOf()) {}
        val frequenciesChannel = pubSub.openChannel(frequenciesTopic, AnalysisData(floatArrayOf(), 0.0)) { }

        init {
            soundAnalyzer.listen { analysis: SoundAnalyzer.Analysis ->
                magnitudesChannel.onChange(analysis.magnitudes)
                frequenciesChannel.onChange(AnalysisData(analysis.frequencies, analysis.timestamp))
            }
        }
    }

    class PubSubSubscriber(pubSub: PubSub.Endpoint) : SoundAnalyzer {
        override val currentAudioInput: AudioInput? = null
        private var audioInputs: List<AudioInput> = emptyList()
        private var magnitudes: FloatArray? = null
        private var frequencies: FloatArray? = null
        private val listeners = mutableSetOf<SoundAnalyzer.AnalysisListener>()
        private val inputsListeners = mutableSetOf<SoundAnalyzer.InputsListener>()

        init {
            pubSub.openChannel(inputsTopic, audioInputs) { audioInputs = it }
            pubSub.openChannel(magnitudesTopic, floatArrayOf()) {
                magnitudes = it
            }
            pubSub.openChannel(frequenciesTopic, AnalysisData(floatArrayOf(), 0.0)) {
                frequencies = it.frequencies
                val analysis = SoundAnalyzer.Analysis(it.frequencies, magnitudes!!, it.timestamp)
                listeners.forEach { it.onSample(analysis) }
            }
        }

        override fun listAudioInputs(): List<AudioInput> = audioInputs

        override fun switchTo(audioInput: AudioInput?) {
            TODO("not implemented")
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
        analysis.magnitudes.forEachIndexed { index, magitude ->
            textureBuffer[index] = magitude * bucketCount
        }
    }

    override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
        private val textureUnit = gl.getTextureUnit(this)
        private val texture = gl.check { createTexture() }

        override fun bind(glslProgram: GlslProgram): ProgramFeed = object : ProgramFeed {
            val bucketCountUniform = glslProgram.getUniform("${varPrefix}.bucketCount")
            val sampleHistoryCountUniform = glslProgram.getUniform("${varPrefix}.sampleHistoryCount")
            val bucketsUniform = glslProgram.getUniform("${varPrefix}.buckets")

            override val isValid: Boolean
                get() = bucketCountUniform != null ||
                        sampleHistoryCountUniform != null ||
                        bucketsUniform != null

            override fun setOnProgram() {
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
}

@Serializable
data class AudioInput(
    val id: String,
    val title: String
)

internal expect fun createServerSoundAnalyzer(pluginContext: PluginContext): SoundAnalyzer
