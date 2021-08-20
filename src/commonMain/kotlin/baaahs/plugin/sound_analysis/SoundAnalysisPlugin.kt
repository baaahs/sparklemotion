package baaahs.plugin.sound_analysis

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
import baaahs.plugin.Plugin
import baaahs.plugin.PluginBuilder
import baaahs.plugin.PluginContext
import baaahs.plugin.objectSerializer
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import com.danielgergely.kgl.*
import kotlinx.serialization.SerialName

class SoundAnalysisPlugin internal constructor(
    val soundAnalyzer: SoundAnalyzer,
    val historySize: Int = 300
) : Plugin {

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

    companion object {
        val id = "baaahs.SoundAnalysis"

        val soundAnalysisStruct = GlslType.Struct(
            "SoundAnalysis",
            "bucketCount" to GlslType.Int,
            "sampleHistoryCount" to GlslType.Int,
            "buckets" to GlslType.Sampler2D
        )

        val soundAnalysisContentType = ContentType("sound-analysis", "Sound Analysis", soundAnalysisStruct)
    }

    class SoundAnalysisPluginBuilder(internal val soundAnalyzer: SoundAnalyzer) : PluginBuilder {
        override val id: String
            get() = SoundAnalysisPlugin.id

        override fun build(pluginContext: PluginContext): Plugin {
            return SoundAnalysisPlugin(soundAnalyzer)
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

    override fun release() = super.release()

    override fun onFullRelease() {
        soundAnalyzer.unlisten(this)
    }
}