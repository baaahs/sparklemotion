package baaahs.plugin.core.feed

import baaahs.gadgets.Select
import baaahs.gl.data.FeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:Select")
data class SelectFeed(
    override val title: String,
    val options: List<Pair<Int, String>>,
    val initialSelectionIndex: Int
) : Feed {
    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        TODO("not implemented")
    }

    companion object : FeedBuilder<SelectFeed> {
        override val title: String get() = "Radio Button Strip"
        override val description: String get() = "A user-adjustable button strip."
        override val resourceName: String get() = "Select"
        override val contentType: ContentType get() = ContentType.Int
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): SelectFeed {
            val config = inputPort.pluginConfig

            val initialSelectionIndex = config?.getValue("default")?.jsonPrimitive?.int ?: 0

            val options = config
                ?.let { it["options"]?.jsonArray }
                ?.mapIndexed { index, it -> index to it.jsonPrimitive.content }
                ?: error("no options given")

            return SelectFeed(
                inputPort.title,
                options,
                initialSelectionIndex
            )
        }
    }

    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Int
    override val contentType: ContentType
        get() = ContentType.Int

    fun createGadget(): Select {
        return Select(title, options, initialSelectionIndex)
    }
}