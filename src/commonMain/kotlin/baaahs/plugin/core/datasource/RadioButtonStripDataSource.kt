package baaahs.plugin.core.datasource

import baaahs.gadgets.RadioButtonStrip
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.Uniform
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSourceBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:RadioButtonStrip")
data class RadioButtonStripDataSource(
    @SerialName("title")
    override val gadgetTitle: String,
    val options: List<String>,
    val initialSelectionIndex: Int
) : GadgetDataSource<RadioButtonStrip> {
    companion object : DataSourceBuilder<RadioButtonStripDataSource> {
        override val resourceName: String get() = "Radio Button Strip"
        override val contentType: ContentType get() = ContentType.Int
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort): Boolean =
            inputPort.dataTypeIs(GlslType.Int)

        override fun build(inputPort: InputPort): RadioButtonStripDataSource {
            val config = inputPort.pluginConfig

            val initialSelectionIndex = config?.getValue("default")?.jsonPrimitive?.int ?: 0

            val options = config
                ?.let { it["options"]?.jsonArray }
                ?.map { it.jsonPrimitive.content }
                ?: error("no options given")

            return RadioButtonStripDataSource(
                inputPort.title,
                options,
                initialSelectionIndex
            )
        }
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = resourceName
    override fun getType(): GlslType = GlslType.Int
    override val contentType: ContentType
        get() = ContentType.Int

    override fun createGadget(): RadioButtonStrip {
        return RadioButtonStrip(gadgetTitle, options, initialSelectionIndex)
    }

    override fun set(gadget: RadioButtonStrip, uniform: Uniform) {
        TODO("not implemented")
    }
}