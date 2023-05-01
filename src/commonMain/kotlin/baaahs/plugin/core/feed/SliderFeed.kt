package baaahs.plugin.core.feed

import baaahs.ShowPlayer
import baaahs.control.MutableSliderControl
import baaahs.gadgets.Slider
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.data.SingleUniformFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.mutable.MutableControl
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:Slider")
data class SliderFeed(
    @SerialName("title")
    val sliderTitle: String,
    val initialValue: Float,
    val minValue: Float,
    val maxValue: Float,
    val stepValue: Float? = null
) : Feed {
    override val title: String get() = "$sliderTitle Slider"
    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType
        get() = ContentType.Float

    fun createGadget(): Slider =
        Slider(sliderTitle, initialValue, minValue, maxValue, stepValue)

    override fun buildControl(): MutableControl =
        MutableSliderControl(sliderTitle, initialValue, minValue, maxValue, stepValue, this)

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        val clock = showPlayer.toolchain.plugins.pluginContext.clock
//        val channel = showPlayer.useChannel<Float>(id)
        val slider = showPlayer.useGadget(this)
            ?: showPlayer.useGadget(id)
            ?: run {
                logger.debug { "No control gadget registered for feed $id, creating one. This is probably busted." }
                createGadget()
            }

        return object : FeedContext, RefCounted by RefCounter() {
            val plugin = showPlayer.toolchain.plugins.findPlugin<BeatLinkPlugin>()
            val beatSource = plugin?.beatSource

            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    return SingleUniformFeedContext(glslProgram, this@SliderFeed, id) { uniform ->
                        val position = tryFromBeatSource()
                            ?: tryFromSliding()
                            ?: slider.position
                        uniform.set(position)
                    }
                }

                private fun tryFromBeatSource(): Float? {
                    if (beatSource != null && slider.beatLinked) {
                        val beatData = beatSource.getBeatData()
                        if (beatData.confidence > .2f) {
                            return (slider.position - slider.floor) *
                                    beatData.fractionTillNextBeat(clock) +
                                    slider.floor
                        }
                    }
                    return null
                }

                private fun tryFromSliding(): Float? {
                    slider.sliding?.let {
                        val now = showPlayer.toolchain.plugins.pluginContext.clock.now()
                        return it.getCurrentPosition(now)
                    }
                    return null
                }
            }
        }
    }

    companion object : FeedBuilder<SliderFeed> {
        override val title: String get() = "Slider"
        override val description: String get() = "A user-adjustable slider."
        override val resourceName: String get() = "Slider"
        override val contentType: ContentType get() = ContentType.Float
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): SliderFeed {
            val config = inputPort.pluginConfig
            return SliderFeed(
                inputPort.title,
                initialValue = config.getFloat("default") ?: 1f,
                minValue = config.getFloat("min") ?: 0f,
                maxValue = config.getFloat("max") ?: 1f,
                stepValue = config.getFloat("step")
            )
        }

        private fun JsonObject?.getFloat(key: String): Float? {
            return try {
                this?.get(key)?.jsonPrimitive?.float
            } catch (e: NumberFormatException) {
                logger.debug(e) {
                    "Invalid number for key \"$key\": ${this?.get(key)?.jsonPrimitive?.contentOrNull}"
                }
                null
            }
        }

        private val logger = Logger<SliderFeed>()
    }
}