package baaahs.sm.server

import baaahs.Gadget
import baaahs.GadgetDataSerializer
import baaahs.PubSub
import baaahs.getBang
import baaahs.sm.webapi.ShowControlCommands
import baaahs.util.Clock
import kotlinx.serialization.json.JsonElement
import kotlin.coroutines.CoroutineContext

class GadgetManager(
    private val pubSub: PubSub.Server,
    private val clock: Clock,
    private val coroutineContext: CoroutineContext
) {
    private val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    var lastUserInteraction = clock.now()

    init {
        ShowControlCommands.IMPL
            .createReceiver(pubSub, object : ShowControlCommands {
                override suspend fun setGadgetState(id: String, state: Map<String, JsonElement>) {
                    gadgets.getBang(id, "gadget")
                        .applyState(state)
                }
            })
    }

    fun <T : Gadget> registerGadget(id: String, gadget: T) {
        gadgets[id] = gadget

        val topic = PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            lastUserInteraction = clock.now()
            gadget.applyState(updated)
        }
        gadget.listen { channel.onChange(it.state) }
        gadgets[id] = gadget
    }

    fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return (gadgets[id]
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }
}