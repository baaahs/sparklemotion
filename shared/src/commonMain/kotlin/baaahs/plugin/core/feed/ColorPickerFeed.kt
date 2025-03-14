package baaahs.plugin.core.feed

import baaahs.Color
import baaahs.control.MutableColorPickerControl
import baaahs.gadgets.ColorPicker
import baaahs.gl.data.FeedContext
import baaahs.gl.data.singleUniformFeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.PluginRef
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.show.mutable.MutableControl
import baaahs.util.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:ColorPicker")
data class ColorPickerFeed(
    @SerialName("title")
    val colorPickerTitle: String,
    val initialValue: Color
) : Feed {
    override val title: String get() = "$colorPickerTitle Color Picker"

    override fun buildControl(): MutableControl {
        return MutableColorPickerControl(colorPickerTitle, initialValue, this)
    }

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
//        val channel = showPlayer.useChannel<Float>(id)
        val colorPicker = feedOpenContext.useGadget(this)
            ?: feedOpenContext.useGadget(id)
            ?: run {
                logger.debug { "No control gadget registered for feed $id, creating one. This is probably busted." }
                createGadget()
            }

        return singleUniformFeedContext<Color>(id) { colorPicker.color }
    }

    companion object : FeedBuilder<ColorPickerFeed> {
        override val title: String get() = "Color Picker"
        override val description: String get() = "A user-adjustable color picker."
        override val resourceName: String get() = "ColorPicker"
        override val contentType: ContentType get() = ContentType.Color
        override val serializerRegistrar get() = classSerializer(serializer())
        val pluginRef = PluginRef(CorePlugin.id, resourceName)

        override fun build(inputPort: InputPort): ColorPickerFeed {
            val default = inputPort.pluginConfig?.get("default")?.jsonPrimitive?.contentOrNull

            return ColorPickerFeed(
                inputPort.title,
                initialValue = default?.let { Color.from(it) } ?: Color.WHITE
            )
        }

        private val logger = Logger<ColorPickerFeed>()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Vec4
    override val contentType: ContentType
        get() = ContentType.Color

    fun createGadget(): ColorPicker = ColorPicker(colorPickerTitle, initialValue)
}