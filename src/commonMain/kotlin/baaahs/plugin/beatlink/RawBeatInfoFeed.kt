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
    override fun getType(): GlslType = struct
    override val contentType: ContentType
        get() = RawBeatInfoFeed.contentType

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
                        val trackStartTimeUniform = glslProgram.getFloatUniform("${varPrefix}.trackStartTime")

                        override val isValid: Boolean
                            get() = measureStartTime != null ||
                                    beatIntervalMsUniform != null ||
                                    bpmUniform != null ||
                                    beatsPerMeasureUniform != null ||
                                    confidenceUniform != null ||
                                    trackStartTimeUniform != null

                        override fun setOnProgram() {
                            val beatData = facade.beatData

                            measureStartTime?.set(beatData.measureStartTime.makeSafeForGlsl())
                            beatIntervalMsUniform?.set(beatData.beatIntervalMs.toFloat())
                            bpmUniform?.set(beatData.bpm)
                            beatsPerMeasureUniform?.set(beatData.beatsPerMeasure.toFloat())
                            confidenceUniform?.set(beatData.confidence)
                            trackStartTimeUniform?.set(beatData.trackStartTime?.toFloat() ?: -1.0f)
                        }
                    }
                }
            }
        }
    }

    companion object {
        val struct = GlslType.Struct(
            "RawBeatInfo",
            "measureStartTime" to GlslType.Float,
            "beatIntervalMs" to GlslType.Float,
            "bpm" to GlslType.Float,
            "beatsPerMeasure" to GlslType.Float,
            "confidence" to GlslType.Float,
            "trackStartTime" to GlslType.Float
        )

        val contentType = ContentType("raw-beat-info", "Raw Beat Info", struct)
    }

    inner class Builder : FeedBuilder<RawBeatInfoFeed> {
        override val title: String get() = "Raw Beat Data"
        override val description: String get() = "A struct containing low-level information about the beat."
        override val resourceName: String get() = "RawBeatInfo"
        override val contentType: ContentType get() = RawBeatInfoFeed.contentType
        override val serializerRegistrar
            get() = objectSerializer("${BeatLinkPlugin.id}:$resourceName", this@RawBeatInfoFeed)
        override val internalOnly: Boolean
            get() = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == contentType
                    || suggestedContentTypes.contains(contentType)
                    || inputPort.type == struct

        override fun build(inputPort: InputPort): RawBeatInfoFeed = this@RawBeatInfoFeed
    }
}