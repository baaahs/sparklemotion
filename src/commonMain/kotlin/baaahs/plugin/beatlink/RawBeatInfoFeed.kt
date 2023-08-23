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
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import baaahs.util.makeSafeForGlsl
import kotlinx.serialization.SerialName

@SerialName("baaahs.BeatLink:RawBeatInfo")
class RawBeatInfoFeed internal constructor(
    private val facade: BeatLinkPlugin.BeatLinkFacade
) : Feed {
    override val pluginPackage: String get() = BeatLinkPlugin.id
    override val title: String get() = "RawBeatInfo"
    override fun getType(): GlslType = BeatLinkPlugin.rawBeatInfoStruct
    override val contentType: ContentType
        get() = BeatLinkPlugin.rawBeatInfoContentType

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        val varPrefix = getVarName(id)
        return object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    return object : ProgramFeedContext {
                        val measureStartTime = glslProgram.getFloatUniform("${varPrefix}.measureStartTime")
                        val beatIntervalMsUniform = glslProgram.getFloatUniform("${varPrefix}.beatIntervalMs")
                        val bpmUniform = glslProgram.getFloatUniform("${varPrefix}.bpm")
                        val beatsPerMeasureUniform = glslProgram.getFloatUniform("${varPrefix}.beatsPerMeasure")
                        val confidenceUniform = glslProgram.getFloatUniform("${varPrefix}.confidence")

                        override val isValid: Boolean
                            get() = measureStartTime != null ||
                                    beatIntervalMsUniform != null ||
                                    bpmUniform != null ||
                                    beatsPerMeasureUniform != null ||
                                    confidenceUniform != null

                        override fun setOnProgram() {
                            val beatData = facade.beatData

                            measureStartTime?.set(beatData.measureStartTime.makeSafeForGlsl())
                            beatIntervalMsUniform?.set(beatData.beatIntervalMs.toFloat())
                            bpmUniform?.set(beatData.bpm)
                            beatsPerMeasureUniform?.set(beatData.beatsPerMeasure.toFloat())
                            confidenceUniform?.set(beatData.confidence)
                        }
                    }
                }
            }
        }
    }

    inner class Builder : FeedBuilder<RawBeatInfoFeed> {
        override val title: String get() = "Raw Beat Data"
        override val description: String get() = "A struct containing low-level information about the beat."
        override val resourceName: String get() = "RawBeatInfo"
        override val contentType: ContentType get() = BeatLinkPlugin.rawBeatInfoContentType
        override val serializerRegistrar
            get() = objectSerializer("${BeatLinkPlugin.id}:RawBeatInfo", this@RawBeatInfoFeed)
        override val internalOnly: Boolean
            get() = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == BeatLinkPlugin.rawBeatInfoContentType
                    || suggestedContentTypes.contains(BeatLinkPlugin.rawBeatInfoContentType)
                    || inputPort.type == BeatLinkPlugin.rawBeatInfoStruct

        override fun build(inputPort: InputPort): RawBeatInfoFeed = this@RawBeatInfoFeed
    }
}