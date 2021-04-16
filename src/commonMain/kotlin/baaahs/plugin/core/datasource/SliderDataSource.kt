package baaahs.plugin.core.datasource

import baaahs.camelize
import baaahs.gadgets.Slider
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.Uniform
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSourceBuilder
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
    override val gadgetTitle: String,
    val initialValue: Float,
    val minValue: Float,
    val maxValue: Float,
    val stepValue: Float? = null
) : GadgetDataSource<Slider> {
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
    override val title: String get() = "$gadgetTitle $resourceName"
    override fun getType(): GlslType = GlslType.Float
    override val contentType: ContentType
        get() = ContentType.Float
    override fun suggestId(): String = "$gadgetTitle Slider".camelize()

    override fun createGadget(): Slider =
        Slider(gadgetTitle, initialValue, minValue, maxValue, stepValue)

    override fun set(gadget: Slider, uniform: Uniform) {
        uniform.set(gadget.position)
    }
}