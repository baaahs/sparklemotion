package baaahs.client

import baaahs.*
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import kotlinx.serialization.json.JsonElement

class ClientShowResources(
    plugins: Plugins,
    override val glslContext: GlslContext,
    private val pubSub: PubSub.Client
) : BaseShowResources(plugins) {
    private val gadgets: MutableMap<String, ClientGadget> = mutableMapOf()

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
//        gadgets[id]?.let { clientGadget -> TODO() }

        val listener: GadgetListener = {
            val observer = gadgets.getBang(id, "client gadget").channel
            if (observer == null) {
                println("Huh, no observer for $id; discarding update (know about ${gadgets.keys})")
            } else {
                observer.onChange(it.state)
            }
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
    }

    override fun <T : Gadget> useGadget(id: String): T {
        return gadgets[id]!!.gadget as T
    }

    class ClientGadget(
        val gadget: Gadget,
        val channel: PubSub.Channel<Map<String, JsonElement>>
    )
}