package baaahs.plugin.core.feed

import baaahs.control.MutableSelectControl
import baaahs.gadgets.Select
import baaahs.gl.data.FeedContext
import baaahs.gl.data.singleUniformFeedContext
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.show.mutable.MutableControl
import baaahs.util.Logger
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
    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Int
    override val contentType: ContentType
        get() = ContentType.Int

    fun createGadget(): Select =
        Select(title, options, initialSelectionIndex)

    override fun buildControl(): MutableControl? =
        MutableSelectControl(title, options.toMutableList(), initialSelectionIndex, this)

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        val select = feedOpenContext.useGadget(this)
            ?: feedOpenContext.useGadget(id)
            ?: run {
                logger.debug { "No control gadget registered for feed $id, creating one. This is probably busted." }
                createGadget()
            }
        return singleUniformFeedContext<Int>(id) {
            select.options[select.selectionIndex].first
        }
    }

    companion object : FeedBuilder<SelectFeed> {
        override val title: String get() = "Radio Button Strip"
        override val description: String get() = "A user-adjustable button strip."
        override val resourceName: String get() = "Select"
        override val contentType: ContentType get() = ContentType.Int
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): SelectFeed {
            val config = inputPort.pluginConfig
                ?: error("No config.")

            val labels = config["labels"]?.jsonArray
                ?.map { it.jsonPrimitive.content }
                ?: error("No labels given.")
            val values = config["values"]?.jsonArray
                ?.map { it.jsonPrimitive.int }
                ?: error("No label values given.")
            if (labels.size != values.size)
                error("Labels and values must be the same size.")

            val default = config["default"]?.jsonPrimitive?.int
            val initialSelectionIndex = values.indexOf(default)
                .let { if (it == -1) 0 else it }

            return SelectFeed(
                inputPort.title,
                labels.zip(values) { label, value -> value to label },
                initialSelectionIndex
            )
        }

        private val logger = Logger<SelectFeed>()
    }
}