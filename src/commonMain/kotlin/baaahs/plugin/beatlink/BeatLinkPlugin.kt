package baaahs.plugin.beatlink

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.data.SingleUniformFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.*
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.mutable.MutableDataSourcePort
import kotlinx.serialization.SerialName

class BeatLinkPlugin internal constructor(
    internal val beatSource: BeatSource,
    pluginContext: PluginContext
) : Plugin {
    override val packageName: String = id
    override val title: String = "Beat Link"

    private val clock = pluginContext.clock

    // We'll just make one up-front. We only ever want one (because equality
    // is using object identity), and there's no overhead.
    internal val beatLinkDataSource = BeatLinkDataSource()
    internal val beatInfoDataSource = BeatInfoDataSource()

    override val addControlMenuItems: List<AddControlMenuItem>
        get() = listOf(
            AddControlMenuItem("New BeatLink Control…", CommonIcons.BeatLinkControl) { mutableShow ->
                MutableBeatLinkControl()
            }
        )
    override val contentTypes: List<ContentType>
        get() = listOf(
            beatDataContentType,
            beatInfoContentType
        )

    override val controlSerializers
        get() = listOf(
            classSerializer(BeatLinkControl.serializer())
        )

    override val dataSourceBuilders: List<DataSourceBuilder<out DataSource>>
        get() = listOf(
            object : DataSourceBuilder<BeatLinkDataSource> {
                override val resourceName: String get() = "BeatLink"
                override val contentType: ContentType get() = beatDataContentType
                override val serializerRegistrar get() =
                    objectSerializer("baaahs.BeatLink:BeatLink", beatLinkDataSource)

                override fun suggestDataSources(
                    inputPort: InputPort,
                    suggestedContentTypes: Set<ContentType>
                ): List<PortLinkOption> {
                    return if (inputPort.contentType == beatDataContentType
                        || suggestedContentTypes.contains(beatDataContentType)
                        || inputPort.type == GlslType.Float
                    ) {
                        listOf(
                            PortLinkOption(
                                MutableDataSourcePort(beatLinkDataSource),
                                wasPurposeBuilt = true,
                                isExactContentType = inputPort.contentType == beatDataContentType
                                        || inputPort.contentType.isUnknown(),
                                isPluginSuggestion = true
                            )
                        )
                    } else emptyList()
                }

                override fun build(inputPort: InputPort): BeatLinkDataSource = beatLinkDataSource
            },
            object : DataSourceBuilder<BeatInfoDataSource> {
                override val resourceName: String get() = "BeatInfo"
                override val contentType: ContentType get() = beatDataContentType
                override val serializerRegistrar get() =
                    objectSerializer("baaahs.BeatLink:BeatInfo", beatInfoDataSource)

                override fun suggestDataSources(
                    inputPort: InputPort,
                    suggestedContentTypes: Set<ContentType>
                ): List<PortLinkOption> {
                    return if (inputPort.contentType == beatDataContentType
                        || suggestedContentTypes.contains(beatDataContentType)
                        || inputPort.type == beatInfoStruct
                    ) {
                        listOf(
                            PortLinkOption(
                                MutableDataSourcePort(beatInfoDataSource),
                                wasPurposeBuilt = true,
                                isExactContentType = inputPort.contentType == beatInfoContentType
                                        || inputPort.contentType.isUnknown(),
                                isPluginSuggestion = true
                            )
                        )
                    } else emptyList()
                }

                override fun build(inputPort: InputPort): BeatInfoDataSource = beatInfoDataSource
            }
        )

    @SerialName("baaahs.BeatLink:BeatLink")
    inner class BeatLinkDataSource internal constructor(): DataSource {
        override val pluginPackage: String get() = id
        override val title: String get() = "BeatLink"
        override fun getType(): GlslType = GlslType.Float
        override val contentType: ContentType
            get() = beatDataContentType

        override fun createFeed(showPlayer: ShowPlayer, id: String): baaahs.gl.data.Feed {
            return object : baaahs.gl.data.Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed =
                        SingleUniformFeed(glslProgram, this@BeatLinkDataSource, id) { uniform ->
                            uniform.set(beatSource.getBeatData().fractionTillNextBeat(clock))
                        }
                }

                override fun release() {
                    super.release()
                }
            }
        }
    }

    @SerialName("baaahs.BeatLink:BeatInfo")
    inner class BeatInfoDataSource internal constructor(): DataSource {
        override val pluginPackage: String get() = id
        override val title: String get() = "BeatInfo"
        override fun getType(): GlslType = beatInfoStruct
        override val contentType: ContentType
            get() = beatInfoContentType

        override fun createFeed(showPlayer: ShowPlayer, id: String): baaahs.gl.data.Feed {
            val varPrefix = getVarName(id)
            return object : baaahs.gl.data.Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed {
                        return object : ProgramFeed {
                            val beatUniform = glslProgram.getUniform("${varPrefix}.beat")
                            val bpmUniform = glslProgram.getUniform("${varPrefix}.bpm")
                            val intensityUniform = glslProgram.getUniform("${varPrefix}.intensity")
                            val confidenceUniform = glslProgram.getUniform("${varPrefix}.confidence")

                            override val isValid: Boolean
                                get() = beatUniform != null ||
                                        bpmUniform != null ||
                                        intensityUniform != null ||
                                        confidenceUniform != null

                            override fun setOnProgram() {
                                val beatData = beatSource.getBeatData()

                                beatUniform?.set(beatData.beatWithinMeasure(clock))
                                bpmUniform?.set(beatData.bpm)
                                intensityUniform?.set(beatData.fractionTillNextBeat(clock))
                                confidenceUniform?.set(beatData.confidence)
                            }
                        }
                    }
                }

                override fun release() {
                    super.release()
                }
            }
        }
    }

    companion object {
        val id = "baaahs.BeatLink"
        val beatDataContentType = ContentType("beat-link", "Beat Link", GlslType.Float)

        val beatInfoStruct = GlslType.Struct(
            "BeatInfo",
            "beat" to GlslType.Float,
            "bpm" to GlslType.Float,
            "intensity" to GlslType.Float,
            "confidence" to GlslType.Float
        )
        val beatInfoContentType = ContentType("beat-info", "Beat Info", beatInfoStruct)
    }

    class Builder(internal val beatSource: BeatSource) : PluginBuilder {
        override val id = BeatLinkPlugin.id

        override fun build(pluginContext: PluginContext): Plugin {
            return BeatLinkPlugin(beatSource, pluginContext)
        }
    }
}