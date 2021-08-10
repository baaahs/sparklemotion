package baaahs.plugin.core.datasource

import baaahs.*
import baaahs.control.MutableColorPickerControl
import baaahs.gadgets.ColorPicker
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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:ColorPicker")
data class ColorPickerDataSource(
    @SerialName("title")
    val colorPickerTitle: String,
    val initialValue: Color
) : DataSource {
    override val title: String get() = "$colorPickerTitle Color Picker"

    override fun buildControl(): MutableControl {
        return MutableColorPickerControl(colorPickerTitle, initialValue, this)
    }

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
//        val channel = showPlayer.useChannel<Float>(id)
        val colorPicker = showPlayer.useGadget(this)
            ?: run {
                logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                createGadget()
            }

        return object : Feed, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    return SingleUniformFeed(glslProgram, this@ColorPickerDataSource, id) { uniform ->
                        val color = colorPicker.color
                        uniform.set(color.redF, color.greenF, color.blueF, color.alphaF)
                    }
                }
            }

            override fun release() = Unit
        }
    }

    companion object : DataSourceBuilder<ColorPickerDataSource> {
        override val resourceName: String get() = "ColorPicker"
        override val contentType: ContentType get() = ContentType.Color
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): ColorPickerDataSource {
            val default = inputPort.pluginConfig?.get("default")?.jsonPrimitive?.contentOrNull

            return ColorPickerDataSource(
                inputPort.title,
                initialValue = default?.let { Color.Companion.from(it) } ?: Color.WHITE
            )
        }

        private val logger = Logger<ColorPickerDataSource>()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Vec4
    override val contentType: ContentType
        get() = ContentType.Color
    override fun suggestId(): String = title.camelize()

    fun createGadget(): ColorPicker = ColorPicker(colorPickerTitle, initialValue)
}