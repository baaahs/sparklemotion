package baaahs.plugin.core.datasource

import baaahs.Color
import baaahs.camelize
import baaahs.gadgets.ColorPicker
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.Uniform
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSourceBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:ColorPicker")
data class ColorPickerDataSource(
    @SerialName("title")
    override val gadgetTitle: String,
    val initialValue: Color
) : GadgetDataSource<ColorPicker> {
    companion object : DataSourceBuilder<ColorPickerDataSource> {
        override val resourceName: String get() = "ColorPicker"
        override val contentType: ContentType get() = ContentType.Color
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort): Boolean =
            inputPort.dataTypeIs(GlslType.Vec4)

        override fun build(inputPort: InputPort): ColorPickerDataSource {
            val default = inputPort.pluginConfig?.get("default")?.jsonPrimitive?.contentOrNull

            return ColorPickerDataSource(
                inputPort.title,
                initialValue = default?.let { Color.Companion.from(it) } ?: Color.WHITE
            )
        }
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "$gadgetTitle $resourceName"
    override fun getType(): GlslType = GlslType.Vec4
    override val contentType: ContentType
        get() = ContentType.Color
    override fun suggestId(): String = "$gadgetTitle Color Picker".camelize()

    override fun createGadget(): ColorPicker = ColorPicker(gadgetTitle, initialValue)

    override fun set(gadget: ColorPicker, uniform: Uniform) {
        val color = gadget.color
//            when (inputPortRef.type) {
//                GlslType.Vec3 -> uniform.set(color.redF, color.greenF, color.blueF)
//                GlslType.Vec4 ->
        uniform.set(color.redF, color.greenF, color.blueF, color.alphaF)
//            }
    }
}