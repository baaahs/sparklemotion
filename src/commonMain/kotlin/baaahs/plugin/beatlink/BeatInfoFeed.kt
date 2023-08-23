package baaahs.plugin.beatlink

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.objectSerializer
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.util.Clock
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName

@SerialName("baaahs.BeatLink:BeatInfo")
class BeatInfoFeed internal constructor(
    private val facade: BeatLinkPlugin.BeatLinkFacade,
    private val clock: Clock
) : Feed {
    override val pluginPackage: String get() = BeatLinkPlugin.id
    override val title: String get() = "BeatInfo"
    override fun getType(): GlslType = BeatLinkPlugin.beatInfoStruct
    override val contentType: ContentType
        get() = BeatLinkPlugin.beatInfoContentType

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        val varPrefix = getVarName(id)
        return object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    return object : ProgramFeedContext {
                        val beatUniform = glslProgram.getFloatUniform("${varPrefix}.beat")
                        val bpmUniform = glslProgram.getFloatUniform("${varPrefix}.bpm")
                        val intensityUniform = glslProgram.getFloatUniform("${varPrefix}.intensity")
                        val confidenceUniform = glslProgram.getFloatUniform("${varPrefix}.confidence")

                        override val isValid: Boolean
                            get() = beatUniform != null ||
                                    bpmUniform != null ||
                                    intensityUniform != null ||
                                    confidenceUniform != null

                        override fun setOnProgram() {
                            val beatData = facade.beatData

                            beatUniform?.set(beatData.beatWithinMeasure(clock))
                            bpmUniform?.set(beatData.bpm)
                            intensityUniform?.set(beatData.fractionTillNextBeat(clock))
                            confidenceUniform?.set(beatData.confidence)
                        }
                    }
                }
            }
        }
    }

    inner class Builder : FeedBuilder<BeatInfoFeed> {
        override val title: String get() = "Beat Info"
        override val description: String get() = "A struct containing information about the beat."
        override val resourceName: String get() = "BeatInfo"
        override val contentType: ContentType get() = BeatLinkPlugin.beatInfoContentType
        override val serializerRegistrar
            get() = objectSerializer("${BeatLinkPlugin.id}:BeatInfo", this@BeatInfoFeed)

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == BeatLinkPlugin.beatInfoContentType
                    || suggestedContentTypes.contains(BeatLinkPlugin.beatInfoContentType)
                    || inputPort.type == BeatLinkPlugin.beatInfoStruct

        override fun build(inputPort: InputPort): BeatInfoFeed = this@BeatInfoFeed
    }
}