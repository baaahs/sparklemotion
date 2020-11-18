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
import baaahs.show.*
import baaahs.show.mutable.MutableDataSourcePort
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.*

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

    override fun resolveDataSource(inputPort: InputPort): DataSource {
        return beatLinkDataSource
    }

    override fun suggestContentTypes(inputPort: InputPort): Collection<ContentType> {
        val glslType = inputPort.type
        val isStream = inputPort.glslVar?.isVarying ?: false
        return if (glslType == GlslType.Float && !isStream)
            listOf(beatDataContentType)
        else
            emptyList()
    }

    override fun resolveContentType(type: String): ContentType? {
        return when (type) {
            "beat-link" -> beatDataContentType
            else -> null
        }
    }

    override fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType>
    ): List<PortLinkOption> {
        if ((inputPort.contentType == beatDataContentType
                    || suggestedContentTypes.contains(beatDataContentType))
            || (inputPort.type == GlslType.Float && inputPort.glslVar?.isVarying != true)
        ) {
            return listOf(
                PortLinkOption(
                    MutableDataSourcePort(beatLinkDataSource),
                    wasPurposeBuilt = true,
                    isExactContentType = inputPort.contentType == beatDataContentType,
                    isPluginSuggestion = true
                )
            )
        } else {
            return emptyList()
        }
    }

    override fun findDataSource(resourceName: String, inputPort: InputPort): DataSource? {
        TODO("Not yet implemented")
    }

    override fun getAddControlMenuItems(): List<AddControlMenuItem> = listOf(
        AddControlMenuItem("New BeatLink Control…", CommonIcons.BeatLinkControl) { mutableShow ->
            MutableBeatLinkControl()
        }
    )

    override fun getControlSerializers() =
        listOf(
            classSerializer(BeatLinkControl.serializer())
        )

    override fun getDataSourceSerializers() =
        listOf(
            objectSerializer("baaahs.BeatLink:BeatLink", beatLinkDataSource)
        )

    @SerialName("baaahs.BeatLink:BeatLink")
    inner class BeatLinkDataSource internal constructor() : DataSource {
        override val pluginPackage: String get() = id
        override val title: String get() = "BeatLink"
        override fun getType(): GlslType = GlslType.Float
        override fun getContentType(): ContentType = beatDataContentType

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

    companion object : PluginBuilder<BeatLinkPluginArgs> {
        override val id = "baaahs.BeatLink"
        val beatDataContentType = ContentType("Beat Link", GlslType.Float)

        override fun createArgs(argsProvider: ArgsProvider): PluginArgs {
            return provideArgs(argsProvider)
        }
    }
}

interface BeatLinkPluginArgs : PluginArgs {
    val enableBeatLink: Boolean

    override fun createPlugin(pluginContext: PluginContext): Plugin {
        val beatSource = when(enableBeatLink) {
            true -> when (pluginContext.mode) {
                PluginMode.Client -> PubSubBeatSource(pluginContext, this)
                PluginMode.Server -> createPlatformBeatSource(pluginContext, this)
            }
            false -> {
                BeatSource.None
            }
        }

        return BeatLinkPlugin(beatSource, pluginContext)
    }
}

expect fun provideArgs(argsProvider: ArgsProvider): BeatLinkPluginArgs

expect fun createPlatformBeatSource(pluginContext: PluginContext, args: BeatLinkPluginArgs): BeatSource

