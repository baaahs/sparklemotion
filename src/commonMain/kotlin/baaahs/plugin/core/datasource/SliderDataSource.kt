package baaahs.plugin.core.datasource

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.control.MutableSliderControl
import baaahs.gadgets.Slider
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.data.SingleUniformFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
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
data class SliderDataSource(
    @SerialName("title")
    val sliderTitle: String,
    val initialValue: Float,
    val minValue: Float,
    val maxValue: Float,
    val stepValue: Float? = null
) : DataSource {
    override val title: String get() = "$sliderTitle Slider"

    override fun buildControl(): MutableControl {
        return MutableSliderControl(sliderTitle, initialValue, minValue, maxValue, stepValue, this)
    }

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
//        val channel = showPlayer.useChannel<Float>(id)
        val slider = showPlayer.useGadget(this)
            ?: run {
                logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                createGadget()
            }

        return object : Feed, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    return SingleUniformFeed(glslProgram, this@SliderDataSource, id) { uniform ->
//                        uniform.set(channel.value)
                        uniform.set(slider.position)
                    }
                }
            }

            override fun release() = Unit
        }
    }

    companion object : DataSourceBuilder<SliderDataSource> {
        override val resourceName: String get() = "Slider"
        override val contentType: ContentType get() = ContentType.Float
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort): Boolean =
            inputPort.dataTypeIs(GlslType.Float)

        override fun build(inputPort: InputPort): SliderDataSource {
            val config = inputPort.pluginConfig
            return SliderDataSource(
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

        private val logger = Logger<SliderDataSource>()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType
        get() = ContentType.Float
    override fun suggestId(): String = title.camelize()

    fun createGadget(): Slider =
        Slider(sliderTitle, initialValue, minValue, maxValue, stepValue)
}