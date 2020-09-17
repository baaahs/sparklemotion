package baaahs.client

import baaahs.*
import baaahs.gl.GlContext
import baaahs.model.ModelInfo
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import kotlinx.serialization.json.JsonElement

class ClientShowPlayer(
    plugins: Plugins,
    override val glContext: GlContext,
    private val pubSub: PubSub.Client,
    modelInfo: ModelInfo
) : BaseShowPlayer(plugins, modelInfo) {
    private val gadgets: MutableMap<String, ClientGadget> = mutableMapOf()

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        gadgets[id] = ClientGadget(id, pubSub, gadget)
        controlledDataSource?.let { dataSourceGadgets[controlledDataSource] = gadget }
    }

    override fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets.getBang(id, "gadget").gadget as T
    }

    private class ClientGadget(
        id: String,
        pubSub: PubSub.Client,
        val gadget: Gadget
    ) {
        private val channel: PubSub.Channel<Map<String, JsonElement>>

        init {
            val gadgetListener = this::onGadgetChange
            gadget.listen(gadgetListener)

            val topic =
                PubSub.Topic("/gadgets/$id", GadgetDataSerializer)

            channel = pubSub.subscribe(topic) { json ->
                gadget.withoutTriggering(gadgetListener) {
                    gadget.state.putAll(json)
                    gadget.changed()
                }
            }
        }

        // GadgetListener callback.
        fun onGadgetChange(g: Gadget) {
            channel.onChange(g.state)
        }
    }
}