package baaahs.plugin.core.feed

import baaahs.control.MutableSliderControl
import baaahs.gadgets.Slider
import baaahs.gl.data.FeedContext
import baaahs.gl.data.singleUniformFeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.show.mutable.MutableControl
import baaahs.util.Logger
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

    fun createGadget(title: String? = null): Slider =
        Slider(title ?: sliderTitle, initialValue, minValue, maxValue, stepValue)

    override fun buildControl(): MutableControl =
        MutableSliderControl(sliderTitle, initialValue, minValue, maxValue, stepValue, this, emptyList())

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        val clock = feedOpenContext.clock
//        val channel = showPlayer.useChannel<Float>(id)
        val slider = feedOpenContext.useGadget(this)
            ?: feedOpenContext.useGadget(id)
            ?: run {
                logger.debug { "No control gadget registered for feed $id, creating one. This is probably busted." }
                createGadget()
            }

        val plugin = feedOpenContext.plugins.findPlugin<BeatLinkPlugin>()
        val beatLink = plugin?.facade

        return singleUniformFeedContext<Float>(id) {
            if (beatLink != null && slider.beatLinked) {
                val beatData = beatLink.beatData
                if (beatData.confidence > .2f) {
                    (slider.position - slider.floor) *
                            beatData.fractionTillNextBeat(clock) +
                            slider.floor
                } else slider.position
            } else slider.position
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