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
//        gadgets[id]?.let { clientGadget -> TODO() }

        val listener: GadgetListener = {
            gadgets.getBang(id, "client gadget").channel.onChange(it.state)
        }
        gadget.listen(listener)

        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.subscribe(topic) { json ->
            gadget.withoutTriggering(listener) {
                gadget.state.putAll(json)
                gadget.changed()
            }
        }
        gadgets[id] = ClientGadget(gadget, channel)
        controlledDataSource?.let { dataSourceGadgets[controlledDataSource] = gadget }
        println("ClientShowPlayer: gadget.title = ${gadget.title} registered for ${controlledDataSource?.dataSourceName}")
    }

    override fun <T : Gadget> useGadget(id: String): T {
        return gadgets[id]!!.gadget as T
    }

    class ClientGadget(
        val gadget: Gadget,
        val channel: PubSub.Channel<Map<String, JsonElement>>
    )
}